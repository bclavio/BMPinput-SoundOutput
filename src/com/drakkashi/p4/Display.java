package com.drakkashi.p4;

import java.util.Random;
import processing.core.PFont;
import processing.core.PApplet;

public class Display extends PApplet {
    
    final SoundLibrary lib = new SoundLibrary(
            "src\\com\\drakkashi\\p4\\fileList.xml");
    
    SoundPlayer soundIntro;
    SoundPlayer[] soundList = new SoundPlayer[2];
            // 0: Sounds
            // 1: Foot steps
    
    Bluetooth bt;
    
    PFont[] fonts = {
        createFont("Copperplate Gothic",24),
        createFont("Copperplate Gothic",12),
        createFont("Copperplate Gothic",16)
    };

    final int[] MARGIN = {50,20,50,20};
    final int WIDTH = 600, HEIGHT = MARGIN[0]+MARGIN[2]+36*7,
              MIN_TIME = 3000, MAX_TIME = 6000;

    String[] inputList;
    String[] textList;
    String[] noteList;
    String title = "";
    String menu = "";
    int soundsPlayed, soundsTotal;
    int focus = 0;
    int preFactor = -1;
    boolean[] boolList;
    

    public static void main(String[] args) {
        PApplet.main(new String[]{com.drakkashi.p4.Display.class.getName()});
    }
    
    @Override
    public void setup() {
        size(WIDTH,HEIGHT);
        Log.setDir("src\\com\\drakkashi\\p4\\");
        Sequence.setDir("src\\com\\drakkashi\\p4\\");
        bt = new Bluetooth();
        moveTo("");
    }

