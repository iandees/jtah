package com.yellowbkpk.jtah.pipeline.command;

import java.awt.geom.Rectangle2D.Double;
import java.io.File;

public class SplitterCommand implements PipelineCommand {

    private File largeImage;
    private int tilesNeededX;
    private int tilesNeededY;

    public SplitterCommand(Double boundingBox, File outputFile, int tilesNeededX, int tilesNeededY) {
        this.largeImage = outputFile;
        this.tilesNeededX = tilesNeededX;
        this.tilesNeededY = tilesNeededY;
    }

    public File getLargeImageFile() {
        return largeImage;
    }

    public int getTilesNeededX() {
        return tilesNeededX;
    }

    public int getTilesNeededY() {
        return tilesNeededY;
    }

}
