package frc.climber;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.ballstuff.intaking.Intake;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums.ButtonStatus;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.misc.SubsystemStatus;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.TalonMotorController;
import frc.motors.VictorMotorController;
import frc.robot.Robot;

import java.util.Objects;

import static frc.controllers.ControllerEnums.ButtonPanelButtons.*;
import static frc.robot.Robot.robotSettings;


/**
 * Allows the robot to climb to the top of the bar. We're going to the moon!
 *
 * @author Smaltin
 */
public class Climber implements ISubsystem {
    public BaseController joystick, buttonpanel;
    private AbstractMotorController[] climberMotors;
    private boolean isLocked = false;

    public Climber() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        createMotors();
        createControllers();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return SubsystemStatus.NOMINAL;
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {
        if (robotSettings.CLIMBER_CONTROL_STYLE == ClimberControlStyles.STANDARD) {
            if (buttonpanel.get(LOWER_CLIMBER) == ButtonStatus.DOWN) {
                for (AbstractMotorController motor : climberMotors) {
                    motor.moveAtPercent(-0.8);
                }
            } else if (buttonpanel.get(RAISE_CLIMBER) == ButtonStatus.DOWN && !isLocked) {
                for (AbstractMotorController motor : climberMotors) {
                    motor.moveAtPercent(0.8);
                }
            } else {
                for (AbstractMotorController motor : climberMotors) {
                    motor.moveAtPercent(0);
                }
            }
            if (buttonpanel.get(CLIMBER_LOCK) == ButtonStatus.DOWN) {
                climberLocks(true);
            } else if (buttonpanel.get(CLIMBER_UNLOCK) == ButtonStatus.DOWN) {
                climberLocks(false);
            }
        } else {
            throw new IllegalStateException("There is no UI configuration for " + robotSettings.CLIMBER_CONTROL_STYLE.name() + " to control the climber. Please implement me");
        }
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "Climber";
    }

    public void climberLocks(boolean deployed) {
        if (robotSettings.ENABLE_PNOOMATICS)
            Robot.pneumatics.climberLock.set(deployed ? Value.kForward : Value.kReverse);
        isLocked = deployed;
    }

    private void createMotors() {
        climberMotors = new AbstractMotorController[robotSettings.CLIMBER_MOTOR_IDS.length];
        for (int indexer = 0; indexer < robotSettings.CLIMBER_MOTOR_IDS.length; indexer++) {
            switch (Robot.robotSettings.CLIMBER_MOTOR_TYPE) {
                case VICTOR:
                    climberMotors[indexer] = new VictorMotorController(robotSettings.CLIMBER_MOTOR_IDS[indexer]);
                    break;
                case TALON_FX:
                    climberMotors[indexer] = new TalonMotorController(robotSettings.CLIMBER_MOTOR_IDS[indexer]);
                    break;
                case CAN_SPARK_MAX:
                    climberMotors[indexer] = new SparkMotorController(robotSettings.CLIMBER_MOTOR_IDS[indexer]);
                    break;
                default:
                    throw new InitializationFailureException("DriveManager does not have a suitible constructor for " + robotSettings.CLIMBER_MOTOR_TYPE.name(), "Add an implementation in the init for climber");
            }
            if (indexer % 2 != 0) {
                climberMotors[indexer].setInverted(true);
            }
        }
    }

    private void createControllers() {
        switch (robotSettings.INTAKE_CONTROL_STYLE) {
            case FLIGHT_STICK:
                joystick = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, BaseController.Controllers.JOYSTICK_CONTROLLER);
            case STANDARD:
                buttonpanel = BaseController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT, BaseController.Controllers.BUTTON_PANEL_CONTROLLER);
                break;
            case XBOX_CONTROLLER:
                joystick = BaseController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT, BaseController.Controllers.XBOX_CONTROLLER);
            case BOP_IT:
                joystick = BaseController.createOrGet(3, BaseController.Controllers.BOP_IT_CONTROLLER);
                break;
            case DRUM_TIME:
                joystick = BaseController.createOrGet(5, BaseController.Controllers.DRUM_CONTROLLER);
                break;
            case WII:
                joystick = BaseController.createOrGet(4, BaseController.Controllers.WII_CONTROLLER);
                break;
            case GUITAR:
                joystick = BaseController.createOrGet(6, BaseController.Controllers.SIX_BUTTON_GUITAR_CONTROLLER);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
    }

    public enum ClimberControlStyles {
        STANDARD,
        WII,
        DRUM_TIME,
        GUITAR,
        BOP_IT,
        FLIGHT_STICK,
        XBOX_CONTROLLER;


        private static SendableChooser<Climber.ClimberControlStyles> myChooser;

        public static SendableChooser<Climber.ClimberControlStyles> getSendableChooser() {
            return Objects.requireNonNullElseGet(myChooser, () -> {
                myChooser = new SendableChooser<>();
                for (Climber.ClimberControlStyles style : Climber.ClimberControlStyles.values())
                    myChooser.addOption(style.name(), style);
                return myChooser;
            });
        }
    }
}