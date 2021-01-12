package frc.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotorChecker{

    private static String output = "";
    /**
     * If the fault is true, put it into the 
     */
    public static void putMotorFault(String motorName, String motorFault, boolean fault){
        if(fault){
            output += motorFault+" fault on motor "+motorName+"\n";
        }
    }

    public static void showData(){
        SmartDashboard.putString("Motor Fault List", output);
    }
}