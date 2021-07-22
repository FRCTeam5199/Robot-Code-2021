package frc.climber;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.ballstuff.intaking.Intake;
import frc.controllers.*;
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

import static frc.controllers.ControllerEnums.ButtonPanelButtons.LOWER_CLIMBER;
import static frc.controllers.ControllerEnums.ButtonPanelButtons.RAISE_CLIMBER;
import static frc.robot.Robot.robotSettings;


/**
 * Allows the robot to climb to the top of the bar. We're going to the moon!
 *
 * @author Smaltin
 */
public class Climber implements ISubsystem {
    private AbstractMotorController[] climberMotors;
    public BaseController joystick, buttonpanel;

    @Override
    public void init() {
        createMotors();
        createControllers();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return null;
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {
        if (Intake.IntakeControlStyles.getSendableChooser().getSelected() != null && robotSettings.INTAKE_CONTROL_STYLE != Intake.IntakeControlStyles.getSendableChooser().getSelected()) {
            robotSettings.INTAKE_CONTROL_STYLE = Intake.IntakeControlStyles.getSendableChooser().getSelected();
            createControllers();
        }

        switch (robotSettings.CLIMBER_CONTROL_STYLE) {
            case STANDARD:
                if (buttonpanel.get(LOWER_CLIMBER) == ButtonStatus.DOWN) {
                    for (AbstractMotorController motor : climberMotors) {
                        motor.moveAtPercent(-0.5);
                    }
                } else if (buttonpanel.get(RAISE_CLIMBER) == ButtonStatus.DOWN) {
                    for (AbstractMotorController motor : climberMotors) {
                        motor.moveAtPercent(0.5);
                    }
                } else {
                    for (AbstractMotorController motor : climberMotors) {
                        motor.moveAtPercent(0);
                    }
                }
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.CLIMBER_CONTROL_STYLE.name() + " to control the climber. Please implement me");
        }
    }

    public void climberLocks(boolean deployed) {
        if (robotSettings.ENABLE_PNEUMATICS)
            Robot.pneumatics.climberLock.set(deployed ? Value.kForward : Value.kReverse);
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
        return null;
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
        }
    }

    private void createControllers() {
        switch (robotSettings.INTAKE_CONTROL_STYLE) {
            case FLIGHT_STICK:
                joystick = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, JoystickController.class);
            case STANDARD:
                buttonpanel = BaseController.createOrGet(robotSettings.BUTTON_PANEL_USB_SLOT, ButtonPanelController.class);
                break;
            case XBOX_CONTROLLER:
                joystick = BaseController.createOrGet(robotSettings.XBOX_CONTROLLER_USB_SLOT, XBoxController.class);
            case BOP_IT:
                joystick = BaseController.createOrGet(3, BopItBasicController.class);
                break;
            case DRUM_TIME:
                joystick = BaseController.createOrGet(5, DrumTimeController.class);
                break;
            case WII:
                joystick = BaseController.createOrGet(4, WiiController.class);
                break;
            case GUITAR:
                joystick = BaseController.createOrGet(6, SixButtonGuitarController.class);
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