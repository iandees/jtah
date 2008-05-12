package com.yellowbkpk.jtah.pipeline;

import com.ge.medit.util.Conduit;

import com.sun.org.apache.xalan.internal.client.XSLTProcessorApplet;
import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.RenderCommand;
import com.yellowbkpk.jtah.pipeline.command.TranslateCommand;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
                File translationFile = new File("osmarender.xsl");
                System.err.println(translationFile + " " + osmFile.exists());
                File rulesFile = new File("osm-map-features-z12.xml");
                System.err.println(rulesFile + " " + rulesFile.exists());
                
                // Use XSLT to transform the file
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer(new StreamSource(translationFile));
                transformer.setParameter("osmfile", osmFile.getName());
                System.err.println("osmfile -> " + osmFile.getCanonicalPath());
                
                File svgFile = new File(UUID.randomUUID() + ".svg");
                System.err.println(svgFile + " " + svgFile.exists());
                transformer.transform(new StreamSource(rulesFile), new StreamResult(new FileOutputStream(svgFile)));

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
