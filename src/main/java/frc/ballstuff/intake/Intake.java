package frc.ballstuff.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.misc.InitializationFailureException;
import frc.robot.RobotMap;

public class Intake {
    private VictorSPX victor;
    private int intakeMult;

    public Intake() throws InitializationFailureException {
        init();
    }

    public void init() {
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