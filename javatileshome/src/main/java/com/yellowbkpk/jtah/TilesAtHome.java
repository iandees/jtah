package com.yellowbkpk.jtah;

import com.ge.medit.util.Conduit;
import com.ge.medit.util.SimpleQueue;

import com.yellowbkpk.jtah.pipeline.DequeuePipelineNode;
import com.yellowbkpk.jtah.pipeline.OSMDataDownloadPipelineNode;
import com.yellowbkpk.jtah.pipeline.PNGSplitterPipelineNode;
import com.yellowbkpk.jtah.pipeline.PNGUploaderPipelineNode;
import com.yellowbkpk.jtah.pipeline.PipelineNode;
import com.yellowbkpk.jtah.pipeline.RenderSVGPipelineNode;
import com.yellowbkpk.jtah.pipeline.TranslateOSMPipelineNode;

public class TilesAtHome {

    public static void main(String[] args) {
        // Dequeue a render request from the server
        Conduit dequeueToDownloadPipe = new SimpleQueue();
        PipelineNode dequeuer = new DequeuePipelineNode(dequeueToDownloadPipe);
        new Thread(dequeuer, "Dequeue").start();
        
        // Download the data using the OSM API
        Conduit downloadToTranslatePipe = new SimpleQueue();
        PipelineNode downloader = new OSMDataDownloadPipelineNode(dequeueToDownloadPipe, downloadToTranslatePipe);
        new Thread(downloader, "OSM Download").start();
        
        // Translate the OSM data into SVG
        Conduit translateToRenderPipe = new SimpleQueue();
        PipelineNode translator = new TranslateOSMPipelineNode(downloadToTranslatePipe, translateToRenderPipe);
        new Thread(translator, "Translate OSM").start();
        
        // Render the SVG to PNG
        Conduit renderToSplitterPipe = new SimpleQueue();
        PipelineNode renderer = new RenderSVGPipelineNode(translateToRenderPipe, renderToSplitterPipe);
        new Thread(renderer, "Render SVG").start();
        
        // Split the large PNG to many smaller ones
        Conduit splitterToUploaderPipe = new SimpleQueue();
        PipelineNode splitter = new PNGSplitterPipelineNode(translateToRenderPipe, splitterToUploaderPipe);
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
