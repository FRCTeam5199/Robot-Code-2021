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
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        if (leader instanceof VictorMotorController){
            motor.follow(((VictorMotorController) leader).motor);
        }
        return this;
    }

    @Override
    public void resetEncoder() {
        motor.setSelectedSensorPosition(0);
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        motor.config_kP(0, pid.getP());
        motor.config_kI(0, pid.getI());
        motor.config_kD(0, pid.getD());
        motor.config_kF(0, pid.getF());
        return this;
    }

    @Override
    public void moveAtVelocity(double realVelocity) {
        motor.set(Velocity, realVelocity / sensorToRealDistanceFactor);
    }

    @Override
    public AbstractMotorController setBrake(boolean brake) {
        motor.setNeutralMode(brake ? Brake : Coast);
        return this;
    }

    @Override
    public double getRotations() {
        return motor.getSelectedSensorPosition() * sensorToRealDistanceFactor;
    }

    @Override
    public double getSpeed() {
        return motor.getSelectedSensorVelocity() * sensorToRealDistanceFactor;
    }

    //TODO make this work lol
    @Override
    public AbstractMotorController setCurrentLimit(int limit) {
        return this;
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(PercentOutput, percent);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        motor.configOpenloopRamp(timeToMax);
        return this;
    }
}
