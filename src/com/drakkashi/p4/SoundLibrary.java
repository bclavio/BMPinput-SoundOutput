package com.drakkashi.p4;

import java.util.Random;
import java.util.ArrayList;

public class SoundLibrary {
    private ArrayList<SoundLibEntry> soundList = new ArrayList<>();
    private ArrayList<SoundLibEntry> soundListMirror = new ArrayList<>();
    private ArrayList<String> stepList = new ArrayList<>();
    private String intro;
    private int lastStep = -1;
    
    public SoundLibrary(String dir){
        XMLLoader xml = new XMLLoader(dir);
        xml.toLibrary(this);
        sortList();
        soundListMirror = (ArrayList<SoundLibEntry>)soundList.clone();
    }

    public void reset(){
        soundList = (ArrayList<SoundLibEntry>)soundListMirror.clone();
    }

    private void sortList(){
        ArrayList<SoundLibEntry> newList = new ArrayList<>();
        
        while(soundList.size() > 1){
            int index = 0;
            for (int i = 1; i < soundList.size();i++)
                if (soundList.get(i).getFactor() < soundList.get(index).getFactor())
                    index = i;
            newList.add(soundList.get(index));
            soundList.remove(index);
        }
        newList.add(soundList.get(0));

        soundList = newList;
    }

    public int size(){
        return soundList.size();
    }

    public String getFootStep(){
        int i = rand(0,stepList.size()-1);

        if (i == lastStep)
            i = (i+1)%stepList.size();

        lastStep = i;
        return stepList.get(i);
    }
    
    private static int rand(int min, int max){
        return new Random().nextInt((max-min)+1)+min;
    }

    public String soundFromIndex (int i){
        return soundList.get(i).getDir();
    }
    
    private SoundLibEntry getRandSound(){
        if (soundList.isEmpty())
            return null;
        if (soundList.size() == 1)
            return soundList.get(0);
        if (soundList.size() > 6)
            return soundList.get(rand(3,soundList.size()-4));
        return soundList.get(rand(1,soundList.size()-2));
    }
    
    public SoundLibEntry getSound (int reaction, int preFactor){
        SoundLibEntry entry;
        
        if (reaction != 0)
            entry = hash(soundList,preFactor-reaction);
        else
            entry = getRandSound();
        
        if (entry != null){
            soundList.remove(entry);
            return entry;
        }
        return null;
    }

    private SoundLibEntry hash (ArrayList<SoundLibEntry> soundList, int i){
        for (int j=0; j < soundList.size() -1; j++){
            int f1 = soundList.get(j).getFactor(),
                f2 = soundList.get(j+1).getFactor();
            if(i <= f1 || i <= ((f2 - f1) / 2 + f1) )
                return soundList.get(j);
        }
        if (soundList.size() > 0)
            return soundList.get(soundList.size()-1);
        return null;
    }

    public void newSound(int i, String str){
        soundList.add(new SoundLibEntry(i,str));
    }

    public void newFootStep(String str){
        stepList.add(str);
    }
    
    public void setIntro(String str){
        intro = str;
    }

    public String getIntro(){
        return intro;
    }
}