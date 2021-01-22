package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.drive.DriveManager;

public class Robot extends TimedRobot {
    public DriveManager driver;

    /**
     * Init everything
     */
    @Override
    public void robotInit() throws IllegalStateException{
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
        // if (RobotToggles.ENABLE_DRIVE) {
        //     driver.updateTeleop();
        // }
        //driver.driveOne();
        driver.updateTest();
    }

    @Override
    public void disabledInit() {
    }

    private static void assertValidStartConditions() throws IllegalStateException{

    }
}
