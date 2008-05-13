package com.yellowbkpk.jtah.pipeline.command;

import java.io.File;

public class SplitterCommand implements PipelineCommand {

    private File largeImage;
    private int tilesNeededX;
    private int tilesNeededY;

    /**
     * @return
     */
    public File getLargeImageFile() {
        return largeImage;
    }

    /**
     * @return
     */
    public int getTilesNeededX() {
        return tilesNeededX;
    }

    /**
     * @return
     */
    public int getTilesNeededY() {
        return tilesNeededY;
    }

}
