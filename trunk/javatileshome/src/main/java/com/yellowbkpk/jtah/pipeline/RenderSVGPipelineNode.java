package com.yellowbkpk.jtah.pipeline;

import com.ge.medit.util.Conduit;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.RenderCommand;
import com.yellowbkpk.jtah.pipeline.command.SliceCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.UUID;

public class RenderSVGPipelineNode implements PipelineNode {

    private Conduit inputPipe;
    private Conduit outputPipe;

    public RenderSVGPipelineNode(Conduit inputPipe,
            Conduit outputPipe) {
        this.inputPipe = inputPipe;
        this.outputPipe = outputPipe;
    }

    public void run() {
        try {
            Object dequeue = inputPipe.dequeue();
            System.err.println("Renderer dequeued " + dequeue);
            RenderCommand comm = (RenderCommand) dequeue;
            
            // Get the SVG file
            String inputFileURL = comm.getFile().toURL().toString();
            File outputFile = new File(UUID.randomUUID() + ".png");
            OutputStream outputStream = new FileOutputStream(outputFile);
            
            // Render to PNG
            PNGTranscoder transcoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(inputFileURL);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            System.err.println("Transcoding " + inputFileURL + " to " + outputFile.toString());
            transcoder.transcode(input, output);
            outputStream.flush();
            outputStream.close();
            System.err.println("Done transcoding");
            
            // Enqueue a slicer job
            PipelineCommand sliceCommand = new SliceCommand(comm.getBoundingBox(), outputFile);
            outputPipe.enqueue(sliceCommand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TranscoderException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
