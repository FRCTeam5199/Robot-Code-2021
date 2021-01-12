package frc.spinner;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

public class Spinner{
    public Spinner(){

    }

    public void update(){
        SmartDashboard.putString("Color on FMS", getFMSColorString());
        SmartDashboard.putString("Color towards Robot", adjustedColorString());
    }

    public void init(){

    }

    private char getFMSColor(){ //return the color you want under the bar
        String color = DriverStation.getInstance().getGameSpecificMessage();
        if(color.length()>0){
            return color.charAt(0);
        }
        else{
            return 'N';
        }
    }
    
    private String getFMSColorString(){ //return the color you want under the bar
        String color = DriverStation.getInstance().getGameSpecificMessage();
        if(color.length()>0){
            return color.substring(0,1);
        }
        else{
            return "N";
        }
    }

    private char adjustedColor(){ //return the color that you want facing you
        switch(getFMSColor()){
            case 'B' ://blue -> red
                return 'R';
            case 'G' ://green -> yellow
                return 'Y';
            case 'R' ://red -> blue
                return 'B';
            case 'Y' ://yellow -> green
                return 'G';
            case 'N' ://no data -> no data
                return 'N';
            default ://return E for error
                return 'E'; 
        }
    }
    private String adjustedColorString(){ //return the color that you want facing you
        switch(getFMSColor()){
            case 'B' ://blue -> red
                return "R";
            case 'G' ://green -> yellow
                return "Y";
            case 'R' ://red -> blue
                return "B";
            case 'Y' ://yellow -> green
                return "G";
            case 'N' ://no data -> no data
                return "N";
            default ://return E for error
                return "E"; 
        }
    }
}