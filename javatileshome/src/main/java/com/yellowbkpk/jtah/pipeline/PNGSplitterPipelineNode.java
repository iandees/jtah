package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.SplitterCommand;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class PNGSplitterPipelineNode extends AbstractPipelineNode {

    public PNGSplitterPipelineNode(BlockingQueue<PipelineCommand> inputPipe, BlockingQueue<PipelineCommand> outputPipe) {
        super(inputPipe, outputPipe);
    }

    public void run() {
        while (true) {
            try {
                Object dequeue = getInputPipe().take();
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

                // TODO Could check to see if the requested tiles will fit in
                // the source image correctly at this point.

                // Create a buffered image the size of the target tile(s)
                BufferedImage targetImage = new BufferedImage(targetImgWidth, targetImgHeight,
                        BufferedImage.TYPE_INT_ARGB);

                // Loop through the chunks of the source tile
                int sourceWidth = sourceImage.getWidth();
                int sourceHeight = sourceImage.getHeight();
                for(int x = 0; x < sourceWidth; x += targetImgWidth) {
                    for(int y = 0; y < sourceHeight; y += targetImgHeight) {
                        // Copy the source image to the target image
                        BufferedImage subimage = sourceImage.getSubimage(x, y, targetImgWidth, targetImgHeight);
                        
                        // Write the target image out to file
                        File outFile = new File(UUID.randomUUID() + ".png");
                        ImageIO.write(subimage, "PNG", outFile);
                        System.err.println("Saving chunk " + x + "," + y + " to " + outFile);
                        // Add the new tile to the list of tiles that were split
                        
                    }
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
