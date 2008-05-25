package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.Config;
import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.RenderCommand;
import com.yellowbkpk.jtah.pipeline.command.TranslateCommand;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class TranslateOSMPipelineNode extends AbstractPipelineNode {

    public TranslateOSMPipelineNode(BlockingQueue<PipelineCommand> inputPipe, BlockingQueue<PipelineCommand> outputPipe) {
        super(inputPipe, outputPipe);
    }

    public void run() {
        while (true) {
            try {
                Object dequeue = getInputPipe().take();
                System.err.println("Translator dequeued " + dequeue);
                TranslateCommand comm = (TranslateCommand) dequeue;

                // Make sure the OSM data exists
                String filePath = comm.getFilePath();
                File osmFile = new File(filePath);
                System.err.println(osmFile + " " + osmFile.exists());

                // The zoom level that we start with
                int startZoomLevel = comm.getZoom();
                
                // This is the "bootstrap" xsl file
                File translationFile = new File(Config.OSMARENDER_BASE + "/osmarender.xsl");
                System.err.println(translationFile + " " + osmFile.exists());

                // Start at the zoom level that was requested and go higher
                // until MAX_ZOOM
                int imageSize = 256;
                for (int zoom = startZoomLevel; zoom <= Config.MAX_ZOOM; zoom++) {
                    File rulesFile = new File(Config.OSMARENDER_BASE + "/osm-map-features-z" + zoom + ".xml");
                    System.err.println(rulesFile + " " + rulesFile.exists());

                    // Add bounds and other required information to the rules
                    // template
                    StringBuilder b = new StringBuilder();
                    b.append("<bounds maxlat='");
                    NumberFormat f = new DecimalFormat("###.#####");
                    b.append(f.format(comm.getBoundingBox().getMinY()));
                    b.append("' minlon='");
                    b.append(f.format(comm.getBoundingBox().getMinX()));
                    b.append("' minlat='");
                    b.append(f.format(comm.getBoundingBox().getMaxY()));
                    b.append("' maxlon='");
                    b.append(f.format(comm.getBoundingBox().getMaxX()));
                    b.append("' />");
                    String boundsXMLLine = b.toString();

                    File tempRulesFile = new File(UUID.randomUUID() + ".xml");
                    BufferedReader ris = new BufferedReader(new FileReader(rulesFile));
                    BufferedWriter ros = new BufferedWriter(new FileWriter(tempRulesFile));
                    String line;
                    while ((line = ris.readLine()) != null) {
                        // Copy the line to the output
                        ros.write(line);
                        ros.write("\n");

                        // Look for the bounds marker
                        if (line.contains("<!--bounds_mkr1-->")) {
                            ros.write(boundsXMLLine);
                            ros.write("\n");
                        }
                    }
                    ros.close();

                    // Use XSLT to transform the file
                    TransformerFactory tFactory = TransformerFactory.newInstance();
                    Transformer transformer = tFactory.newTransformer(new StreamSource(translationFile));
                    transformer.setParameter("osmfile", "../" + osmFile.getName());
                    System.err.println("osmfile -> " + "../" + osmFile.getCanonicalPath());

                    File svgFile = new File(UUID.randomUUID() + ".svg");
                    System.err.println(svgFile + " " + svgFile.exists());
                    transformer.transform(new StreamSource(tempRulesFile), new StreamResult(new FileOutputStream(
                            svgFile)));

                    System.err.println("Translated " + imageSize + "px square image for zoom level " + zoom);
                    
                    // Create a render command
                    PipelineCommand renderCommand = new RenderCommand(comm.getBoundingBox(), svgFile, imageSize);
                    System.err.println("Translator enqueued " + renderCommand);
                    getOutputPipe().put(renderCommand);
                    
                    // After we send it to the queue, the next pass needs to be twice as wide/tall
                    imageSize = imageSize * 2;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }

}
