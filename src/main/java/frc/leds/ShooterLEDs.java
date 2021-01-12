package frc.leds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class ShooterLEDs{
    private Timer timer;
    private AddressableLED ledsLeft;//, ledsRight;
    private AddressableLEDBuffer ledBuffer;
    private int[] background = {255,0,0};
    private int[] dark = {0,0,0};
    private boolean doingCycle = false;
    private int ticker = 0;
    private boolean drawBackground = true;

    private double period = 0.0002;

    public void init(){
        timer = new Timer();
        timer.stop();
        timer.reset();

        ledBuffer = new AddressableLEDBuffer(17);

        ledsLeft = new AddressableLED(9);
        ledsLeft.setLength(ledBuffer.getLength());

        //ledsRight = new AddressableLED(1);
        //ledsRight.setLength(ledBuffer.getLength());

        enableBackgroundDraw(true);
        setBackground();

        ledsLeft.setData(ledBuffer);
        ledsLeft.start();
        //ledsRight.setData(ledBuffer);
        //ledsRight.start();
    }

    public void startShootCycle(){
        doingCycle = true;
        timer.stop();
        timer.reset();
        ticker = 0;
        timer.start();
    }

    public void update(){
        if(doingCycle){
            if(timer.hasPeriodPassed(period)){
                ticker++;
            }
            if(ticker == ledBuffer.getLength()+1){
                doingCycle = false;
                timer.stop();
                timer.reset();
                setBackground();
            }
            else{
                int[] color = {0,0,255}; //blue
                setThrough(ticker - 3, ticker, color);
            }
        }
        ledsLeft.setData(ledBuffer);
        //ledsRight.setData(ledBuffer);
    }

    public void setBackground(){
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            if(drawBackground){
                ledBuffer.setRGB(i, background[0], background[1], background[2]);
            }
            else{
                ledBuffer.setRGB(i, 0, 0, 0);
            }
        }
    }

    public void enableBackgroundDraw(boolean enabled){
        drawBackground = enabled;
    }

    public void setThrough(int start, int end, int[] color){
        if(end>ledBuffer.getLength()){
            end = ledBuffer.getLength();
        }
        if(start<0){
            start = 0;
        }
        for(int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setRGB(i, 0, 0, 0);
        }
        for(int i = start; i<end; i++){
            ledBuffer.setRGB(i, color[0], color[1], color[2]);
        }
    }

    public void setTargetIndication(boolean valid){
        int[] green = {0,255,0};
        int[] red = {255,0,0};
        if(valid){
            background = green;
        }
        else{
            background = red;
        }
    }
}