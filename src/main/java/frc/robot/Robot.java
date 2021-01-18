package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.drive.DriveManager;

public class Robot extends TimedRobot {
    public DriveManager driver;

    @Override
    public void robotInit() {
        if (RobotToggles.ENABLE_DRIVE) {
            driver = new DriveManager();
        }
        if (RobotToggles.ENABLE_INTAKE) {
            //init intake
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
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
        if (RobotToggles.ENABLE_DRIVE) {
            driver.updateTeleop();
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
    }

    @Override
    public void disabledInit() {
    }
}
