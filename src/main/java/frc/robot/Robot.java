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
import frc.misc.Chirp;
import frc.misc.ISubsystem;
import frc.vision.IVision;
import frc.vision.BallPhoton;
import frc.vision.GoalPhoton;

import java.io.File;
import java.util.ArrayList;

public class Robot extends TimedRobot {
    private static final ShuffleboardTab MUSICK_TAB = Shuffleboard.getTab("musick");
    private static final NetworkTableEntry songTab = MUSICK_TAB.add("Song", "WiiSports").getEntry();
    private static final boolean songFound = false;
    private static final NetworkTableEntry foundSong = MUSICK_TAB.add("Found it", songFound).getEntry();
    public static DriveManager driver;
    public static Intake intake;
    public static Hopper hopper;
    public static Shooter shooter;
    public static Turret turret;
    public static Chirp chirp;
    public static IVision goalPhoton, ballPhoton;
    public static AbstractAutonManager autonManager;
    private static String lastFoundSong = "";
    private static long lastDisable = 0;

    public static final ArrayList<ISubsystem> subsytems = new ArrayList<>();

    /**
     * Init everything
     */
    @Override
    public void robotInit() throws IllegalStateException {
        RobotMap.printMappings();
        RobotToggles.printToggles();
        RobotNumbers.printNumbers();
        if (RobotToggles.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
            ballPhoton = new BallPhoton();
        }
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
        if (RobotToggles.ENABLE_MUSIC) {
            chirp = new Chirp();
        }
    }

    @Override
    public void disabledInit() {
        for (ISubsystem system : subsytems) {
            system.initDisabled();
        }
        lastDisable = System.currentTimeMillis();
        if (RobotToggles.ENABLE_MUSIC) {
            if (chirp.isPlaying()) {
                chirp.stop();
            }
        }
    }

    @Override
    public void autonomousInit() {
        for (ISubsystem system : subsytems) {
            system.initAuton();
        }
        if (RobotToggles.ENABLE_DRIVE) {
            if (RobotToggles.GALACTIC_SEARCH) {
                autonManager = new frc.drive.auton.galacticsearch.AutonManager(driver);
            } else {
                autonManager = new frc.drive.auton.butbetternow.AutonManager("RobotTestPath2", driver);
            }
            autonManager.initAuton();
        }
    }

    @Override
    public void teleopInit() {
        for (ISubsystem system : subsytems) {
            system.initTeleop();
        }
    }

    @Override
    public void testInit() {
        for (ISubsystem system : subsytems) {
            system.initTest();
        }
        if (RobotToggles.ENABLE_MUSIC) {
            chirp.loadSound("Imperial_March");
            //chirp.play();
        }
    }

    @Override
    public void robotPeriodic() {
        String songName = songTab.getString("");
        foundSong.setBoolean(new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists());
        if (new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists() && !songName.equals(lastFoundSong)) {
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
        for (ISubsystem system : subsytems) {
            system.updateAuton();
        }
    }

    @Override
    public void teleopPeriodic() {
        for (ISubsystem system : subsytems) {
            system.updateTeleop();
        }
    }

    @Override
    public void testPeriodic() {
        for (ISubsystem system : subsytems) {
            system.updateTest();
        }
    }
}
