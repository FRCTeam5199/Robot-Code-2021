package frc.robot;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import frc.ballstuff.intaking.Hopper;
import frc.ballstuff.intaking.Intake;
import frc.ballstuff.shooting.ArticulatedHood;
import frc.ballstuff.shooting.Shooter;
import frc.ballstuff.shooting.Turret;
import frc.climber.Climber;
import frc.discordslackbot.MessageHandler;
import frc.drive.AbstractDriveManager;
import frc.drive.DriveManagerStandard;
import frc.drive.DriveManagerSwerve;
import frc.drive.auton.AbstractAutonManager;
import frc.drive.auton.followtrajectory.Trajectories;
import frc.misc.*;
import frc.motors.AbstractMotorController;
import frc.pdp.PDP;
import frc.robot.robotconfigs.DefaultConfig;
import frc.robot.robotconfigs.twentyone.CompetitionRobot2021;
import frc.robot.robotconfigs.twentyone.PracticeRobot2021;
import frc.robot.robotconfigs.twentyone.Swerve2021;
import frc.robot.robotconfigs.twentytwenty.Robot2020;
import frc.selfdiagnostics.ISimpleIssue;
import frc.selfdiagnostics.IssueHandler;
import frc.selfdiagnostics.MotorDisconnectedIssue;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Welcome. Please enjoy your stay here in programmer fun time land. And remember, IntelliJ is king
 */
@ClientSide
public class Robot extends TimedRobot {
    /**
     * If you change this ONE SINGULAR VARIABLE the ENTIRE CONFIG WILL CHANGE. Use this to select which robot you are
     * using from the list under robotconfigs
     */
    public static final Preferences preferences = Preferences.getInstance();
    public static final ArrayList<ISubsystem> subsystems = new ArrayList<>();
    private static final String DELETE_PASSWORD = "programmer funtime lanD";
    public static DefaultConfig robotSettings;
    public static AbstractDriveManager driver;
    public static Intake intake;
    public static Hopper hopper;
    public static Shooter shooter;
    public static ArticulatedHood articulatedHood;
    public static Turret turret;
    public static Chirp chirp;
    public static PDP pdp;
    public static LEDs leds;
    public static Pneumatics pneumatics;
    public static Climber climber;
    public static AbstractAutonManager autonManager;
    public static boolean SECOND_TRY;
    public static String lastFoundSong = "";
    private static long lastDisable = 0;

    /**
     * Init everything
     */
    @Override
    public void robotInit() throws IllegalStateException {
        getRestartProximity();
        robotSettings = getSettings();
        robotSettings.printMappings();
        robotSettings.printToggles();
        robotSettings.printNumbers();
        UserInterface.initRobot();
        if (robotSettings.ENABLE_MEMES) {
            Main.pipeline = ClientServerPipeline.getClient();
        }

        if (robotSettings.ENABLE_DRIVE) {
            if (robotSettings.DRIVE_BASE == AbstractDriveManager.DriveBases.STANDARD)
                driver = new DriveManagerStandard();
            else if (robotSettings.DRIVE_BASE == AbstractDriveManager.DriveBases.SWIVEL)
                driver = new DriveManagerSwerve();
        }
        if (robotSettings.ENABLE_LEDS) {
            leds = new LEDs();
        }
        if (robotSettings.ENABLE_INTAKE) {
            intake = new Intake();
        }
        if (robotSettings.ENABLE_HOPPER) {
            hopper = new Hopper();
        }
        if (robotSettings.ENABLE_SHOOTER) {
            shooter = new Shooter();
        }
        if (robotSettings.ENABLE_HOOD_ARTICULATION) {
            articulatedHood = new ArticulatedHood();
        }
        if (robotSettings.ENABLE_PNEUMATICS) {
            pneumatics = new Pneumatics();
        }
        if (robotSettings.ENABLE_TURRET) {
            turret = new Turret();
            if (robotSettings.ENABLE_DRIVE) turret.setTelemetry(driver.guidance);
        }
        if (robotSettings.ENABLE_MUSIC) {
            chirp = new Chirp();
        }
        if (robotSettings.ENABLE_CLIMBER) {
            climber = new Climber();
        }
        if (robotSettings.ENABLE_DRIVE && robotSettings.ENABLE_IMU) {
            switch (robotSettings.AUTON_TYPE) {
                case GALACTIC_SEARCH:
                    autonManager = new frc.drive.auton.galacticsearch.AutonManager(driver);
                    break;
                case FOLLOW_PATH:
                    autonManager = new frc.drive.auton.followtrajectory.AutonManager(Trajectories.SLALOM2, driver);//Trajectories.TEST_PATH, driver);
                    break;
                case GALACTIC_SCAM:
                    autonManager = new frc.drive.auton.galacticsearchtest.AutonManager(driver);
                    break;
            }
        }
        if (robotSettings.ENABLE_PDP) {
            pdp = new PDP(robotSettings.PDP_ID);
        }

        for (AbstractMotorController motor : AbstractMotorController.motorList) {
            MotorDisconnectedIssue.handleIssue(driver, motor);
            if (motor.getMotorTemperature() > 5) {
                UserInterface.motorTemperatureMonitors.put(motor, UserInterface.WARNINGS_TAB.add(motor.getName(), motor.getMotorTemperature()).withWidget(BuiltInWidgets.kNumberBar).withProperties(Map.of("Min", 30, "Max", Robot.robotSettings.OVERHEAT_THRESHOLD)));
            }
        }
        String quote = QuoteOfTheDay.getRandomQuote();
        System.out.println("\n\n" + quote);
        UserInterface.smartDashboardPutString("Quote", quote);
    }

