package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D;
import java.io.File;

public class RenderCommand implements PipelineCommand {

    private Rectangle2D.Double boundingBox;
    private File file;
    private int imgSize;
    private int tileX;
    private int tileY;
    private int tileZ;
    private String layer;

    /**
     * @param boundingBox
     * @param svgFile
     * @param imgSize
     */
    public RenderCommand(Rectangle2D.Double boundingBox, int x, int y, int z, String layer, File svgFile, int imgSize) {
        this.boundingBox = boundingBox;
        this.file = svgFile;
        this.imgSize = imgSize;
        this.tileX = x;
        this.tileY = y;
        this.tileZ = z;
        this.layer = layer;
    }

    public Rectangle2D.Double getBoundingBox() {
        return boundingBox;
    }

    public File getFile() {
        return file;
    }

    public int getImageSize() {
        return imgSize;
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

    public String getTileLayer() {
        return layer;
    }

}
