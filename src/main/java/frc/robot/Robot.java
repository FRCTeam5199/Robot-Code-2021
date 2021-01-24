package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.ballstuff.intaking.Hopper;
import frc.ballstuff.intaking.Intake;
import frc.ballstuff.shooting.Shooter;
import frc.ballstuff.shooting.Turret;
import frc.drive.DriveManager;

public class Robot extends TimedRobot {
    public DriveManager driver;
    public Intake intake;
    public Hopper hopper;
    public Shooter shooter;
    public Turret turret;

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

        if (RobotToggles.ENABLE_SHOOTER) {
            shooter = new Shooter();
            turret = new Turret();
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
            intake.updateAuton();
        }
        if (RobotToggles.ENABLE_SHOOTER) {
            //shooter.updateAutonomous();
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
            hopper.updateTeleop();
        }
        if (RobotToggles.ENABLE_SHOOTER) {
            shooter.updateTeleop();
            turret.update();
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
        if (RobotToggles.ENABLE_SHOOTER) {
            shooter.updateTest();
            turret.update();
        }
        if (RobotToggles.ENABLE_HOPPER) {
            hopper.updateTest();
        }
    }

    @Override
    public void disabledInit() {
    }
}