    /**
     * Reads from the preferences what the last boot time is. Depending on current system time, sets the {@link
     * #SECOND_TRY} flag to either restart on error or to persist as best as possible. If you leave the robot on for
     * half a century then it might not work right so please refrain from that
     */
    private static void getRestartProximity() {
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
     * @return the settings for this robot based on the preferences
     * @see DefaultConfig
     */
    private static DefaultConfig getSettings() {
        String hostName = preferences.getString("hostname", "ERR_NOT_FOUND");
        System.out.println("I am " + hostName);
        switch (hostName) {
            case "2020-Comp":
                return new Robot2020();
            case "2021-Prac":
                return new PracticeRobot2021();
            case "2021-Comp":
                return new CompetitionRobot2021();
            case "2021-Swivel":
                return new Swerve2021();
            case "ERR_NOT_FOUND":
                throw new IllegalStateException("You need to ID this robot.");
            default:
                throw new IllegalStateException("Invalid ID " + hostName + " for robot. Please Re-ID.");
        }
    }

    @Override
    public void disabledInit() {
        for (ISubsystem system : subsystems) {
            system.initDisabled();
        }
        lastDisable = System.currentTimeMillis();
    }

    @Override
    public void autonomousInit() {
        AbstractMotorController.resetAllMotors();
        for (ISubsystem system : subsystems) {
            system.initAuton();
        }
    }

    @Override
    public void teleopInit() {
        AbstractMotorController.resetAllMotors();
        for (ISubsystem system : subsystems) {
            system.initTeleop();
        }
    }

    @Override
    public void testInit() {
        AbstractMotorController.resetAllMotors();
        for (ISubsystem system : subsystems) {
            system.initTest();
        }
    }

    @Override
    public void robotPeriodic() {
        if (UserInterface.PRINT_ROBOT_TOGGLES.getEntry().getBoolean(false)) {
            robotSettings.printToggles();
            UserInterface.PRINT_ROBOT_TOGGLES.getEntry().setBoolean(false);
        }
        if (UserInterface.PRINT_ROBOT_MAPPINGS.getEntry().getBoolean(false)) {
            robotSettings.printMappings();
            UserInterface.PRINT_ROBOT_MAPPINGS.getEntry().setBoolean(false);
        }
        if (UserInterface.PRINT_ROBOT_NUMBERS.getEntry().getBoolean(false)) {
            robotSettings.printNumbers();
            UserInterface.PRINT_ROBOT_NUMBERS.getEntry().setBoolean(false);
        }
        if (UserInterface.MUSIC_DISABLE_SONG_TAB.getEntry().getBoolean(false)) {
            chirp.stop();
            UserInterface.MUSIC_DISABLE_SONG_TAB.getEntry().setBoolean(false);
        }
        if (UserInterface.DELETE_DEPLOY_DIRECTORY.getEntry().getString("").equals(DELETE_PASSWORD)) {
            UserInterface.DELETE_DEPLOY_DIRECTORY.getEntry().setString("Correct password");
            deleteFolder(Filesystem.getDeployDirectory());
            throw new RuntimeException("Deleted deploy dir contents");
        }
        if (robotSettings.ENABLE_PDP) {
            pdp.updateGeneric();
        }

        for (AbstractMotorController motor : AbstractMotorController.motorList) {
            if (motor.getMotorTemperature() > 5) {
                UserInterface.motorTemperatureMonitors.get(motor).getEntry().setNumber(motor.getMotorTemperature());
            }
        }

        if (UserInterface.CLEAR_WARNINGS.getEntry().getBoolean(false)) {
            UserInterface.CLEAR_WARNINGS.getEntry().setBoolean(false);
            if (robotSettings.ENABLE_MEMES) {
                Main.pipeline.wipeSounds();
            }
            IssueHandler.issues.clear();
        }

        ISimpleIssue.robotPeriodic();
        if (robotSettings.ENABLE_MEMES) {
            Main.pipeline.updatePipeline();
            MessageHandler.persistPendingCommands();
        }
    }

    @Override
    public void disabledPeriodic() {
        if (robotSettings.ENABLE_DRIVE && System.currentTimeMillis() > lastDisable + 5000)
            driver.setBrake(false);
    }

    @Override
    public void autonomousPeriodic() {
        for (ISubsystem system : subsystems) {
            system.updateAuton();
        }
    }

    @Override
    public void teleopPeriodic() {
        for (ISubsystem system : subsystems) {
            system.updateTeleop();
        }
    }

    @Override
    public void testPeriodic() {
        for (ISubsystem system : subsystems) {
            system.updateTest();
        }
    }

    /**
     * Uses {@link #deleteFolder(File) deadly recursion} in order to maybe delete ghost files
     *
     * @param parentFolder The deploy folder/subfolders within deploy folder
     */
    private void deleteFolder(File parentFolder) {
        for (File file : parentFolder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            }
            file.delete();
            System.out.println("REMOVED FILE " + file.getName());
        }
    }
}