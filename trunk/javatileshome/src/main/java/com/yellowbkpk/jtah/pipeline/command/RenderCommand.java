package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D;
import java.io.File;

public class RenderCommand implements PipelineCommand {

    private Rectangle2D.Double boundingBox;
    private File file;
    private int imgSize;

    /**
     * @param boundingBox
     * @param svgFile
     * @param imgSize
     */
    public RenderCommand(Rectangle2D.Double boundingBox, File svgFile, int imgSize) {
        this.boundingBox = boundingBox;
        this.file = svgFile;
        this.imgSize = imgSize;
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

}
