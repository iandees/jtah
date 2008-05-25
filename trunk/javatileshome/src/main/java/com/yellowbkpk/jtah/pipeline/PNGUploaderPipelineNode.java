package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.TranslateCommand;

import java.util.concurrent.BlockingQueue;

public class PNGUploaderPipelineNode extends AbstractPipelineNode {

    public PNGUploaderPipelineNode(BlockingQueue<PipelineCommand> inputPipe) {
        super(inputPipe, null);
    }

    public void run() {
        while(true) {
            try {
                Object dequeue = getInputPipe().take();
                System.err.println("Translator dequeued " + dequeue);
                TranslateCommand comm = (TranslateCommand) dequeue;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
