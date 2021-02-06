package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import frc.ballstuff.intaking.Hopper;
import frc.ballstuff.intaking.Intake;
import frc.ballstuff.shooting.Shooter;
import frc.ballstuff.shooting.Turret;
import frc.drive.DriveManager;
import frc.drive.auton.butbetternow.AutonManager;
import frc.vision.GoalPhoton;

public class Robot extends TimedRobot {
    public static DriveManager driver;
    public static Intake intake;
    public static Hopper hopper;
    public static Shooter shooter;
    public static Turret turret;
    public static GoalPhoton goalPhoton;
    public static AutonManager autonManager;
    public static Command autonomousCommand;

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
            if (RobotToggles.ENABLE_DRIVE) turret.setTelemetry(driver.guidance);
        }
        if (RobotToggles.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
        }
    }
    
    @Override
    public void autonomousInit() {
        if (RobotToggles.ENABLE_DRIVE) {
            driver.initGeneric();
            driver.guidance.resetEncoders();
            autonManager = new AutonManager("ForwardAndBack", driver);
        }
    }

    @Override
    public void teleopInit() {
        if (RobotToggles.ENABLE_SHOOTER) {
            turret.teleopInit();
        }
        if (RobotToggles.ENABLE_DRIVE) {
            driver.initGeneric();
        }
    }

    @Override
    public void testInit() {
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledPeriodic() {
        //Do nothing
    }

    @Override
    public void autonomousPeriodic() {
        if (RobotToggles.ENABLE_INTAKE) {
            intake.updateAuton();
        }
        if (RobotToggles.ENABLE_SHOOTER) {
            shooter.updateAuton();
        }
        if (RobotToggles.ENABLE_VISION) {
            if (RobotToggles.USE_PHOTONVISION) {
                goalPhoton.updateAuton();
            }
        }
        autonManager.updateAuton();
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
            turret.updateTeleop();
        }
        if (RobotToggles.ENABLE_VISION) {
            if (RobotToggles.USE_PHOTONVISION) {
                goalPhoton.updateTeleop();
            }
        }
    }
    @Override
    public void disabledInit() {
        if (RobotToggles.ENABLE_SHOOTER) {
            turret.disabledInit();
        }
        if (RobotToggles.ENABLE_DRIVE) {
            driver.setBrake(true);
        }
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
            turret.updateTest();
        }
        if (RobotToggles.ENABLE_HOPPER) {
            hopper.updateTest();
        }
        if (RobotToggles.ENABLE_VISION) {
            if (RobotToggles.USE_PHOTONVISION) {
                goalPhoton.updateTest();
            }
        }
    }    
}
