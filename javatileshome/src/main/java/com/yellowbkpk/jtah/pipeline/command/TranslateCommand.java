package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class TranslateCommand implements PipelineCommand {

    private Double bbox;
    private String filePath;
    private int zoom;

    /**
     * @param boundingBox
     * @param zoom 
     * @param osmDataFilePath 
     */
    public TranslateCommand(Rectangle2D.Double boundingBox, int zoom, String osmDataFilePath) {
        this.bbox = boundingBox;
        this.zoom = zoom;
        this.filePath = osmDataFilePath;
    }

    /**
     * @return the bounding box.
     */
    public Double getBoundingBox() {
        return bbox;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public int getZoom() {
        return zoom;
    }
}
