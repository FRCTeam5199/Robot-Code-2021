package frc.misc;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotSettings;

import java.util.List;

public class ShuffleboardDisplay {
    //SHOOTER TODO make PID widget (kPIDController)
    public static final ShuffleboardTab shooterTab = Shuffleboard.getTab("Shooter");
    public static final SimpleWidget shooterP = shooterTab.add("P", RobotSettings.SHOOTER_PID.getP()),
            SHOOTER_I = shooterTab.add("I", RobotSettings.SHOOTER_PID.getI()),
            SHOOTER_D = shooterTab.add("D", RobotSettings.SHOOTER_PID.getD()),
            SHOOTER_F = shooterTab.add("F", RobotSettings.SHOOTER_PID.getF()),
            SHOOTER_CONST_SPEED = shooterTab.add("Constant Speed", 0),
            SHOOTER_CALIBRATE_PID = shooterTab.add("Recalibrate PID", false);

    //DRIVETRAIN TODO make PID widget (kPIDController)
    public static final ShuffleboardTab driveTab = Shuffleboard.getTab("drive");
    public static final SimpleWidget DRIVE_ROT_MULT = driveTab.add("Rotation Factor", RobotSettings.TURN_SCALE),
            DRIVE_SCALE_MULT = driveTab.add("Speed Factor", RobotSettings.DRIVE_SCALE),
            DRIVE_P = driveTab.add("P", RobotSettings.DRIVEBASE_PID.getP()),
            DRIVE_I = driveTab.add("I", RobotSettings.DRIVEBASE_PID.getI()),
            DRIVE_D = driveTab.add("D", RobotSettings.DRIVEBASE_PID.getD()),
            DRIVE_F = driveTab.add("F", RobotSettings.DRIVEBASE_PID.getF()),
            DRIVE_CALIBRATE_PID = driveTab.add("Calibrate PID", false),
            DRIVE_COAST = driveTab.add("Coast", true);

    //PDP TODO make pdp widget (kPowerDistributionPanel)
    public static final ShuffleboardTab pdpTab = Shuffleboard.getTab("Lectricity");
    public static final SimpleWidget PDP_TOTAL_ENERGY_ON_THIS_BOOT = pdpTab.add("Total energy on this boot", 0),
            PDP_PEAK_CURRENT = pdpTab.add("Peak current", 0),
            //PDP_OTHER_ENERGY = POWER_TAB.add("Energy on current enable", 0),
            PDP_PEAK_POWER = pdpTab.add("Peak power", 0);

    //MUSICK
    public static final ShuffleboardTab MUSICK_TAB = Shuffleboard.getTab("musick");
    public static final SimpleWidget MUSIC_DISABLE_SONG_TAB = MUSICK_TAB.add("Stop Song", false).withWidget(BuiltInWidgets.kToggleButton),
            MUSIC_FOUND_SONG = MUSICK_TAB.add("Found it", false);

    //DANGER PANEL
    public static final ShuffleboardTab ROBOT_TAB = Shuffleboard.getTab("DANGER!");
    public static final SimpleWidget DELETE_DEPLOY_DIRECTORY = ROBOT_TAB.add("DELETE DEPLOY DIRECTORY", ""),
            PRINT_ROBOT_TOGGLES = ROBOT_TAB.add("Reprint robot toggles", false).withWidget(BuiltInWidgets.kToggleButton),
            PRINT_ROBOT_MAPPINGS = ROBOT_TAB.add("Reprint robot mappings", false).withWidget(BuiltInWidgets.kToggleButton),
            PRINT_ROBOT_NUMBERS = ROBOT_TAB.add("Reprint robot numbers", false).withWidget(BuiltInWidgets.kToggleButton);

    //WARNINGS PANEL
    public static final ShuffleboardTab FAILURES_TAB = Shuffleboard.getTab("Warnings");

    public static void putNumber(String key, double value){
        SmartDashboard.putNumber(key, value);
    }

    public static void putBoolean(String key, boolean value){
        SmartDashboard.putBoolean(key, value);
    }
}