package frc.climber;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.controllers.*;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.util.Logger;

public class Climber{
    private VictorSPX motorA, motorB;
    //private XBoxController xbox;
    private JoystickController joy;
    private ButtonPanel panel;

    public String[] data = {"match time", "init time", "speed", "motor current"};
    public String[] units = {"seconds", "seconds", "rpm", "A"};

    // private ShuffleboardTab tab = Shuffleboard.getTab("climber");
    // private NetworkTableEntry speedEntry = tab.add("Climber Speed", 0.25).getEntry();
    private Timer climberTimer;

    public Solenoid buddyLock;
    private DoubleSolenoid climberLock;

    /**
     * Update the Climber object(run every tick)
     */
    public void update(){
        //double push = xbox.getRTrigger();
        //double pull = xbox.getLTrigger();
        // if(xbox.getButton(6)){
        //     drive(-0.5);
        // }
        // else if(xbox.getButton(5)){
        //     drive(0.5);
        // }
        // else{
        //     drive(0);
        // }
        if(panel.getButton(1)){
            climberLock.set(Value.kReverse);
            drive(0.7);
        }
        else if(panel.getButton(2)){
            drive(-0.7);
        }
        else{
            drive(0);
        }
        //check if lock button is pressed, if it is lock the climber
        //start a timer when the button goes down, stop when it goes up, check time, reset, if time > 1s and button still down unlock climber
        // if(false/*button pressed*/){
        //     //lock climber
        //     climberTimer.start();
        // }
        // if(false/*button unpressed*/){
        //     climberTimer.stop();
        //     if(climberTimer.get()>1.5){
        //         //unlock climber
        //     }
        //     climberTimer.reset();
        // }
        
        if(panel.getButtonDown(3)){ //lock
            climberLock.set(Value.kForward);
        }
        if(panel.getButtonDown(4)){ //unlock
            climberLock.set(Value.kReverse);
        }

        if(panel.getButtonDown(5)){ //drop buddy
            buddyLock.set(true);
        }
    }

    /**
     * Drive both motors on a -1 to 1 scale
     * @param speed - motor speed on a -1 to 1 scale
     */
    private void drive(double speed){
        motorA.set(ControlMode.PercentOutput, speed);
        motorB.set(ControlMode.PercentOutput, -speed);
    }

    /**
     * Initialize the Climber object(run during robotInit())
     */
    public void init(){
        motorA = new VictorSPX(RobotMap.climberA); //climberL
        motorB = new VictorSPX(RobotMap.climberB);
        motorA.configOpenloopRamp(0);
        motorB.configOpenloopRamp(0);
        //xbox = new XBoxController(0);
        joy = new JoystickController(1);
        panel = new ButtonPanel(2);
        motorA.setNeutralMode(NeutralMode.Brake);
        motorB.setNeutralMode(NeutralMode.Brake);
        climberTimer = new Timer();
        climberTimer.stop();
        climberTimer.reset();
        climberLock = new DoubleSolenoid(RobotMap.pcm, RobotMap.climberLockIn, RobotMap.climberLockOut);
        buddyLock = new Solenoid(RobotMap.pcm, RobotMap.buddyUnlock);
        climberLock.set(Value.kReverse);
        //motorA.set(ControlMode.Follower, RobotMap.climberB);
        buddyLock.set(false);
        climberLock.set(Value.kForward);
    }
}