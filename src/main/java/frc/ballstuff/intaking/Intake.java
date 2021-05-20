package frc.ballstuff.intaking;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.JoystickHatDirection;
import frc.controllers.DrumTimeController;
import frc.controllers.JoystickController;
import frc.drive.auton.AutonType;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.motors.AbstractMotorController;
import frc.motors.VictorMotorController;
import frc.selfdiagnostics.MotorDisconnectedIssue;

import java.util.Objects;

import static frc.robot.Robot.robotSettings;

/**
 * The "Intake" is referring to the part that picks up power cells from the floor
 */
public class Intake implements ISubsystem {
    private static final boolean DEBUG = false;
    public AbstractMotorController intakeMotor;
    public BaseController joystick;
    public int intakeMult;

    public Intake() throws InitializationFailureException, IllegalStateException {
        addToMetaList();
        init();
    }

    /**
     * create controller and motor
     *
     * @throws IllegalStateException if the selected control is not implemented
     */
    @Override
    public void init() throws IllegalStateException {
        createControllers();
        intakeMotor = new VictorMotorController(robotSettings.INTAKE_MOTOR_ID);
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return intakeMotor.isFailed() ? SubsystemStatus.FAILED : SubsystemStatus.NOMINAL;
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {

    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {
        if (robotSettings.AUTON_TYPE == AutonType.GALACTIC_SEARCH || robotSettings.AUTON_TYPE == AutonType.GALACTIC_SCAM) {
            setIntake(robotSettings.autonComplete ? IntakeDirection.OFF : IntakeDirection.IN);
        }
        updateGeneric();
    }

    @Override
    public void updateGeneric() {
        if (IntakeControlStyles.getSendableChooser().getSelected() != null && robotSettings.INTAKE_CONTROL_STYLE != IntakeControlStyles.getSendableChooser().getSelected()) {
            robotSettings.INTAKE_CONTROL_STYLE = IntakeControlStyles.getSendableChooser().getSelected();
            createControllers();
        }
        MotorDisconnectedIssue.handleIssue(this, intakeMotor);
        intakeMotor.moveAtPercent(0.8 * intakeMult);
        switch (robotSettings.INTAKE_CONTROL_STYLE) {
            case STANDARD:
                if (joystick.hatIs(JoystickHatDirection.DOWN)) {//|| buttonPanel.get(ControllerEnums.ButtonPanelButtons.) {
                    setIntake(IntakeDirection.IN);
                } else if (joystick.hatIs(JoystickHatDirection.UP)) {
                    setIntake(IntakeDirection.OUT);
                } else {
                    setIntake(IntakeDirection.OFF);
                }
                break;
            case BOPIT:
                if (joystick.get(ControllerEnums.DrumButton.PEDAL) == ControllerEnums.ButtonStatus.DOWN)
                    setIntake(IntakeDirection.IN);
                else
                    setIntake(IntakeDirection.OFF);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        if (robotSettings.DEBUG && DEBUG) {
            UserInterface.smartDashboardPutNumber("Intake Speed", intakeMult);
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
        return "Intake";
    }

    /**
     * Set intake direction
     *
     * @param input -1 for out, 1 for in, 0 for off
     */
    public void setIntake(IntakeDirection input) {
        intakeMult = input.ordinal() - 1;
    }

    private void createControllers() {
        switch (robotSettings.INTAKE_CONTROL_STYLE) {
            case STANDARD:
                joystick = BaseController.createOrGet(robotSettings.FLIGHT_STICK_USB_SLOT, JoystickController.class);
                break;
            case BOPIT:
                joystick = BaseController.createOrGet(3, BopItBasicController.class);
                break;
            case DRUM_TIME:
                joystick = BaseController.createOrGet(5, DrumTimeController.class);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + robotSettings.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
    }

    /**
     * Preserve this order. Out runs the motor at 0 - 1 = -1, off at 1 - 1 = 0, and in at 2 - 1 = 1 (percent)
     *
     * @see #setIntake(IntakeDirection)
     */
    public enum IntakeDirection {
        OUT, OFF, IN
    }

    /**
     * Determines how the {@link Intake} uses user input and what controllers to use
     */
    public enum IntakeControlStyles {
        STANDARD, BOPIT, DRUM_TIME;

        private static SendableChooser<IntakeControlStyles> myChooser;

        public static SendableChooser<IntakeControlStyles> getSendableChooser() {
            return Objects.requireNonNullElseGet(myChooser, () -> {
                myChooser = new SendableChooser<>();
                for (IntakeControlStyles style : IntakeControlStyles.values())
                    myChooser.addOption(style.name(), style);
                return myChooser;
            });
        }
    }
}