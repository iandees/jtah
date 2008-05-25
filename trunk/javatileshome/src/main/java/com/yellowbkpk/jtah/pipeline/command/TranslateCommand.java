package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class TranslateCommand implements PipelineCommand {

    private Rectangle2D.Double bbox;
    private int tileX;
    private int tileY;
    private String tileLayer;
    private String filePath;
    private int zoom;

    public TranslateCommand(Rectangle2D.Double boundingBox, int x, int y, int zoom, String layer, String osmDataFilePath) {
        this.bbox = boundingBox;
        this.tileX = x;
        this.tileY = y;
        this.zoom = zoom;
        this.filePath = osmDataFilePath;
    }

    public Double getBoundingBox() {
        return bbox;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public int getZoom() {
        return zoom;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public String getTileLayer() {
        return tileLayer;
    }
    
}
