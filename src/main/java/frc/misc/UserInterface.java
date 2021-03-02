package frc.misc;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotSettings;

import java.util.HashMap;

public class UserInterface {
    //SHOOTER TODO make PID widget (kPIDController)
    public static final ShuffleboardTab SHOOTER_TAB = Shuffleboard.getTab("Shooter"),
            DRIVE_TAB = Shuffleboard.getTab("drive"),
            pdpTab = Shuffleboard.getTab("Lectricity"),
            MUSICK_TAB = Shuffleboard.getTab("musick"),
            ROBOT_TAB = Shuffleboard.getTab("DANGER!"),
            FAILURES_TAB = Shuffleboard.getTab("Warnings");
    public static final SimpleWidget SHOOTER_P = SHOOTER_TAB.add("P", RobotSettings.SHOOTER_PID.getP()),
            SHOOTER_I = SHOOTER_TAB.add("I", RobotSettings.SHOOTER_PID.getI()),
            SHOOTER_D = SHOOTER_TAB.add("D", RobotSettings.SHOOTER_PID.getD()),
            SHOOTER_F = SHOOTER_TAB.add("F", RobotSettings.SHOOTER_PID.getF()),
            SHOOTER_CONST_SPEED = SHOOTER_TAB.add("Constant Speed", 0),
            SHOOTER_CALIBRATE_PID = SHOOTER_TAB.add("Recalibrate PID", false),

    //DRIVETRAIN TODO make PID widget (kPIDController)
            DRIVE_ROT_MULT = DRIVE_TAB.add("Rotation Factor", RobotSettings.TURN_SCALE),
            DRIVE_SCALE_MULT = DRIVE_TAB.add("Speed Factor", RobotSettings.DRIVE_SCALE),
            DRIVE_P = DRIVE_TAB.add("P", RobotSettings.DRIVEBASE_PID.getP()),
            DRIVE_I = DRIVE_TAB.add("I", RobotSettings.DRIVEBASE_PID.getI()),
            DRIVE_D = DRIVE_TAB.add("D", RobotSettings.DRIVEBASE_PID.getD()),
            DRIVE_F = DRIVE_TAB.add("F", RobotSettings.DRIVEBASE_PID.getF()),
            DRIVE_CALIBRATE_PID = DRIVE_TAB.add("Calibrate PID", false),
            DRIVE_COAST = DRIVE_TAB.add("Coast", true),

    //PDP TODO make pdp widget (kPowerDistributionPanel)
            PDP_TOTAL_ENERGY_ON_THIS_BOOT = pdpTab.add("Total energy on this boot", 0),
            PDP_PEAK_CURRENT = pdpTab.add("Peak current", 0),
            PDP_PEAK_POWER = pdpTab.add("Peak power", 0),
    //PDP_OTHER_ENERGY = POWER_TAB.add("Energy on current enable", 0),

            //MUSICK
            MUSIC_DISABLE_SONG_TAB = MUSICK_TAB.add("Stop Song", false).withWidget(BuiltInWidgets.kToggleButton),
            MUSIC_FOUND_SONG = MUSICK_TAB.add("Found it", false),
            DELETE_DEPLOY_DIRECTORY = ROBOT_TAB.add("DELETE DEPLOY DIRECTORY", ""),
            PRINT_ROBOT_TOGGLES = ROBOT_TAB.add("Reprint robot toggles", false).withWidget(BuiltInWidgets.kToggleButton),
            PRINT_ROBOT_MAPPINGS = ROBOT_TAB.add("Reprint robot mappings", false).withWidget(BuiltInWidgets.kToggleButton),
            PRINT_ROBOT_NUMBERS = ROBOT_TAB.add("Reprint robot numbers", false).withWidget(BuiltInWidgets.kToggleButton);

    //public static final ComplexWidget PDP_DISPLAY = pdpTab.add("PDPDisplay", new PowerDistributionPanel(RobotSettings.PDP_ID)).withWidget(BuiltInWidgets.kPowerDistributionPanel);
    public static ComplexWidget MUSIC_SELECTOR = MUSICK_TAB.add("SongSelector", Chirp.MUSIC_SELECTION).withWidget(BuiltInWidgets.kComboBoxChooser);
    //DANGER PANEL

    public static final HashMap<String, ? extends SimpleWidget> MISC_TABS = new HashMap<>();

    //SmartDashboard
    public static void putNumber(String key, double value) {
        SmartDashboard.putNumber(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        SmartDashboard.putBoolean(key, value);
    }
}