package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;


public class SliceCommand implements PipelineCommand {

    private Double boundingBox;
    private File outputFile;

    public SliceCommand(Rectangle2D.Double boundingBox, File outputFile) {
        this.boundingBox = boundingBox;
        this.outputFile = outputFile;
    }

    /**
     * @return the boundingBox
     */
    public Double getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return the outputFile
     */
    public File getOutputFile() {
        return outputFile;
    }

}
