package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.Config;
import com.yellowbkpk.jtah.pipeline.command.DataDownloadCommand;
import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.TranslateCommand;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class OSMDataDownloadPipelineNode implements PipelineNode {

    private static final double limitY = lat2y(Math.toDegrees(Math.atan(Math.sinh(Math.PI))));
    private static final double rangeY = 2.0 * limitY;
    private BlockingQueue<PipelineCommand> inputPipe;
    private BlockingQueue<PipelineCommand> outputPipe;

    /**
     * @param inputPipe
     * @param outputPipe
     */
    public OSMDataDownloadPipelineNode(BlockingQueue<PipelineCommand> inputPipe,
            BlockingQueue<PipelineCommand> outputPipe) {
        this.inputPipe = inputPipe;
        this.outputPipe = outputPipe;
    }

    public void run() {
        while(true) {
            try {
                Object dequeue = inputPipe.take();
                System.err.println("Downloder dequeued " + dequeue);
                DataDownloadCommand comm = (DataDownloadCommand) dequeue;
                
                // Build the bounding box
                Rectangle2D.Double boundingBox = project(comm.getX(), comm.getY(), comm.getZ());
                // bbox = w,s,e,n
                String bbox = stringifyBBox(boundingBox);
                
                // Make the request for the data to the server
                URL apiReqUrl = new URL(Config.OSM_BASE_URL + Config.OSM_VERSION + "/map?bbox=" + bbox);
                
                System.err.println("Downloading data from " + apiReqUrl);
                
                // Save the input stream
                URLConnection openConnection = apiReqUrl.openConnection();
                BufferedReader ir = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
                
                File tempFile = new File(UUID.randomUUID() + ".osm");
                String tempFilePath = tempFile.getAbsolutePath();
                BufferedWriter ow = new BufferedWriter(new FileWriter(tempFile));
                
                System.err.println("Saving OSM data to " + tempFilePath);
                
                String line;
                while((line = ir.readLine()) != null) {
                    ow.write(line);
                    ow.write("\n");
                }
                ir.close();
                ow.close();
                
                System.err.println("Done saving data.");
                
                // Create the translate command
                PipelineCommand translateCommand = new TranslateCommand(boundingBox, comm.getZ(), tempFilePath);
                
                // Stick it on the queue
                System.err.println("Downloader enqueued " + translateCommand);
                outputPipe.add(translateCommand);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String stringifyBBox(Double boundingBox) {
        StringBuilder b = new StringBuilder();
        
        NumberFormat f = new DecimalFormat("###.#####");
        b.append(f.format(boundingBox.getMinX()));
        b.append(",");
        b.append(f.format(boundingBox.getMaxY()));
        b.append(",");
        b.append(f.format(boundingBox.getMaxX()));
        b.append(",");
        b.append(f.format(boundingBox.getMinY()));
        
        return b.toString();
    }

    private Rectangle2D.Double project(int x, int y, int z) {
        Rectangle2D.Double result = new Rectangle2D.Double();
        
        // project tile y to latitude
        double unit = 1.0 / Math.pow(2, z);
        
        double relY1 = y * unit;
        double relY2 = relY1 + unit;
        
        relY1 = limitY - rangeY * relY1;
        relY2 = limitY - rangeY * relY2;
        
        double lat1 = projectMercToLat(relY1);
        double lat2 = projectMercToLat(relY2);
        
        result.y = lat1;
        result.height = lat2 - lat1;
        
        // project tile x to longitude
        unit = 360.0 / Math.pow(2, z);
        double long1 = -180.0 + x * unit;
        
        result.x = long1;
        result.width = unit;
        
        return result;
    }

    private double projectMercToLat(double relY1) {
        return Math.toDegrees(Math.atan(Math.sinh(relY1)));
    }

    private static double lat2y(double degrees) {
        double lat = Math.toRadians(degrees);
        double y = Math.log(Math.tan(lat) + (1.0 / Math.cos(lat)));
        return y;
    }

}
