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
import frc.pdp.PDP;

import java.io.File;
import java.util.ArrayList;

public class Robot extends TimedRobot {
    private static final String DELETE_PASSWORD = "programmer funtime lanD";
    private static final ShuffleboardTab ROBOT_TAB = Shuffleboard.getTab("DANGER!");
    private static final NetworkTableEntry remove = ROBOT_TAB.add("DELETE DEPLOY DIRECTORY", "").getEntry();
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
    public static PDP pdp;
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
        if (RobotToggles.ENABLE_DRIVE) {
            switch (RobotToggles.GALACTIC_SEARCH){
                case GALACTIC_SEARCH:
                //autonManager = new frc.drive.auton.galacticsearch.AutonManager(driver);
                autonManager = new frc.drive.auton.galacticsearchscam.AutonManager(driver);
                break;
                case BUT_BETTER_NOW:
                autonManager = new frc.drive.auton.butbetternow.AutonManager("RobotTestPath2", driver);
                break;
            }
        }
        pdp = new PDP(0);
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
            //chirp.loadSound("Imperial_March");
            //chirp.play();
        }
    }

    @Override
    public void robotPeriodic() {
        if (remove.getString("").equals(DELETE_PASSWORD)) {
            deleteFolder(Filesystem.getDeployDirectory());
            throw new RuntimeException("Deleted deploy dir contents");
        }
        String songName = songTab.getString("");
        foundSong.setBoolean(new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists());
        if (new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists() && !songName.equals(lastFoundSong)) {
            chirp.loadSound(songName);
            lastFoundSong = songName;
        }

        pdp.update();
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

    private void deleteFolder(File parentfolder){
        for (File file : parentfolder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            }
            file.delete();
            System.out.println("REMOVED FILE " + file.getName());
        }
    }
}