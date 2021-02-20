package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.ballstuff.intaking.Hopper;
import frc.ballstuff.intaking.Intake;
import frc.ballstuff.shooting.Shooter;
import frc.ballstuff.shooting.Turret;
import frc.drive.DriveManager;
import frc.drive.auton.AbstractAutonManager;
import frc.vision.BallPhoton;
import frc.vision.GoalPhoton;
import frc.misc.Chirp;

import java.io.File;

public class Robot extends TimedRobot {
    private static final ShuffleboardTab MUSICK_TAB = Shuffleboard.getTab("musick");
    private static String song = "WiiSports";
    private static final NetworkTableEntry songTab = MUSICK_TAB.add("Song" , song).getEntry();
    private static boolean songFound = false;
    private static final NetworkTableEntry foundSong = MUSICK_TAB.add("Found it" , songFound).getEntry();
    private static String lastFoundSong = "";
    public static DriveManager driver;
    public static Intake intake;
    public static Hopper hopper;
    public static Shooter shooter;
    public static Turret turret;
    public static Chirp chirp;
    public static GoalPhoton goalPhoton;
    public static BallPhoton ballPhoton;
    public static AbstractAutonManager autonManager;

    private static long lastDisable = 0;

    /**
     * Init everything
     */
    @Override
    public void robotInit() throws IllegalStateException {
        RobotMap.printMappings();
        RobotToggles.printToggles();
        RobotNumbers.printNumbers();
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
            ballPhoton = new BallPhoton();
        }
        if (RobotToggles.ENABLE_MUSIC) {
            chirp = new Chirp();
        }
    }

    @Override
    public void disabledInit() {
        if (RobotToggles.ENABLE_SHOOTER) {
            turret.disabledInit();
        }
        if (RobotToggles.ENABLE_DRIVE) {
            driver.setBrake(true);
            lastDisable = System.currentTimeMillis();
        }
        if (RobotToggles.ENABLE_MUSIC) {
            if (chirp.isPlaying()) {
                chirp.stop();
            }
        }
    }

    @Override
    public void autonomousInit() {
        if (RobotToggles.ENABLE_VISION)
            ballPhoton.updateAuton();
        if (RobotToggles.ENABLE_DRIVE) {
            driver.initGeneric();
            driver.guidance.resetEncoders();
            if (RobotToggles.GALACTIC_SEARCH) {
                autonManager = new frc.drive.auton.galacticsearch.AutonManager(driver).initAuton();
            } else
                autonManager = new frc.drive.auton.butbetternow.AutonManager("RobotTestPath2", driver);
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
        if (RobotToggles.ENABLE_MUSIC) {
            chirp.loadSound("Imperial_March");
            //chirp.play();
        }
    }

    @Override
    public void robotPeriodic() {
        String songName = songTab.getString("");
        foundSong.setBoolean(new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists());
        if (new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists() && !songName.equals(lastFoundSong)){
            chirp.loadSound(songName);
            lastFoundSong = songName;
        }
    }

    @Override
    public void disabledPeriodic() {
        //Do nothing
        if (RobotToggles.ENABLE_DRIVE && System.currentTimeMillis() > lastDisable + 5000)
            driver.setBrake(false);
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
                ballPhoton.updateAuton();
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
    public void testPeriodic() {
        if (RobotToggles.ENABLE_DRIVE) {
            driver.updateTest();
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
        if (RobotToggles.ENABLE_MUSIC) {
            if (!chirp.isPlaying()) {
                chirp.play();
            }
        }
    }
}
