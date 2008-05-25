package com.yellowbkpk.jtah.pipeline.command;

import java.io.File;

public class UploadCommand implements PipelineCommand {

    private File outFile;
    private int tileX;
    private int tileY;
    private int tileZ;
    private String tileLayer;

    public UploadCommand(File outFile, int tileX, int tileY, int tileZ, String layer) {
        this.outFile = outFile;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileZ = tileZ;
        this.tileLayer = layer;
    }

    public File getOutFile() {
        return outFile;
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
        return tileLayer;
    }

}
