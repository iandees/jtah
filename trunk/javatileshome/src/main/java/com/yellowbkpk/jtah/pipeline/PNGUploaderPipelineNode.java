package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.Config;
import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.UploadCommand;
import com.yellowbkpk.jtah.util.MultiPartFormOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;

public class PNGUploaderPipelineNode extends AbstractPipelineNode {

    public PNGUploaderPipelineNode(BlockingQueue<PipelineCommand> inputPipe) {
        super(inputPipe, null);
    }

    public void run() {
        while(true) {
            try {
                Object dequeue = getInputPipe().take();
                System.err.println("Uploader dequeued " + dequeue);
                UploadCommand comm = (UploadCommand) dequeue;

                int tileX = comm.getTileX();
                int tileY = comm.getTileY();
                int tileZ = comm.getTileZ();
                String tileLayer = comm.getTileLayer();

                // Setup the URL for sending the images to the server
                URL url = new URL(Config.SERVER_URL + "/tiles/add");
                System.err.println("Uploading " + tileX + "," + tileY + " z" + tileZ + " layer " + tileLayer + " to " + url);
                String boundary = MultiPartFormOutputStream.createBoundary();
                URLConnection urlConn = MultiPartFormOutputStream.createConnection(url);
                urlConn.setRequestProperty("Accept", "*/*");
                urlConn.setRequestProperty("Content-Type", MultiPartFormOutputStream.getContentType(boundary));
                // Set some other request headers
                urlConn.setRequestProperty("Connection", "Keep-Alive");
                urlConn.setRequestProperty("Cache-Control", "no-cache");
                MultiPartFormOutputStream out = new MultiPartFormOutputStream(urlConn.getOutputStream(), boundary);
                // Write out the key data for the tile
                out.writeField("x", tileX);
                out.writeField("y", tileY);
                out.writeField("z", tileZ);
                out.writeField("layer", tileLayer);
                out.writeField("token", Config.USER_TOKEN);
                // Write out the tile image file
                out.writeFile("file", "image/png", comm.getOutFile());
                
                // Close the outbound connection
                out.close();
                System.err.println("Done uploading " + tileX + "," + tileY + " z" + tileZ + " layer " + tileLayer );

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String line;
                String response = "";
                while((line = in.readLine()) != null) {
                    response += line;
                }
                in.close();
                System.err.println(response);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
