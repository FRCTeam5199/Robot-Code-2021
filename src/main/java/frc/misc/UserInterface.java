package frc.misc;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.motors.AbstractMotorController;
import frc.robot.RobotSettings;

import java.util.HashMap;

public class UserInterface {
    //TABS
    public static final ShuffleboardTab SHOOTER_TAB = Shuffleboard.getTab("Shooter"),
            DRIVE_TAB = Shuffleboard.getTab("drive"),
            PDP_TAB = Shuffleboard.getTab("Lectricity"),
            MUSICK_TAB = Shuffleboard.getTab("musick"),
            ROBOT_TAB = Shuffleboard.getTab("DANGER!"),
            WARNINGS_TAB = Shuffleboard.getTab("Warnings");

    //SHOOTER TODO make PID widget (kPIDController)
    public static final SimpleWidget SHOOTER_P = SHOOTER_TAB.add("P", RobotSettings.SHOOTER_PID.getP()),
            SHOOTER_I = SHOOTER_TAB.add("I", RobotSettings.SHOOTER_PID.getI()),
            SHOOTER_D = SHOOTER_TAB.add("D", RobotSettings.SHOOTER_PID.getD()),
            SHOOTER_F = SHOOTER_TAB.add("F", RobotSettings.SHOOTER_PID.getF()),
            SHOOTER_CONST_SPEED = SHOOTER_TAB.add("Constant Speed", 0),
            SHOOTER_CALIBRATE_PID = SHOOTER_TAB.add("Calibrate PID", false).withWidget(BuiltInWidgets.kToggleSwitch),

    //DRIVETRAIN TODO make PID widget (kPIDController)
    DRIVE_ROT_MULT = DRIVE_TAB.add("Rotation Factor", RobotSettings.TURN_SCALE),
            DRIVE_SCALE_MULT = DRIVE_TAB.add("Speed Factor", RobotSettings.DRIVE_SCALE),
            DRIVE_P = DRIVE_TAB.add("P", RobotSettings.DRIVEBASE_PID.getP()),
            DRIVE_I = DRIVE_TAB.add("I", RobotSettings.DRIVEBASE_PID.getI()),
            DRIVE_D = DRIVE_TAB.add("D", RobotSettings.DRIVEBASE_PID.getD()),
            DRIVE_F = DRIVE_TAB.add("F", RobotSettings.DRIVEBASE_PID.getF()),
            DRIVE_CALIBRATE_PID = DRIVE_TAB.add("Calibrate PID", false).withWidget(BuiltInWidgets.kToggleSwitch),
            DRIVE_COAST = DRIVE_TAB.add("Coast", true),
            DRIVE_RUMBLE_NEAR_MAX = DRIVE_TAB.add("Rumble Near Max", false).withWidget(BuiltInWidgets.kBooleanBox),
    /*
    //PDP TODO make pdp widget (kPowerDistributionPanel)
    PDP_TOTAL_ENERGY_ON_THIS_BOOT = PDP_TAB.add("Total energy on this boot", 0),
            PDP_PEAK_CURRENT = PDP_TAB.add("Peak current", 0),
            PDP_PEAK_POWER = PDP_TAB.add("Peak power", 0),
    //PDP_OTHER_ENERGY = POWER_TAB.add("Energy on current enable", 0),
    */
    //MUSICK
    MUSIC_DISABLE_SONG_TAB = MUSICK_TAB.add("Stop Song", false).withWidget(BuiltInWidgets.kToggleButton),
            MUSIC_FOUND_SONG = MUSICK_TAB.add("Found it", false),
            DELETE_DEPLOY_DIRECTORY = ROBOT_TAB.add("DELETE DEPLOY DIRECTORY", ""),
            PRINT_ROBOT_TOGGLES = ROBOT_TAB.add("Reprint robot toggles", false).withWidget(BuiltInWidgets.kToggleButton),
            PRINT_ROBOT_MAPPINGS = ROBOT_TAB.add("Reprint robot mappings", false).withWidget(BuiltInWidgets.kToggleButton),
            PRINT_ROBOT_NUMBERS = ROBOT_TAB.add("Reprint robot numbers", false).withWidget(BuiltInWidgets.kToggleButton),

    //DANGER PANEL
    GET_RANDOM_FIX = ROBOT_TAB.add("Get random fix", false).withWidget(BuiltInWidgets.kToggleButton);
    public static final HashMap<AbstractMotorController, SimpleWidget> motorTemperatureMonitors = new HashMap<>();

    //STATIC STUFF
    static SimpleWidget SHOOTER_RPM;
    static ComplexWidget MUSIC_SELECTOR, PDP_DISPLAY, SHOOTER_PID, DRIVEBASE_PID;

    //SmartDashboard
    public static void smartDashboardPutNumber(String key, double value) {
        SmartDashboard.putNumber(key, value);
    }
    public static void smartDashboardPutBoolean(String key, boolean value) {
        SmartDashboard.putBoolean(key, value);
    }
    public static void smartDashboardPutString(String key, String value) {
        SmartDashboard.putString(key, value);
    }

    //MISC
    public static void initRobot() {
        if (RobotSettings.ENABLE_MUSIC) {
            MUSIC_SELECTOR = MUSICK_TAB.add("SongSelector", Chirp.MUSIC_SELECTION).withWidget(BuiltInWidgets.kComboBoxChooser);
        }
        if (RobotSettings.ENABLE_PDP) {
            PDP_DISPLAY = PDP_TAB.add("PDPDisplay", new PowerDistributionPanel(RobotSettings.PDP_ID)).withWidget(BuiltInWidgets.kPowerDistributionPanel);
        }
        if (RobotSettings.ENABLE_DRIVE){
            DRIVEBASE_PID = DRIVE_TAB.add("Drivebase PID", RobotSettings.DRIVEBASE_PID).withWidget(BuiltInWidgets.kPIDController);
        }
        if (RobotSettings.ENABLE_SHOOTER){
            SHOOTER_PID = SHOOTER_TAB.add("Drivebase PID", RobotSettings.SHOOTER_PID).withWidget(BuiltInWidgets.kPIDController);
            SHOOTER_RPM = SHOOTER_TAB.add("RPM", 0);
        }
    }
}