    @Override
    public void draw() {
        if (textList[0] != null && textList[0].equals("DONE"))
            return;
        
        textFont(fonts[0]);
        background(26);
        fill(160);
        int hr = bt.getHR();

        text(title,MARGIN[3],MARGIN[0]);
        if (menu.equals("HEARTRATE_ENTRY") || menu.equals("SEQUENCE_ENTRY")){
            float proc = soundsPlayed;
            if (bt.isConnected() && Log.getLog() != null){
                if (soundIntro == null){
                    try{
                        soundIntro = new SoundPlayer(lib.getIntro(),0);
                    } catch (Exception e){
                        System.err.println(e);
                    }
                }
                else if (soundIntro.isComplete()){
                    if (menu.equals("HEARTRATE_ENTRY") && bt.getBaseHR() <= 0)
                        bt.setBaseHR();
                    proc++;
                    if (hr > 0){
                        if (soundList[0] == null || soundList[0].isComplete()){
                            if (soundList[0] != null){
                                soundsPlayed++;
                                proc++;
                            }
                                        
                            if (soundsPlayed == soundsTotal){
                                textList[0] = "DONE";
                                textList[1] = null;
                                focus = 0;
                            }
                            else if (menu.equals("HEARTRATE_ENTRY"))
                                try{
                                    SoundLibEntry sound = lib.getSound(bt.getFactor(preFactor),preFactor);
                                    preFactor = sound.getFactor();
                                    soundList[0] = new SoundPlayer(sound.getDir(),rand(MIN_TIME,MAX_TIME));
                                } catch (Exception e){
                                    System.err.println(e);
                                }
                            else
                                try{
                                    soundList[0] = new SoundPlayer(lib.soundFromIndex(Sequence.getCurrent().nextSound()),rand(MIN_TIME,MAX_TIME));
                                } catch (Exception e){
                                    System.err.println(e);
                                }
                        }
                        if (soundList[1] == null || soundList[1].isComplete()){
                            try{
                                soundList[1] = new SoundPlayer(lib.getFootStep(),0,false);
                            } catch (Exception e){
                                System.err.println(e);
                            }
                        }
                    }
                }
            }
            else if (!bt.isConnected()){
                bt.close();
                bt = new Bluetooth();
            }
            
            textFont(fonts[2]);
            fill(90);
            int align = (int)textWidth("Sounds played ");
            String timeStr = ": ";
            
            
            if (Log.getLog() != null){
                int time = (int)Log.getLog().getTime();
                if (time/60 >= 10)
                    timeStr += String.valueOf((int)(time/60)) + ":";
                else
                    timeStr += "0" + String.valueOf((int)(time/60)) + ":";

                if (time%60 >= 10)
                    timeStr += time%60;
                else
                    timeStr += "0" + time%60;
            }
            else
                timeStr = ": 00:00";

            text("Heartrate",MARGIN[3]+22,MARGIN[0]                                     +36*2+24*0);
                text(": "+hr,MARGIN[3]+22+align,MARGIN[0]                               +36*2+24*0);
            text("Elapsed time",MARGIN[3]+22,MARGIN[0]                                  +36*2+24*1);
                text(timeStr,MARGIN[3]+22+align,MARGIN[0]                               +36*2+24*1);
            text("Sounds played",MARGIN[3]+22,MARGIN[0]                                 +36*2+24*2);
                text(": "+soundsPlayed+"/"+soundsTotal,MARGIN[3]+22+align,MARGIN[0]     +36*2+24*2);
            
            if (!bt.isConnected()){
                fill(150,80,80);
                text("Disconnected",MARGIN[3]+22,MARGIN[0]+36*2+24*3);
            }
            else
                text("Connected",MARGIN[3]+22,MARGIN[0]+36*2+24*3);

            proc /= soundsTotal+1;
            fill(90);
            stroke(0);
            rect(MARGIN[3]+22,MARGIN[0]+36*2+24*4,WIDTH-(MARGIN[3]+22)*2,24);
            fill(120,120,150);
            noStroke();
            rect(MARGIN[3]+22+1,MARGIN[0]+36*2+24*4+1,(WIDTH-(MARGIN[3]+22)*2-1)*(proc == 0 ? 0.01f : proc),24-1);

            textFont(fonts[0]);
            fill((focus == 0 ? 255 : 160));
            text(textList[0],MARGIN[3]+20,MARGIN[0]+36*(5+2));
            if (textList[1] != null){
                fill((focus == 1 ? 255 : 160));
                text(textList[1],MARGIN[3]+20+textWidth(textList[0]+" "),MARGIN[0]+36*(5+2));
            }
        }
        else
            for (int i = 0; i < textList.length; i++){

                if (textList[i] != null){
                    textFont(fonts[0]);                
                    if (i == focus)
                        fill(255);
                    else
                        fill(160);

                    text(textList[i] + (i <= inputList.length -1 ? " : " + inputList[i] : ""),MARGIN[3]+20,MARGIN[0]+36*(i+2));
                }

                if (i <= noteList.length -1 && noteList[i] != null){
                    textFont(fonts[1]);
                    fill(90);
                    text(noteList[i],MARGIN[3]+22,MARGIN[0]+36*(i+2)+24);                
                }
            }
    }
    
    private void moveTo(String str){
        menu = str;
        focus = 0;

        if (str.isEmpty()){
            title = "";
            textList = new String[2];
            textList[0] = "HEARTRATE";
            textList[1] = "SEQUENCE";
            inputList = new String[0];
            noteList = new String[0];
        }
        else if (str.equals("HEARTRATE")){
            focus = 2;
            title = "Heartrate";
            textList = new String[4];
            textList[0] = "SOUNDS";
            textList[2] = "SELECT";
            textList[3] = "BACK";
            inputList = new String[1];
            inputList[0] = "14";
            noteList = new String[1];
            noteList[0] = "Available sounds: "+lib.size();
        }
        else if (str.equals("SEQUENCE") && Sequence.getCurrent() != null){
            title = "Sequence";
            textList = new String[4];
            textList[0] = "CONTINUE";
            textList[1] = "NEW SEQUENCE";
            textList[3] = "BACK";
            inputList = new String[0];
            noteList = new String[0];
        }
        else if (str.equals("NEW SEQUENCE") || str.equals("SEQUENCE")){
            focus = 4;
            title = "New Sequence";
            textList = new String[6];
            textList[0] = "SOUNDS";
            textList[2] = "SESSIONS";
            textList[4] = "CREATE";
            textList[5] = "BACK";
            inputList = new String[3];
            inputList[0] = String.valueOf(lib.size());
            inputList[2] = String.valueOf(lib.size());
            noteList = new String[3];
            noteList[0] = "(Per session)";
            noteList[2] = "Total sounds: "+lib.size();
        }
        else{
            soundIntro = null;
            soundsPlayed = 0;

            if (str.equals("HEARTRATE_ENTRY"))
                title = Log.nextLog();
            else{
                title = (Sequence.getEntryIndex()+1)+"/"+Sequence.size();
                soundsTotal = Sequence.getCurrent().totalSounds();
            }
            
            textList = new String[2];
            textList[0] = "START";
            textList[1] = "BACK";
            inputList = new String[0];
            noteList = new String[0];
        }
        boolList = new boolean[inputList.length];
    }

