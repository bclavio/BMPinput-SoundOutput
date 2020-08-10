package com.drakkashi.p4;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class Log {
    
    private static String dir;
    private static Log log;
    private final Long timeStamp = System.currentTimeMillis();
    private final String entry;
    private BufferedWriter out;
    private File file;

    public Log(){
        entry = nextLog();
        file = new File(dir+entry);
        try{
            out = new BufferedWriter(new FileWriter(file, true));
            System.out.println("\n\nNew log: " + file);
        } catch(IOException e){
            System.err.println(e);
        }
    }

    public Log(String dir, String entry){
        log = this;
        this.entry = entry;
        file = new File(dir + entry);
        try{
            out = new BufferedWriter(new FileWriter(file, true));
        } catch(IOException e){
            System.err.println(e);
        }
    }
    
    public void logHR(int i){
        try{
            out.newLine();
            out.write(getTime()+"\t"+i);
            System.out.println(getTime()+"\t"+i);
            out.flush();
        } catch(IOException e){
            System.err.println(e);
        }
    }
    
    public void soundStart(String str){
        try{
            out.write("\tSTART " + str);
            System.out.println("\t\tSTART " + str);
            out.flush();
        } catch(IOException e){
            System.err.println(e);
        }
    }

    public void soundEnd(String str){
        try{
            out.write("\tEND " + str);
            System.out.println("\t\tEND " + str);
            out.flush();
        } catch(IOException e){
            System.err.println(e);
        }
    }
    
    public void cancel(){
        try{
            out.close();
        } catch(IOException e){
            System.err.println(e);
        }
        out = null;
        file.delete();
        file = null;            
    }
    
    public void close(){
        try{
            out.close();
        } catch(IOException e){
            System.err.println(e);
        }
        out = null;
        file = null;
    }

    public String getEntry(){
        return entry;
    }

    public float getTime(){
        return (float)(System.currentTimeMillis() - timeStamp)/1000;
    }

    public static void setDir(String str){        
        dir = str;
    }

    public static void newLog(){
        log = new Log();
    }

    public static void cancelLog(){
        if (log != null){
            log.cancel();
            log = null;
        }
    }

    public static void closeLog(){
        if (log != null){
            log.close();
            log = null;
        }
    }

    public static Log getLog(){
        return log;
    }
    
    public static String nextLog(){
        int i = 0;
        File f;
        String entry = "test000.txt";
        String str = dir + entry;

        while((f = new File(str)).exists() && !f.isDirectory()){
            i++;
            
            entry = "test";

            if (i < 10)
                entry += "00";
            else if (i < 100)
                entry += "0";
            
            entry += i+".txt";
            
            str = dir + entry;
        }
        
        return entry;
    }
}