package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;

public class RenderCommand implements PipelineCommand {

    private Double boundingBox;
    private File file;

    /**
     * @param boundingBox
     * @param svgFile
     */
    public RenderCommand(Rectangle2D.Double boundingBox, File svgFile) {
        this.boundingBox = boundingBox;
        this.file = svgFile;
    }

    public Double getBoundingBox() {
        return boundingBox;
    }

    public File getFile() {
        return file;
    }

}
