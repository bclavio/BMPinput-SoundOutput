package com.drakkashi.p4;

public class SoundLibEntry {
    
    private final String soundDir;
    private final int factor;

    public SoundLibEntry(int i, String str){
        factor = i;
        soundDir = str;
    }
    
    public String getDir(){
        return soundDir;
    }
    
    public int getFactor(){
        return factor;
    }
}
