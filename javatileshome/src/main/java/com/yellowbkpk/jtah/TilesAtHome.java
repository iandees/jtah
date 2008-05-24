package com.yellowbkpk.jtah;

import com.yellowbkpk.jtah.pipeline.DequeuePipelineNode;
import com.yellowbkpk.jtah.pipeline.OSMDataDownloadPipelineNode;
import com.yellowbkpk.jtah.pipeline.PNGSplitterPipelineNode;
import com.yellowbkpk.jtah.pipeline.PNGUploaderPipelineNode;
import com.yellowbkpk.jtah.pipeline.PipelineNode;
import com.yellowbkpk.jtah.pipeline.RenderSVGPipelineNode;
import com.yellowbkpk.jtah.pipeline.TranslateOSMPipelineNode;
import com.yellowbkpk.jtah.pipeline.command.PipelineCommand;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TilesAtHome {

    public static void main(String[] args) {
        // Dequeue a render request from the server
        BlockingQueue<PipelineCommand> dequeueToDownloadPipe = new ArrayBlockingQueue<PipelineCommand>(6);
        PipelineNode dequeuer = new DequeuePipelineNode(dequeueToDownloadPipe);
        new Thread(dequeuer, "Dequeue").start();
        
        // Download the data using the OSM API
        BlockingQueue<PipelineCommand>  downloadToTranslatePipe = new ArrayBlockingQueue<PipelineCommand>(6);
        PipelineNode downloader = new OSMDataDownloadPipelineNode(dequeueToDownloadPipe, downloadToTranslatePipe);
        new Thread(downloader, "OSM Download").start();
        
        // Translate the OSM data into SVG
        BlockingQueue<PipelineCommand>  translateToRenderPipe = new ArrayBlockingQueue<PipelineCommand>(6);
        PipelineNode translator = new TranslateOSMPipelineNode(downloadToTranslatePipe, translateToRenderPipe);
        new Thread(translator, "Translate OSM").start();
        
        // Render the SVG to PNG
        BlockingQueue<PipelineCommand>  renderToSplitterPipe = new ArrayBlockingQueue<PipelineCommand>(6);
        PipelineNode renderer = new RenderSVGPipelineNode(translateToRenderPipe, renderToSplitterPipe);
        new Thread(renderer, "Render SVG").start();
        
        // Split the large PNG to many smaller ones
        BlockingQueue<PipelineCommand>  splitterToUploaderPipe = new ArrayBlockingQueue<PipelineCommand>(6);
        PipelineNode splitter = new PNGSplitterPipelineNode(renderToSplitterPipe, splitterToUploaderPipe);
        new Thread(splitter, "Split PNG").start();
        
        // Upload the PNG to the server
        PipelineNode uploader = new PNGUploaderPipelineNode(splitterToUploaderPipe);
        new Thread(uploader, "Uploader").start();
        
        while(true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
