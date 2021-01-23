package frc.ballstuff.intaking;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.controllers.JoystickController;
import frc.misc.InitializationFailureException;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;

public class Intake {
    private VictorSPX victor;
    private JoystickController joystick;
    private int intakeMult;

    public Intake() throws InitializationFailureException {
        init();
        joystick = new JoystickController(RobotNumbers.FLIGHT_STICK_SLOT);
    }

    public void init() throws InitializationFailureException{
        try {
            victor = new VictorSPX(RobotMap.INTAKE_MOTOR);
        } catch (Exception e) {
            throw new InitializationFailureException("Intake motor failed to be created", "Disable the intake or investigate your motor mappings");
        }
    }

    /**
     * Set intake direction
     *
     * @param input - -1 for out, 1 for in, 0 for none
     */
    public void setIntake(int input) {
        intakeMult = input;
    }

    public void updateGeneric() {
        victor.set(ControlMode.PercentOutput, 0.8 * intakeMult);
        if (joystick.getHat() == 180) {
            setIntake(1);
            //deploy intake
            //intake.setDeploy(true);
        } else if (joystick.getHat() == 0) {
            setIntake(-1);
            //deploy intake
            //intake.setDeploy(true);
        } else {
            setIntake(0);
            //deployn't intake
            //intake.setDeploy(false);
        }
    }

    public void updateTeleop() {
        updateGeneric();
    }

    public void updateTest() {
        updateGeneric();
    }

    public void updateAutonomous() {
    }

}