package frc.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.*;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.util.Logger;
//import frc.util.Permalogger;
import frc.vision.GoalChameleon;

import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;

import edu.wpi.first.wpilibj.Joystick;

public class Hopper{
    public VictorSPX agitator, indexer;
    public Rev2mDistanceSensor indexSensor;
    private double fireOffset = 0;
    private ButtonPanel panel;
    private Joystick joy;
    // private ShuffleboardTab tab = Shuffleboard.getTab("balls");
    // private NetworkTableEntry aSpeed = tab.add("Agitator Speed", 0.6).getEntry();
    // private NetworkTableEntry iSpeed = tab.add("Indexer Speed", 0.7).getEntry();
    // public NetworkTableEntry visionOverride = tab.add("VISION OVERRIDE", false).getEntry();
    // public NetworkTableEntry spinupOverride = tab.add("SPINUP OVERRIDE", false).getEntry();
    // public NetworkTableEntry disableOverride = tab.add("LOADING DISABLE", false).getEntry();

    public boolean autoIndex = true;
    private boolean isReversed = false;
    private boolean isForced = false;
    public boolean indexed = false;


    public void init(){
        autoIndex = true;
        if(autoIndex){
            indexSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches , RangeProfile.kHighAccuracy);
        }
        agitator = new VictorSPX(RobotMap.agitatorMotor);
        indexer = new VictorSPX(RobotMap.indexerMotor);
        panel = new ButtonPanel(3);
        //joy = new Joystick(3);
    }

    // public void updateSimple(){
    //     if(panel.getButton(8)){
    //         indexer.set(ControlMode.PercentOutput, iSpeed.getDouble(0.6));
    //     }
    //     else if(panel.getButton(9)){
    //         indexer.set(ControlMode.PercentOutput, -iSpeed.getDouble(0.6));
    //     }
    //     else{
    //         indexer.set(ControlMode.PercentOutput, 0);
    //     }

    //     if(panel.getButton(3)){
    //         agitator.set(ControlMode.PercentOutput, aSpeed.getDouble(0.7));
    //     }
    //     else if(panel.getButton(4)){
    //         agitator.set(ControlMode.PercentOutput, -aSpeed.getDouble(0.7));
    //     }
    //     else{
    //         agitator.set(ControlMode.PercentOutput, 0);
    //     }
    // }

    private boolean agitatorActive, indexerActive;
    
    public void setAgitator(boolean set){
        agitatorActive = set;
    }
    public void setIndexer(boolean set){
        indexerActive = set;
    }

    public void setReverse(boolean reverse){
        isReversed = reverse;
    }

    public void setForced(boolean forced){
        isForced = forced;
    }

    public void update(){
        SmartDashboard.putBoolean("indexer enable", indexerActive);
        SmartDashboard.putBoolean("agitator enable", agitatorActive);
        SmartDashboard.putNumber("indexer sensor", indexerSensorRange());
        boolean indexerOverride = false;

        if(indexerSensorRange()>9){
            indexer.set(ControlMode.PercentOutput, 0.3);
            agitator.set(ControlMode.PercentOutput, 0.3);
            indexed = false;
            indexerOverride = true;
        }
        if(indexerSensorRange()<9){ 
            indexer.set(ControlMode.PercentOutput, 0);
            agitator.set(ControlMode.PercentOutput, 0);
            indexed = true;
            indexerOverride = false;
        }

        if(indexerActive){
            indexer.set(ControlMode.PercentOutput, 0.8);
        }
        else if(!indexerOverride){
            indexer.set(ControlMode.PercentOutput, 0);
        }
        if(agitatorActive){
            agitator.set(ControlMode.PercentOutput, 0.6);
        }
        else if(!indexerOverride){
            agitator.set(ControlMode.PercentOutput, 0);
        }

        if(isReversed){
            agitator.set(ControlMode.PercentOutput, -1);
            //indexer.set(ControlMode.PercentOutput, -1);
        }
    
        if(isForced){
            agitator.set(ControlMode.PercentOutput, 0.6);
            indexer.set(ControlMode.PercentOutput, 0.8);
        }
        
    }

    public double indexerSensorRange(){
        if(autoIndex){
            return indexSensor.getRange();
        }
        return -2;
    }

    /*public void updateSimple(){
        int out = -1;
        // if(joy.getRawButton(3)){
        //     agitator.set(ControlMode.PercentOutput, 0.5);
        // }
        // else if(joy.getRawButton(4)){
        //     agitator.set(ControlMode.PercentOutput, -0.5);
        // }
        // else{
        //     agitator.set(ControlMode.PercentOutput, 0);
        // }

        if(joy.getRawButton(8)){
            indexer.set(ControlMode.PercentOutput, 0.5);
            out = 1;
        }
        else if(joy.getRawButton(9)){
            indexer.set(ControlMode.PercentOutput, -0.5);
            out = 2;
        }
        else{
            agitator.set(ControlMode.PercentOutput, 0);
            out = 0;
        }

        SmartDashboard.putNumber("aaaaaaa", out);
        // if(panel.getButton(3)){
        //     agitator.set(ControlMode.PercentOutput, 0.5);
        // }
        // else if(panel.getButton(4)){
        //     agitator.set(ControlMode.PercentOutput, -0.5);
        // }
        // else{
        //     agitator.set(ControlMode.PercentOutput, 0);
        // }

        // if(panel.getButton(8)){
        //     indexer.set(ControlMode.PercentOutput, 0.5);
        // }
        // else if(panel.getButton(9)){
        //     indexer.set(ControlMode.PercentOutput, -0.5);
        // }
        // else{
        //     agitator.set(ControlMode.PercentOutput, 0);
        // }
    }*/

    // public void update(){
    //     //if there are any balls in the hopper, attempt to agitate and index
    //     boolean ballsInHopper = true;
    //     if(ballsInHopper){
    //         //if no ball in the proper index spot, run agitator and indexer until there is
    //         boolean indexed = indexSensor.getRange() > 5 && indexSensor.getRange() < 7;
    //         if(!indexed){
    //             indexer.set(ControlMode.PercentOutput, 0.5+fireOffset);
    //             agitator.set(ControlMode.PercentOutput, 0.5+fireOffset);
    //         }
    //         else{
    //             indexer.set(ControlMode.PercentOutput, fireOffset);
    //             agitator.set(ControlMode.PercentOutput, fireOffset);
    //         }
    //     }
    //     else{
    //         indexer.set(ControlMode.PercentOutput, 0);
    //         agitator.set(ControlMode.PercentOutput, 0);
    //     }
    //     fireOffset = 0;
    // }

    // public void fireBall(){
    //     fireOffset = 0.5;
    // }

    // public void setupSensor(){
    //     indexSensor.setAutomaticMode(true);
    // }
}