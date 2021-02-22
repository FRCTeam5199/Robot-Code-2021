package frc.ballstuff.intaking;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
import frc.controllers.BopItBasicController;
import frc.controllers.ControllerEnums;
import frc.controllers.ControllerEnums.JoystickHatDirection;
import frc.controllers.JoystickController;
import frc.drive.auton.AutonType;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.motors.AbstractMotorController;
import frc.motors.VictorMotorController;
import frc.robot.RobotSettings;

/**
 * The "Intake" is referring to the part that picks up power cells from the floor
 */
public class Intake implements ISubsystem {
    private AbstractMotorController victor;
    private BaseController joystick;
    private int intakeMult;

    public Intake() throws InitializationFailureException, IllegalStateException {
        addToMetaList();
        init();
    }

    /**
     * create controller and motor
     *
     * @throws InitializationFailureException intake motor failed to be created
     * @throws IllegalStateException          if the selected control is not implemented
     */
    @Override
    public void init() throws InitializationFailureException, IllegalStateException {
        switch (RobotSettings.INTAKE_CONTROL_STYLE) {
            case STANDARD:
                joystick = new JoystickController(RobotSettings.FLIGHT_STICK_USB_SLOT);
                break;
            case BOPIT:
                joystick = new BopItBasicController(1);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotSettings.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        try {
            victor = new VictorMotorController(RobotSettings.INTAKE_MOTOR_ID);
        } catch (Exception e) {
            throw new InitializationFailureException("Intake motor failed to be created", "Disable the intake or investigate your motor mappings");
        }
    }

    /**
     * @see #updateGeneric()
     */
    @Override
    public void updateTest() {
        updateGeneric();
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
        if (RobotSettings.AUTON_MODE == AutonType.GALACTIC_SEARCH) {
            setIntake(RobotSettings.autonComplete ? 0 : -1);
        }
    }

    @Override
    public void updateGeneric() {
        victor.moveAtPercent(0.8 * intakeMult);
        switch (RobotSettings.INTAKE_CONTROL_STYLE) {
            case STANDARD:
                if (joystick.hatIs(JoystickHatDirection.DOWN)) {//|| buttonPanel.get(ControllerEnums.ButtonPanelButtons.) {
                    setIntake(1);
                } else if (joystick.hatIs(JoystickHatDirection.UP)) {
                    setIntake(-1);
                } else {
                    setIntake(0);
                }
                break;
            case BOPIT:
                if (joystick.get(ControllerEnums.BopItButtons.BOPIT) == ControllerEnums.ButtonStatus.DOWN)
                    setIntake(1);
                else
                    setIntake(0);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotSettings.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        if (RobotSettings.DEBUG) {
            SmartDashboard.putNumber("Intake Speed", intakeMult);
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

    /**
     * Set intake direction
     *
     * @param input -1 for out, 1 for in, 0 for off
     */
    public void setIntake(int input) {
        intakeMult = input;
    }

}