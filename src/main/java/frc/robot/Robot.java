package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.ballstuff.intaking.*;
import frc.ballstuff.shooting.*;
import frc.drive.DriveManager;

public class Robot extends TimedRobot {
    public DriveManager driver;
    public Intake intake;
    public Hopper hopper;

    /**
     * Init everything
     */
    @Override
    public void robotInit() throws IllegalStateException {
        if (RobotToggles.ENABLE_DRIVE) {
            driver = new DriveManager();
        }

        if (RobotToggles.ENABLE_INTAKE) {
            intake = new Intake();
        }

        if (RobotToggles.ENABLE_HOPPER) {
            hopper = new Hopper();
        }
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {
        if (RobotToggles.ENABLE_DRIVE) {
            //driver.updateAutonomous();
        }
        if (RobotToggles.ENABLE_INTAKE) {
            intake.updateAutonomous();
        }
    }

    @Override
    public void teleopInit() {

    }

    @Override
    public void teleopPeriodic() {
        if (RobotToggles.ENABLE_DRIVE) {
            driver.updateTeleop();
        }
        if (RobotToggles.ENABLE_INTAKE) {
            intake.updateTeleop();
        }
        if (RobotToggles.ENABLE_HOPPER) {
            hopper.update();
        }
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
        if (RobotToggles.ENABLE_DRIVE) {
            driver.updateTeleop();
        }
        if (RobotToggles.ENABLE_INTAKE) {
            intake.updateTest();
        }
    }

    @Override
    public void disabledInit() {
    }
}