    @Override
    public void keyPressed() {
        if (key == CODED && keyCode == UP)
            do{
                focus = (focus > 0 ? (focus-1)%textList.length : textList.length-1 );
            } while(textList[focus] == null);
        else if (key == 0x09 || key == CODED && (keyCode == DOWN || textList[0] != null && (textList[0].equals("YES") || textList[0].equals("START")) && (keyCode == LEFT || keyCode == RIGHT)))
            do{
                focus = (focus+1)%textList.length;
            } while(textList[focus] == null);
        else if (key == ENTER && focus > inputList.length -1){
            switch (textList[focus]) {
                case "BACK":
                    if (menu.equals("HEARTRATE_ENTRY"))
                        moveTo("HEARTRATE");
                    else if (menu.equals("SEQUENCE_ENTRY"))
                        moveTo("SEQUENCE");
                    else
                        moveTo("");
                    break;
                case "CREATE":
                    float input1 = Float.valueOf(inputList[0]),
                          input2 = Float.valueOf(inputList[2]);
                    if (input1 > 0 && input1 <= lib.size() &&
                            input2 > 0 && input2 <= lib.size() &&
                            isInt(input2/lib.size()*input1))
                    {
                        Sequence.setCurrent((int)input1, (int)input2, lib.size());
                        moveTo("SEQUENCE_ENTRY");
                    }
                    break;
                case "CONTINUE":
                    moveTo("SEQUENCE_ENTRY");
                    break;
                case "SELECT":
                    int input = Integer.valueOf(inputList[0]);
                    if (input > 0 && input <= lib.size()){
                        soundsTotal = input;
                        moveTo("HEARTRATE_ENTRY");
                    }
                    break;
                case "START":
                    if (menu.equals("HEARTRATE_ENTRY"))
                        Log.newLog();
                    else
                        Sequence.getCurrent().newEntry();
                    textList[0] = "CANCEL";
                    textList[1] = null;
                    break;
                case "CANCEL":
                    textList[0] = "YES";
                    textList[1] = "NO";
                    focus = 1;
                    break;
                case "NO":
                    textList[0] = "CANCEL";
                    textList[1] = null;
                    focus = 0;
                    break;
                case "YES":
                    soundList = new SoundPlayer[2];
                    lib.reset();
                    moveTo((menu.equals("HEARTRATE_ENTRY") ? "HEARTRATE" : "SEQUENCE"));
                    Log.cancelLog();
                    break;
                case "DONE":
                    soundList = new SoundPlayer[2];
                    lib.reset();
                    Log.closeLog();
                    if (menu.equals("HEARTRATE_ENTRY"))
                        moveTo("HEARTRATE");
                    else if (Sequence.complete())
                        moveTo("");
                    else
                        moveTo("SEQUENCE");
                    break;
                default:
                    moveTo(textList[focus]);
                    break;
            }
        }
        else if (focus <= inputList.length -1){
            if (key == 0x08 && inputList[focus].length() != 0)
                inputList[focus] = inputList[focus].substring(0,inputList[focus].length()-1);
            else if (key == 0x2E || key >= 0x30 && key <= 0x39){
                inputList[focus] = ((inputList[focus].isEmpty() || inputList[focus].charAt(0) != '0' ) && boolList[focus] ? inputList[focus] : "") + key;
                boolList[focus] = true;
            }
        }
    }

    private static boolean isInt(float f){
        System.out.println("Total plays per sound: " + f);
        return (int)f == f;
    }

    private static int rand(int min, int max){
        return new Random().nextInt((max-min)+1)+min;
    }

    @Override
    public void keyReleased() {
    }
}
