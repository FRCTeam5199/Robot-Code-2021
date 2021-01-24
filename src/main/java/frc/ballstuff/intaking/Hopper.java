package frc.ballstuff.intaking;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.ButtonPanel;
import frc.misc.ISubsystem;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

public class Hopper implements ISubsystem {
    public VictorSPX agitator, indexer;
    public Rev2mDistanceSensor indexSensor;
    public boolean autoIndex = true;
    public boolean indexed = false;
    private double fireOffset = 0;
    // private ShuffleboardTab tab = Shuffleboard.getTab("balls");
    // private NetworkTableEntry aSpeed = tab.add("Agitator Speed", 0.6).getEntry();
    // private NetworkTableEntry iSpeed = tab.add("Indexer Speed", 0.7).getEntry();
    // public NetworkTableEntry visionOverride = tab.add("VISION OVERRIDE", false).getEntry();
    // public NetworkTableEntry spinupOverride = tab.add("SPINUP OVERRIDE", false).getEntry();
    // public NetworkTableEntry disableOverride = tab.add("LOADING DISABLE", false).getEntry();
    private ButtonPanel panel;
    private Joystick joy;
    private boolean isReversed = false;
    private boolean isForced = false;
    private boolean agitatorActive, indexerActive;

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

    public Hopper() {
        init();
    }
    
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

    @Override
    public void init() {
        if (autoIndex) {
            indexSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighAccuracy);
            indexSensor.setEnabled(true);
            indexSensor.setAutomaticMode(true);
        }
        agitator = new VictorSPX(RobotMap.AGITATOR_MOTOR);
        indexer = new VictorSPX(RobotMap.INDEXER_MOTOR);
        panel = new ButtonPanel(RobotNumbers.BUTTON_PANEL_SLOT);
        joy = new Joystick(RobotNumbers.FLIGHT_STICK_SLOT);
    }

    @Override
    public void updateGeneric() {
        if (RobotToggles.DEBUG) {
            SmartDashboard.putBoolean("indexer enable", indexerActive);
            SmartDashboard.putBoolean("agitator enable", agitatorActive);
            SmartDashboard.putNumber("indexer sensor", indexerSensorRange());
        }
        if (indexerSensorRange() > 9) {
            indexer.set(ControlMode.PercentOutput, 0.8); //0.3
            agitator.set(ControlMode.PercentOutput, 0.6); //0.3
            indexed = false;
        } else {
            indexer.set(ControlMode.PercentOutput, 0);
            agitator.set(ControlMode.PercentOutput, 0);
            indexed = true;
        }
    }

    public double indexerSensorRange() {
        if (autoIndex) {
            return indexSensor.getRange();
        }
        return -2;
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }
    
/*
    public void updateSimple() {
        //SmartDashboard.putNumber("Sensor Range", indexSensor.getRange());
        System.out.println("Index Sensor: " + indexSensor.getRange());
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
        if (joy.getRawButton(8)) {
            indexer.set(ControlMode.PercentOutput, 0.5);
            out = 1;
        } else if (joy.getRawButton(9)) {
            indexer.set(ControlMode.PercentOutput, -0.5);
            out = 2;
        } else {
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
    }

    public void updateStuff() {
        //if there are any balls in the hopper, attempt to agitate and index
        boolean ballsInHopper = true;
        if (ballsInHopper) {
            //if no ball in the proper index spot, run agitator and indexer until there is
            boolean indexed = indexSensor.getRange() > 5 && indexSensor.getRange() < 7;
            if (!indexed) {
                indexer.set(ControlMode.PercentOutput, 0.5 + fireOffset);
                agitator.set(ControlMode.PercentOutput, 0.5 + fireOffset);
            } else {
                indexer.set(ControlMode.PercentOutput, fireOffset);
                agitator.set(ControlMode.PercentOutput, fireOffset);
            }
        } else {
            indexer.set(ControlMode.PercentOutput, 0);
            agitator.set(ControlMode.PercentOutput, 0);
        }
        fireOffset = 0;
    }

    public void fireBall() {
        fireOffset = 0.5;
    }
    */
}