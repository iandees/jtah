package com.yellowbkpk.jtah.pipeline;

import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;

import java.util.concurrent.BlockingQueue;

/**
 * @author Ian Dees
 *
 */
public abstract class AbstractPipelineNode implements PipelineNode {

    private BlockingQueue<PipelineCommand> inputPipe;
    private BlockingQueue<PipelineCommand> outputPipe;

    public AbstractPipelineNode(BlockingQueue<PipelineCommand> input, BlockingQueue<PipelineCommand> output) {
        this.inputPipe = input;
        this.outputPipe = output;
    }

    protected BlockingQueue<PipelineCommand> getInputPipe() {
        return inputPipe;
    }

    protected BlockingQueue<PipelineCommand> getOutputPipe() {
        return outputPipe;
    }

}