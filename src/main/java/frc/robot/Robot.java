package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
import frc.robot.robotconfigs.twentyone.CompetitionRobot2021;
import frc.robot.robotconfigs.twentyone.PracticeRobot2021;
import frc.robot.robotconfigs.twentytwenty.Robot2020;
import frc.vision.BallPhoton;
import frc.vision.GoalPhoton;
import frc.vision.IVision;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Welcome. Please enjoy your stay here in programmer fun time land. And remember, IntelliJ is king
 */
public class Robot extends TimedRobot {
    /**
     * If you change this ONE SINGULAR VARIBLE the ENTIRE CONFIG WILL CHANGE. Use this to select which robot you are
     * using from the list under robotconfigs
     */
    public static final Preferences preferences = Preferences.getInstance();
    public static final ArrayList<ISubsystem> subsytems = new ArrayList<>();
    public static final ShuffleboardTab MUSICK_TAB = Shuffleboard.getTab("musick"),
            ROBOT_TAB = Shuffleboard.getTab("DANGER!"),
            FAILURES_TAB = Shuffleboard.getTab("Warnings");
    public static final boolean songFound = false;
    public static final SendableChooser<List<String>> MUSIC_SELECTION;
    public static final NetworkTableEntry //songTab,
            disableSongTab = MUSICK_TAB.add("Stop Song", false).withWidget(BuiltInWidgets.kToggleButton).getEntry(),
            foundSong = MUSICK_TAB.add("Found it", songFound).getEntry();

    private static final String DELETE_PASSWORD = "programmer funtime lanD";
    private static final NetworkTableEntry remove = ROBOT_TAB.add("DELETE DEPLOY DIRECTORY", "").getEntry(),
            printToggles = ROBOT_TAB.add("Reprint robot toggles", false).withWidget(BuiltInWidgets.kToggleButton).getEntry(),
            printMappings = ROBOT_TAB.add("Reprint robot mappings", false).withWidget(BuiltInWidgets.kToggleButton).getEntry(),
            printNumbers = ROBOT_TAB.add("Reprint robot numbers", false).withWidget(BuiltInWidgets.kToggleButton).getEntry();
    public static DefaultConfig settingsFile;
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
    public static boolean SECOND_TRY;
    public static String lastFoundSong = "";
    private static long lastDisable = 0;

    static {
        MUSIC_SELECTION = new SendableChooser<>();
        Chirp.getSongs(MUSIC_SELECTION);
        MUSICK_TAB.add(MUSIC_SELECTION);
    }

    /**
     * Init everything
     */
    @Override
    public void robotInit() throws IllegalStateException {
        //Yes, it has to be a string otherwise it truncates it after 6 digits
        getRestartProximety();
        getSettings();
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
        }
        if (RobotSettings.ENABLE_TURRET) {
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
                case FOLLOW_PATH:
                    //autonManager = new frc.drive.auton.followtrajectory.AutonManager("RobotTestPath2", driver);
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

    private static void getRestartProximety() {
        long lastBoot = Long.parseLong(preferences.getString("lastboot", "0"));
        long currentBoot = System.currentTimeMillis();
        preferences.putString("lastboot", "0" + currentBoot);
        if (lastBoot > currentBoot) {
            SECOND_TRY = false;
        } else if (lastBoot > 1614461266977L) {
            SECOND_TRY = currentBoot - lastBoot < 30000;
        } else if (lastBoot < 1614461266977L && currentBoot < 1614461266977L) {
            SECOND_TRY = currentBoot - lastBoot < 30000;
        } else {
            SECOND_TRY = false;
        }
    }

    /**
     * Loads settings based on the id of the robot.
     *
     * @see DefaultConfig
     */
    private static void getSettings() {
        String hostName = preferences.getString("hostname", "Default");
        System.out.println("I am " + hostName);
        switch (hostName) {
            case "2020-Comp":
                settingsFile = new Robot2020();
                break;
            case "2021-Prac":
                settingsFile = new PracticeRobot2021();
                break;
            case "2021-Comp":
                settingsFile = new CompetitionRobot2021();
                break;
            default:
                throw new IllegalStateException("You need to ID this robot.");
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
                //chirp.stop();
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
            chirp.initDisabled();
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