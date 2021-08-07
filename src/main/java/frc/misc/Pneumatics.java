package frc.misc;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

import static frc.robot.Robot.robotSettings;

/**
 * I accidentally deleted this, so here we go again. Allows you to control all of the solenoids for all of your air
 * powered needs (pnoomatics)
 *
 * @author Smaltin
 */
public class Pneumatics implements ISubsystem {
    public DoubleSolenoid solenoidIntake;
    public DoubleSolenoid climberLock;
    public Solenoid shooterCooling;

    public Pneumatics() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        if (robotSettings.ENABLE_INTAKE && robotSettings.ENABLE_PNEUMATICS) {
            solenoidIntake = new DoubleSolenoid(robotSettings.PCM_ID, robotSettings.INTAKE_OUT_ID, robotSettings.INTAKE_IN_ID);
        }
        if (robotSettings.ENABLE_CLIMBER && robotSettings.ENABLE_PNEUMATICS) {
            climberLock = new DoubleSolenoid(robotSettings.PCM_ID, robotSettings.CLIMBER_IN_ID, robotSettings.CLIMBER_OUT_ID);
        }
        if (robotSettings.ENABLE_SHOOTER && robotSettings.ENABLE_PNEUMATICS && robotSettings.ENABLE_SHOOTER_COOLING) {
            shooterCooling = new Solenoid(robotSettings.PCM_ID, robotSettings.SHOOTER_COOLING_ID);
        }
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return SubsystemStatus.NOMINAL;
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {

    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "Pneumatics";
    }
}
