package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;
import com.yellowbkpk.jtah.pipeline.command.TranslateCommand;

import java.util.concurrent.BlockingQueue;

public class PNGUploaderPipelineNode implements PipelineNode {

    private BlockingQueue<PipelineCommand> inputPipe;

    public PNGUploaderPipelineNode(BlockingQueue<PipelineCommand> inputPipe) {
        this.inputPipe = inputPipe;
    }

    public void run() {
        while(true) {
            try {
                Object dequeue = inputPipe.take();
                System.err.println("Translator dequeued " + dequeue);
                TranslateCommand comm = (TranslateCommand) dequeue;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
