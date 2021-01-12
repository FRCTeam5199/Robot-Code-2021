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
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.controllers.*;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.util.Logger;
//import frc.util.Permalogger;
import frc.vision.GoalChameleon;

public class Intake{ 
    private VictorSPX victor;
    private ButtonPanel panel;
    // private ShuffleboardTab tab = Shuffleboard.getTab("balls");
    // private NetworkTableEntry speedEntry = tab.add("Intake Speed", 0).getEntry();
    // private NetworkTableEntry pneumaticNo = tab.add("Pneumatic Number", 0).getEntry();
    // private NetworkTableEntry pneumaticActuated = tab.add("Pneumatic Actuated", false).getEntry();
    private DoubleSolenoid solenoidIntake;

    private Solenoid sol1, sol7;

    public void init(){
        victor = new VictorSPX(RobotMap.intakeMotor);
        panel = new ButtonPanel(3);
        solenoidIntake = new DoubleSolenoid(RobotMap.pcm, RobotMap.intakeOut, RobotMap.intakeIn);

        sol1 = new Solenoid(RobotMap.pcm, 1);
        sol7 = new Solenoid(RobotMap.pcm, 7);

        sol1.set(false);
        sol7.set(false);
    }

    public void closeUnusedSolenoids(){
        sol1.set(false);
        sol7.set(false);
    }

    private int intakeMult;
    /**
     * Set intake direction
     * @param input - -1 for out, 1 for in, 0 for none
     */
    public void setIntake(int input){
        intakeMult = input;
    }
    public void update(){
        victor.set(ControlMode.PercentOutput, 0.8*intakeMult);
    }

    /**
     * Set the deployment of the intake
     * @param deployed - true out, false in
     */
    public void setDeploy(boolean deployed){
        if(deployed){
            solenoidIntake.set(Value.kForward);
        }
        else{
            solenoidIntake.set(Value.kReverse);
        }
    }

    //private int lastSolenoid = 0;
    //, solenoidShifterL, solenoidShifterR;
    public void initPneumatic(){
        

        // solenoid8 = new Solenoid(23, 8);
        // solenoid9 = new Solenoid(23, 9);
        // solenoid10 = new Solenoid(23, 10);
        // solenoid11 = new Solenoid(23, 11);
        // solenoid12 = new Solenoid(23, 12);
    }

    // public void setPneumaticTest(){
    //     boolean actuate = pneumaticActuated.getBoolean(false);
    //     switch((int)pneumaticNo.getDouble(0)){
    //         case 0 :
    //         if(actuate){
    //             solenoidIntake.set(Value.kForward);
    //         }
    //         if(!actuate){
    //             solenoidIntake.set(Value.kReverse);
    //         }
    //         break;
    //         case 1 :
            
    //         break;
    //         // case 8 :
    //         // solenoid8.set(actuate);
    //         // break;
    //         // case 9 :
    //         // solenoid9.set(actuate);
    //         // break;
    //         // case 10 :
    //         // solenoid10.set(actuate);
    //         // break;
    //         // case 11 :
    //         // solenoid11.set(actuate);
    //         // break;
    //         // case 12 :
    //         // solenoid12.set(actuate);
    //         // break;
    //     }
    //     // if((int)pneumaticNo.getDouble(0) != lastSolenoid){
    //     //     if(solenoid != null){
    //     //         System.out.println("Closing Solenoid");
    //     //         solenoid.close();
    //     //         System.out.println("Solenoid Closed");
    //     //     }
    //     //     else{
    //     //         System.out.println("Opening Solenoid");
    //     //         solenoid = new Solenoid(23, (int)pneumaticNo.getDouble(0));
    //     //         System.out.println("Solenoid Opened");
    //     //     }
    //     // }
    //     // if(solenoid != null){
    //     //     solenoid.set(pneumaticActuated.getBoolean(false));
    //     //     System.out.println("Actuating Solenoid");
    //     // }
    // }

    // public void updateSimple(){
    //     if(panel.getButton(1)){
    //         victor.set(ControlMode.PercentOutput, speedEntry.getDouble(0.6));
    //     }
    //     else if(panel.getButton(2)){
    //         victor.set(ControlMode.PercentOutput, -speedEntry.getDouble(0.6));
    //     }
    //     else{
    //         victor.set(ControlMode.PercentOutput, 0);
    //     }
    // }
}