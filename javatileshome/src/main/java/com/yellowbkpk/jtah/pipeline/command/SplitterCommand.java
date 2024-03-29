package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D.Double;
import java.io.File;

public class SplitterCommand implements PipelineCommand {

    private File largeImage;
    private int tileX;
    private int tileY;
    private int tileZ;
    private String tileLayer;

    public SplitterCommand(Double boundingBox, File outputFile, int tileX, int tileY, int tileZ, String layer) {
        this.largeImage = outputFile;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileZ = tileZ;
        this.tileLayer = layer;
    }

    public File getLargeImageFile() {
        return largeImage;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }
    
    public int getTileZ() {
        return tileZ;
    }

    public String getLayer() {
        return tileLayer;
    }

}
