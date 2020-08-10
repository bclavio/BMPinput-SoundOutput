package com.drakkashi.p4;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Random;
import java.util.ArrayList;

public class Sequence {

    private static Sequence current;
    private static String dir;
    private static int index;
    private static int entryIndex;
    private int[][] list;
    private int sound = 0;

    public Sequence(){
        File dataList = new File(dir + indexToStr(index) + "/list.txt");

        try{
            BufferedReader reader = new BufferedReader(new FileReader(dataList));
            ArrayList<ArrayList<Integer>> holderList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strList = line.split(",");
                ArrayList<Integer> subList = new ArrayList<>();
                
                for (String entry : strList) {
                    subList.add(Integer.valueOf(entry));
                }

                holderList.add(subList);
            }
            list = new int[holderList.size()][holderList.get(0).size()];
            
            for (int i = 0; i < list.length; i++)
                for (int j = 0; j < list[0].length; j++)
                    list[i][j] = holderList.get(i).get(j);
            
        } catch (IOException e){
        }
        for (int i = 0; i < list.length; i++){
            for (int j = 0; j < list[i].length; j++)
                System.out.print("[" + list[i][j] + "]");
            System.out.print("\n");
        }
        nextEntry();
        if (entryIndex < list.length)
            current = this;
    }

    public Sequence(int sessions, int sounds, int total){
        newList(sessions, sounds, total);
        for (int i = 0; i < list.length; i++){
            for (int j = 0; j < list[i].length; j++)
                System.out.print("[" + list[i][j] + "]");
            System.out.print("\n");
        }
        try{
            System.out.println("File: " + dir + indexToStr(index) + "/list.txt");

            File folder = new File(dir + indexToStr(index));
            if (!folder.exists()) {
                System.out.println("creating directory: " + dir + indexToStr(index));

                if(folder.mkdir()) {
                    System.out.println("Directory created");  
                }
            }            
            System.out.println("Writing to file...");
            
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(dir + indexToStr(index) + "/list.txt"), true));
            for (int i = 0; i < list.length; i++){
                
                String str = String.valueOf(list[i][0]);
                for (int j = 1; j < list[i].length; j++)
                    str += "," + list[i][j];
                
                System.out.println("Line " + (i+1) + ": [" + str + "]");
                out.write(str+"\n");
            }
            out.close();
        } catch (IOException e){
            System.err.println(e);
        }
    }

    public int nextSound(){
        return list[entryIndex][sound++];
    }

    public int listSize(){
        return list.length;
    }

    public int totalSounds(){
        return list[0].length;
    }

    private void newList(int sounds, int sessions, int total){
        list = new int[sessions][sounds];
        int plays = (sessions*sounds)/total;
        boolean error;
        
        do{
            error = false;
            for (int i = 0; i < sounds*sessions; i++)
                list[i/sounds][i%sounds] = -1;

            for (int i = 0; i < total && !error; i++){
                boolean[] takenX = new boolean[sounds];
                boolean[] takenY = new boolean[sessions];

                for (int l = 0; l < plays && !error; l++){
                    ArrayList<Integer> validList = new ArrayList<>();
                    for (int j = 0; j < sounds*sessions; j++){

                        if (takenY[j/sounds])
                            j += sounds-1;
                        else if (!takenX[j%sounds] && list[j/sounds][j%sounds] == -1)
                            validList.add(j);
                    }
                    if (validList.size() > 0){
                        int r = validList.get(rand(validList.size()));
                        list[r/sounds][r%sounds] = i;
                        takenX[r%sounds] = true;
                        takenY[r/sounds] = true;
                    }
                    else
                        error = true;
               }
            }        
        } while (error);
    }
    
    public void newEntry(){
        sound = 0;
        new Log(dir, indexToStr(index) + "\\" + nextEntry());
        System.out.println("\n\nEntry: " + entryIndex);
    }
    
    public static Sequence getCurrent(){
        return current;
    }
    
    public static void setDir(String str){
        dir = str;
        
        System.out.println("Root File: "+dir+"current.txt");
        index = getIndex();
        System.out.println("Current index: "+index);
        if (index >= 0)
            new Sequence();
    }

    public static void setCurrent(int sounds, int sessions, int total){
        index++;
        current = new Sequence(sounds, sessions, total);
    }
    
    public static int size(){
        return current.listSize();
    }

    public static int getEntryIndex(){
        nextEntry();
        return entryIndex;
    }
    
    public static String nextEntry(){
        int i = 0;
        File f;
        String entry = "entry000.txt";
        String str = dir + indexToStr(index) + "/" + entry;

        while((f = new File(str)).exists() && !f.isDirectory()){
            i++;
            
            entry = "entry";

            if (i < 10)
                entry += "00";
            else if (i < 100)
                entry += "0";
            
            entry += i+".txt";
            
            str = dir + indexToStr(index) + "/" + entry;
        }
        
        entryIndex = i;
        return entry;
    }
    
    public static boolean complete(){
        nextEntry();
        if (entryIndex >= size()){
            current = null;
            return true;
        }
        return false;
    }
    
    private static int getIndex(){
        int i = 0;
        String str = dir + "/sequence000";

        while(new File(str).exists())
            str = dir + indexToStr(++i);
        
        return i-1;
    }

    private static String indexToStr(int i){
        String str = "sequence";

        if (i < 10)
            str += "00";
        else if (i < 100)
            str += "0";
        
        return str + i;
    }

    private static int rand(int i){
        return new Random().nextInt(i);
    }
}
