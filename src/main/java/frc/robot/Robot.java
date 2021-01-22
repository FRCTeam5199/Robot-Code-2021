package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.ballstuff.intake.Intake;
import frc.drive.DriveManager;

public class Robot extends TimedRobot {
    public DriveManager driver;
    public Intake intake;

    private static void assertValidStartConditions() throws IllegalStateException {

    }

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
