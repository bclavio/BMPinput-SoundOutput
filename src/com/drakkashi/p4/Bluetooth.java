package com.drakkashi.p4;

import gnu.io.*;
import java.io.*;
import java.util.*;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class Bluetooth implements SerialPortEventListener{

    private ArrayList<Integer> hrList = new ArrayList<>();
    private SerialPort serialPort;
    private InputStream is;
    private int heartRate = 0;
    private int preStamp = 0;
    private int baseHR = -1;
    private boolean connected = false;

    public Bluetooth() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()){
            CommPortIdentifier portId = (CommPortIdentifier)portList.nextElement();
            
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
                try{
                    serialPort = (SerialPort) portId.open(null, 9600);
                    is = serialPort.getInputStream();
                    connected = true;
                    System.out.println("Connected");
                    System.out.println("Port: " + portId.getName());
                    break;
                } catch (PortInUseException | IOException e){
                }
            }
        }
    }

    public int getFactor(int preFactor){
        int index = 0;
        while (index < hrList.size()){
            if (hrList.get(index) > 20 && hrList.get(index) < 140)
                index++;
            else
                hrList.remove(index);
        }
        if (hrList.isEmpty() || preFactor < 0)
            return 0;
        
        int min = 240, max = 0;
        for (Integer entry : hrList)
            if (entry < min)
                min = entry;
            else if (entry > max)
                max = entry;
        
        int factor = max - min;
        hrList.clear();
        System.err.println("f: " + (factor) +" ("+(factor-preFactor)+")");
        return factor-preFactor;
    }

    public void setBaseHR(){
        hrList = sort(hrList);
        baseHR = hrList.get(hrList.size()/2);
        System.err.println("\nBase Heartrate: "+baseHR);
        hrList.clear();
    }

    public int getBaseHR(){
        return baseHR;
    }

    public int getHR() {
        if (!connected)
            return 0;
        
        byte[] buffer = new byte[133];
        int i;
        try {
            while((i = is.read()) >= 0){
                if (i == 2){
                    if (is.available() < 2)
                        return heartRate;
                    is.skip(1);
                    int DLC = is.read();
                    is.read(buffer,0,(DLC > is.available() ? is.available() : DLC ));
                    ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                    wrapped.order(ByteOrder.LITTLE_ENDIAN);

                    int[] payload = new int[17];
                    payload[0] = wrapped.get(9);
                    payload[1] = wrapped.get();
                    for (int j = 0; j < 15 && wrapped.remaining() >= 2 ; j++)
                        payload[2+j] = wrapped.getChar();
                    
                    int newHR = calculateHR(payload);
                    if (newHR > 0)
                        heartRate = newHR;
                    Log log = Log.getLog();
                    if (log != null){
                        log.logHR(heartRate);
                        hrList.add(heartRate);
                    }
                }
            }
        } catch (IOException e){
            System.err.println("Connection error");
            connected = false;
        }
        
        return heartRate;
    }

    private int calculateHR(int[] payload) {
        if (payload[7] == 0)
            return 0;

        int i = 7;
        while (payload[i] != preStamp && i+1 < payload.length && payload[i+1] > 0)
            i++;
        
        if (payload[7] > 0)
           preStamp = payload[7];
        return toHR(payload,7,i);
    }

    private int toHR(int[] payload, int i, int j) {
        if (i == j)
            return 0;

        int total = 0,
            count = 0;
        for (int l = i; l+1 <= j; l++){
            int n = millisToHR(payload[l],payload[l+1]);
            total += n;
            count++;
        }
        total /= count;
        
        return (int)total;
    }

    private int millisToHR(float i, float j) {
        if (i < j)
            return (int)(60/((i+65535-j)/1000));
        return (int)(60/((i-j)/1000));
    }

    private ArrayList<Integer> sort (ArrayList<Integer> list){
        ArrayList<Integer> newList = new ArrayList<>();

        while(list.size() > 1){
            int index = 0;
            for (int i = 1; i < list.size();i++)
                if (list.get(i) < list.get(index))
                    index = i;
            newList.add(list.get(index));
            list.remove(index);
        }
        newList.add(list.get(0));
        return newList;
    }

    public void close() {
        try {
            if (is != null)
                is.close();
            if (serialPort != null)
                serialPort.close();

            if (connected)
                System.out.println("Disconnected");
            connected = false;
        } catch ( IOException e){
            System.err.println(e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void serialEvent(SerialPortEvent evt) {
    }
}
