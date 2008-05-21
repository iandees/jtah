package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.SplitterCommand;

import javax.imageio.ImageIO;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class PNGSplitterPipelineNode implements PipelineNode {

    private BlockingQueue<PipelineCommand> inputPipe;
    private BlockingQueue<PipelineCommand> outputPipe;

    public PNGSplitterPipelineNode(BlockingQueue<PipelineCommand> inputPipe, BlockingQueue<PipelineCommand> outputPipe) {
        this.inputPipe = inputPipe;
        this.outputPipe = outputPipe;
    }

    public void run() {
        while (true) {
            try {
                Object dequeue = inputPipe.take();
                System.err.println("Splitter dequeued " + dequeue);
                SplitterCommand comm = (SplitterCommand) dequeue;

                // Get the image file we're splitting
                File inputImage = comm.getLargeImageFile();

                // Figure out the number of times we have to split the image
                int tilesAccrossX = comm.getTilesNeededX();
                int tilesAccrossY = comm.getTilesNeededY();

                // TODO For now these should be 256, but perhaps they could be
                // smaller later?
                int targetImgWidth = 256;
                int targetImgHeight = 256;

                // Create the source buffered image
                BufferedImage sourceImage = ImageIO.read(inputImage);
                Graphics sourceGraphics = sourceImage.getGraphics();

                // TODO Could check to see if the requested tiles will fit in
                // the source image correctly at this point.

                // Create a buffered image the size of the target tile(s)
                BufferedImage targetImage = new BufferedImage(targetImgWidth, targetImgHeight,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics targetGraphics = targetImage.getGraphics();

                // Loop through the chunks of the tile
                // Determine the relative X and Y coords to pick from
                // Copy the source image to the target image
                // Write the target image out to file
                // Add the new tile to the list of tiles that were split
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
