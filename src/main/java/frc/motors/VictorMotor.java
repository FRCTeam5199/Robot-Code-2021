package frc.motors;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;

import static com.ctre.phoenix.motorcontrol.ControlMode.*;

//TODO complete implementation
public class VictorMotor extends AbstractMotor {
    private final VictorSPX motor;
    private final VictorSPXConfiguration config;

    public VictorMotor(int id) {
        motor = new VictorSPX(id);
        config = new VictorSPXConfiguration();
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
    public void follow(AbstractMotor leader) {
        if (leader instanceof VictorMotor)
            motor.follow(((VictorMotor) leader).motor);
    }

    @Override
    public void resetEncoder() {

    }

    @Override
    public void setPid(double p, double i, double d, double f) {
        motor.config_kP(0, p);
        motor.config_kI(0, i);
        motor.config_kD(0, d);
        motor.config_kF(0, f);
    }

    @Override
    public void moveAtVelocity(double amount) {
        motor.set(Velocity, amount / sensorToRevolutionFactor);
    }

    @Override
    public void setBrake(boolean brake) {

    }

    @Override
    public double getRotations() {
        return 0;
    }

    @Override
    public void setCurrentLimit(int limit) {

    }

    @Override
    public void moveAtVoltage(double voltage) {
        //bad dont use
        motor.set(Current, voltage);
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
