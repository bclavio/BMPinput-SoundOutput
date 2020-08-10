package com.drakkashi.p4;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;

public class SoundPlayer extends Object implements LineListener {

    private static int count = 0;
    private File file;
    private Clip clip;
    private AudioInputStream ais;
    private long timeStamp;
    private boolean complete = false;
    private boolean logEvents;

    public SoundPlayer(String str, long i, boolean ... b) throws Exception {
        file = new File(str);
        logEvents = b.length == 0 || b[0];
        timeStamp = i;

        Line.Info linfo = new Line.Info(Clip.class);
        Line line = AudioSystem.getLine(linfo);
        clip = (Clip) line;
        clip.addLineListener(this);
        ais = AudioSystem.getAudioInputStream(file);
        try{
            clip.open(ais);
        } catch (LineUnavailableException | IOException e){
            System.err.print("\n" + str + ": ");
            clip.open(ais);
        }
        clip.start();
        count++;
    }

    @Override
    public void update(LineEvent e) {
        LineEvent.Type type = e.getType();
        if (type == LineEvent.Type.OPEN){
        }
        if (type == LineEvent.Type.CLOSE){
        }
        if (type == LineEvent.Type.START){
            if (logEvents)
                Log.getLog().soundStart(file.getName());
        }
        if (type == LineEvent.Type.STOP){
            if (logEvents)
                Log.getLog().soundEnd(file.getName());
            removeSelf();
        }
    }
    
    public boolean isComplete(){
        return complete && System.currentTimeMillis() > timeStamp;
    }

    public void removeSelf(){
        timeStamp += System.currentTimeMillis();
        complete = true;
        try {
            clip.close();
            clip = null;
            file = null;
            ais.close();
            ais = null;
            count--;
        } catch (IOException e){
            System.out.println(e);
        }
    }
    
    public static int getSounds(){
        return count;
    }
}
