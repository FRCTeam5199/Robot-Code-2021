package frc.ballstuff.intaking;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums.JoystickHatDirection;
import frc.controllers.JoystickController;
import frc.misc.ISubsystem;
import frc.misc.InitializationFailureException;
import frc.motors.AbstractMotor;
import frc.motors.VictorMotor;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

/**
 * The "Intake" is referring to the part that picks up power cells from the floor
 */
public class Intake implements ISubsystem {
    private AbstractMotor victor;
    private int intakeMult;
    private BaseController joystick;

    public Intake() throws InitializationFailureException, IllegalStateException {
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
        switch (RobotToggles.INTAKE_CONTROL_STYLE) {
            case STANDARD:
                joystick = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotToggles.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        try {
            victor = new VictorMotor(RobotMap.INTAKE_MOTOR);
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
    }

    @Override
    public void updateGeneric() {
        victor.moveAtPercent(0.8 * intakeMult);
        switch (RobotToggles.INTAKE_CONTROL_STYLE) {
            case STANDARD:
                if (joystick.hatIs(JoystickHatDirection.DOWN)) {//|| buttonPanel.get(ControllerEnums.ButtonPanelButtons.) {
                    setIntake(1);
                } else if (joystick.hatIs(JoystickHatDirection.UP)) {
                    setIntake(-1);
                } else {
                    setIntake(0);
                }
                break;
            default:
                throw new IllegalStateException("There is no UI configuration for " + RobotToggles.INTAKE_CONTROL_STYLE.name() + " to control the shooter. Please implement me");
        }
        if (RobotToggles.DEBUG) {
            SmartDashboard.putNumber("Intake Speed", intakeMult);
        }
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