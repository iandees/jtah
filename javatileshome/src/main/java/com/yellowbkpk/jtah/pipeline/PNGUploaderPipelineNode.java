package com.yellowbkpk.jtah.pipeline;

import com.ge.medit.util.Conduit;

import com.yellowbkpk.jtah.pipeline.command.TranslateCommand;

public class PNGUploaderPipelineNode implements PipelineNode {

    private Conduit inputPipe;

    public PNGUploaderPipelineNode(Conduit inputPipe) {
        this.inputPipe = inputPipe;
    }

    public void run() {
        while(true) {
            try {
                Object dequeue = inputPipe.dequeue();
                System.err.println("Translator dequeued " + dequeue);
                TranslateCommand comm = (TranslateCommand) dequeue;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
