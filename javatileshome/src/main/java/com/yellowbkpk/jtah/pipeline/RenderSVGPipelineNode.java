package com.yellowbkpk.jtah.pipeline;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.RenderCommand;
import com.yellowbkpk.jtah.pipeline.command.SplitterCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class RenderSVGPipelineNode implements PipelineNode {

    private BlockingQueue<PipelineCommand> inputPipe;
    private BlockingQueue<PipelineCommand> outputPipe;

    public RenderSVGPipelineNode(BlockingQueue<PipelineCommand> inputPipe,
            BlockingQueue<PipelineCommand> outputPipe) {
        this.inputPipe = inputPipe;
        this.outputPipe = outputPipe;
    }

    public void run() {
        while (true) {
            try {
                Object dequeue = inputPipe.take();
                System.err.println("Renderer dequeued " + dequeue);
                RenderCommand comm = (RenderCommand) dequeue;

                // Get the SVG file
                String inputFileURL = comm.getFile().toURL().toString();
                File outputFile = new File(UUID.randomUUID() + ".png");
                OutputStream outputStream = new FileOutputStream(outputFile);

                // Render to PNG
                int imgSize = comm.getImageSize();
                PNGTranscoder transcoder = new PNGTranscoder();

                transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(imgSize));
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(imgSize));

                TranscoderInput input = new TranscoderInput(inputFileURL);
                TranscoderOutput output = new TranscoderOutput(outputStream);

                System.err.println("Transcoding " + inputFileURL + " to " + outputFile.toString());
                transcoder.transcode(input, output);
                outputStream.flush();
                outputStream.close();
                System.err.println("Done transcoding");

                // Determine how many tiles the image should be split in to
                int tilesNeeded = imgSize / 256;

                // Enqueue a slicer job
                PipelineCommand sliceCommand = new SplitterCommand(comm.getBoundingBox(), outputFile, tilesNeeded, tilesNeeded);
                outputPipe.put(sliceCommand);
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

}
