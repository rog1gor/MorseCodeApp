package com.example.morsecode;

// Singleton for holding all static data structures.
public class MorsApp {
    private static MorsApp mInstance= null;

    private Torch.Tool tool;
    private Torch torch;

    protected MorsApp() {}

    public static synchronized MorsApp getInstance() {
        if (null == mInstance) {
            mInstance = new MorsApp();
        }
        return mInstance;
    }

    public Torch.Tool getTool() {
        return this.tool;
    }

    public void setTool(Torch.Tool tool) {
        this.tool = tool;
    }

    public Torch getTorch() {
        return this.torch;
    }

    public void setTorch(Torch torch) {
        this.torch = torch;
    }
}