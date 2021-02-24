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
import frc.misc.LEDs;
import frc.pdp.PDP;
import frc.robot.robotconfigs.DefaultConfig;
import frc.robot.robotconfigs.twentytwenty.Robot2020GalacticSearch;
import frc.vision.BallPhoton;
import frc.vision.GoalPhoton;
import frc.vision.IVision;

import java.io.File;
import java.util.ArrayList;

public class Robot extends TimedRobot {
    /**
     * If you change this ONE SINGULAR VARIBLE the ENTIRE CONFIG WILL CHANGE. Use this to select which robot you are
     * using from the list under robotconfigs
     */
    public static final DefaultConfig getNumbersFrom = new Robot2020GalacticSearch();
    private static final String DELETE_PASSWORD = "programmer funtime lanD";
    private static final ShuffleboardTab ROBOT_TAB = Shuffleboard.getTab("DANGER!");
    private static final NetworkTableEntry remove = ROBOT_TAB.add("DELETE DEPLOY DIRECTORY", "").getEntry(),
            printToggles = ROBOT_TAB.add("Reprint robot toggles", false).getEntry(),
            printMappings = ROBOT_TAB.add("Reprint robot mappings", false).getEntry(),
            printNumbers = ROBOT_TAB.add("Reprint robot numbers", false).getEntry();
    private static final ShuffleboardTab MUSICK_TAB = Shuffleboard.getTab("musick");
    private static final NetworkTableEntry songTab = MUSICK_TAB.add("Song", "").getEntry(),
            disableSongTab = MUSICK_TAB.add("Stop Song", false).getEntry();
    private static final boolean songFound = false;
    private static final NetworkTableEntry foundSong = MUSICK_TAB.add("Found it", songFound).getEntry();
    public static DriveManager driver;
    public static Intake intake;
    public static Hopper hopper;
    public static Shooter shooter;
    public static Turret turret;
    public static Chirp chirp;
    public static PDP pdp;
    public static LEDs leds;
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
        RobotSettings.printMappings();
        RobotSettings.printToggles();
        RobotSettings.printNumbers();
        if (RobotSettings.ENABLE_VISION) {
            goalPhoton = new GoalPhoton();
            ballPhoton = new BallPhoton();
        }
        if (RobotSettings.ENABLE_DRIVE) {
            driver = new DriveManager();
        }
        if (RobotSettings.ENABLE_INTAKE) {
            intake = new Intake();
        }
        if (RobotSettings.ENABLE_HOPPER) {
            hopper = new Hopper();
        }
        if (RobotSettings.ENABLE_SHOOTER) {
            shooter = new Shooter();
            turret = new Turret();
            if (RobotSettings.ENABLE_DRIVE) turret.setTelemetry(driver.guidance);
        }
        if (RobotSettings.ENABLE_MUSIC) {
            chirp = new Chirp();
        }
        if (RobotSettings.ENABLE_DRIVE) {
            switch (RobotSettings.AUTON_MODE) {
                case GALACTIC_SEARCH:
                    //autonManager = new frc.drive.auton.galacticsearch.AutonManager(driver);
                    autonManager = new frc.drive.auton.galacticsearch.AutonManager(driver);
                    break;
                case BUT_BETTER_NOW:
                    autonManager = new frc.drive.auton.butbetternow.AutonManager("RobotTestPath2", driver);
                    break;
                case GALACTIC_SCAM:
                    autonManager = new frc.drive.auton.galacticsearchscam.AutonManager(driver);
                    break;
            }
        }
        if (RobotSettings.ENABLE_PDP) {
            pdp = new PDP(RobotSettings.PDP_ID);
        }
    }

    @Override
    public void disabledInit() {
        for (ISubsystem system : subsytems) {
            system.initDisabled();
        }
        lastDisable = System.currentTimeMillis();
        if (RobotSettings.ENABLE_MUSIC) {
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
        if (RobotSettings.ENABLE_MUSIC) {
            //chirp.loadSound("Imperial_March");
            //chirp.play();
        }
    }

    @Override
    public void robotPeriodic() {
        if (printToggles.getBoolean(false)) {
            RobotSettings.printToggles();
            printToggles.setBoolean(false);
        }
        if (printMappings.getBoolean(false)) {
            RobotSettings.printMappings();
            printMappings.setBoolean(false);
        }
        if (printNumbers.getBoolean(false)) {
            RobotSettings.printNumbers();
            printNumbers.setBoolean(false);
        }
        if (disableSongTab.getBoolean(false)) {
            chirp.stop();
            disableSongTab.setBoolean(false);
        }
        if (remove.getString("").equals(DELETE_PASSWORD)) {
            remove.setString("Correct password");
            deleteFolder(Filesystem.getDeployDirectory());
            throw new RuntimeException("Deleted deploy dir contents");
        }
        if (RobotSettings.ENABLE_PDP) {
            pdp.update();
        }
    }

    @Override
    public void disabledPeriodic() {
        //Do nothing
        if (RobotSettings.ENABLE_MUSIC) {
            String songName = songTab.getString("");
            foundSong.setBoolean(new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists());
            if (!songName.equals("") && !chirp.isPlaying()) {
                if (new File(Filesystem.getDeployDirectory().toPath().resolve("sounds/" + songName + ".chrp").toString()).exists() && !songName.equals(lastFoundSong)) {
                    lastFoundSong = songName;
                    chirp.loadSound(songName);
                    chirp.play();
                }
            } else {
                chirp.updateDisabled();
            }
        }
        if (RobotSettings.ENABLE_DRIVE && System.currentTimeMillis() > lastDisable + 5000)
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

    private void deleteFolder(File parentfolder) {
        for (File file : parentfolder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            }
            file.delete();
            System.out.println("REMOVED FILE " + file.getName());
        }
    }
}