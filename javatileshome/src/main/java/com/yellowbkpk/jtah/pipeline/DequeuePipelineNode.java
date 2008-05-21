package com.yellowbkpk.jtah.pipeline;


import com.yellowbkpk.jtah.Config;
import com.yellowbkpk.jtah.pipeline.command.DataDownloadCommand;
import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;

public class DequeuePipelineNode implements PipelineNode {

    private BlockingQueue<PipelineCommand> outPipe;

    /**
     * @param outputPipe
     */
    public DequeuePipelineNode(BlockingQueue<PipelineCommand> outputPipe) {
        outPipe = outputPipe;
    }

    public void run() {
        while(true) {
            try {
                // Retrieve a job from the server
                URL dequeueServerURL = new URL(Config.SERVER_URL + "/jobs/dequeue?token=" + Config.USER_TOKEN);
                
                System.err.println("URL is " + dequeueServerURL);
                
                // Read the result of the HTTP request
                URLConnection connection = dequeueServerURL.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder b = new StringBuilder();
                String line;
                while((line = r.readLine()) != null) {
                    b.append(line);
                }
                r.close();
                
                line = b.toString().trim();
                
                // Create the download command
                String[] strings = line.split("\\|");
                if(strings.length == 6) {
                    int x = Integer.valueOf(strings[2]);
                    int y = Integer.valueOf(strings[3]);
                    int z = Integer.valueOf(strings[4]);
                    String layer = strings[5];
                    
                    System.err.println("X,Y,Z,layer = " + x + "," + y + "," + z + "," + layer);
                    
                    PipelineCommand command = new DataDownloadCommand(x, y, z, layer);
                    
                    // Enqueue the command
                    System.err.println("Job dequeuer enqueued " + command);
                    outPipe.put(command);
                } else {
                    System.err.println("Wrong return from server: " + line);
                }
                
                // Wait a while to do the next request.
                // TODO Should wait a certain amount depending on how long the output queue is.
                Thread.sleep(50000);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
