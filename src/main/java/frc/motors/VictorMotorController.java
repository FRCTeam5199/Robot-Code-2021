package frc.motors;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.misc.PID;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Coast;

/**
 * This works to wrap 775 Pros and maybe some other motors
 */
public class VictorMotorController extends AbstractMotorController {
    private final VictorSPX motor;

    public VictorMotorController(int id) {
        motor = new VictorSPX(id);
    }

    @Override
    public void moveAtRotations(double rpm) {
        motor.set(Velocity, rpm);
    }

    @Override
    public void setInverted(boolean invert) {
        motor.setInverted(invert);
    }

    @Override
    public void follow(AbstractMotorController leader) {
        if (leader instanceof VictorMotorController)
            motor.follow(((VictorMotorController) leader).motor);
    }

    @Override
    public void resetEncoder() {
        motor.setSelectedSensorPosition(0);
    }

    @Override
    public void setPid(PID pid) {
        motor.config_kP(0, pid.getP());
        motor.config_kI(0, pid.getI());
        motor.config_kD(0, pid.getD());
        motor.config_kF(0, pid.getF());
    }

    @Override
    public void moveAtVelocity(double amount) {
        motor.set(Velocity, amount / sensorToRevolutionFactor);
    }

    @Override
    public void setBrake(boolean brake) {
        motor.setNeutralMode(brake ? Brake : Coast);
    }

    @Override
    public double getRotations() {
        return motor.getSelectedSensorVelocity() * sensorToRevolutionFactor;
    }

    //TODO make this work lol
    @Override
    public void setCurrentLimit(int limit) {

    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(PercentOutput, percent);
    }

    @Override
    public void setOpenLoopRampRate(double timeToMax) {
        motor.configOpenloopRamp(timeToMax);
    }
}
