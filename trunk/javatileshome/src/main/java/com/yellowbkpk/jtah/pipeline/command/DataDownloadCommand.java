package com.yellowbkpk.jtah.pipeline.command;

public class DataDownloadCommand implements PipelineCommand {

    private int x;
    private int y;
    private int z;
    private String layer;

    /**
     * @param x
     * @param y
     * @param z
     * @param layer
     */
    public DataDownloadCommand(int x, int y, int z, String layer) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.layer = layer;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @return the layer
     */
    public String getLayer() {
        return layer;
    }
    
}
