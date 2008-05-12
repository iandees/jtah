package com.yellowbkpk.jtah.pipeline;

import com.ge.medit.util.Conduit;

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

public class TranslateOSMPipelineNode implements PipelineNode {

    private Conduit inputPipe;
    private Conduit outputPipe;

    /**
     * @param inputPipe
     * @param outputPipe
     */
    public TranslateOSMPipelineNode(Conduit inputPipe,
            Conduit outputPipe) {
        this.inputPipe = inputPipe;
        this.outputPipe = outputPipe;
    }

    public void run() {
        while(true) {
            try {
                Object dequeue = inputPipe.dequeue();
                System.err.println("Translator dequeued " + dequeue);
                TranslateCommand comm = (TranslateCommand) dequeue;

                // Read the file
                String filePath = comm.getFilePath();
                File osmFile = new File(filePath);
                System.err.println(osmFile + " " + osmFile.exists());
                
                // Determine which rule set to use
                File translationFile = new File(Config.OSMARENDER_BASE + "/osmarender.xsl");
                System.err.println(translationFile + " " + osmFile.exists());
                File rulesFile = new File(Config.OSMARENDER_BASE + "/osm-map-features-z12.xml");
                System.err.println(rulesFile + " " + rulesFile.exists());
                
                // Add bounds and other required information to the rules template
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
                while((line = ris.readLine()) != null) {
                    // Copy the line to the output
                    ros.write(line);
                    ros.write("\n");
                    
                    // Look for the bounds marker
                    if(line.contains("<!--bounds_mkr1-->")) {
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
                transformer.transform(new StreamSource(tempRulesFile), new StreamResult(new FileOutputStream(svgFile)));

                // Create a render command
                PipelineCommand renderCommand = new RenderCommand(comm.getBoundingBox(), svgFile);

                // Stick it on the queue
                System.err.println("Translator enqueued " + renderCommand);
                outputPipe.enqueue(renderCommand);
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
