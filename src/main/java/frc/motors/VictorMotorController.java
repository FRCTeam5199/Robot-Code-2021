package frc.motors;

import com.ctre.phoenix.ErrorCode;
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
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        if (leader instanceof VictorMotorController) {
            motor.follow(((VictorMotorController) leader).motor);
        }
        return this;
    }

    @Override
    public void resetEncoder() {
        if (motor.setSelectedSensorPosition(0) != ErrorCode.OK)
            throw new IllegalStateException("Victor Motor Controller with ID " + motor.getDeviceID() + " could not be reset");
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        if (motor.config_kP(0, pid.getP()) != ErrorCode.OK || motor.config_kI(0, pid.getI()) != ErrorCode.OK || motor.config_kD(0, pid.getD()) != ErrorCode.OK || motor.config_kF(0, pid.getF()) != ErrorCode.OK)
            throw new IllegalStateException("Victor Motor Controller with ID " + motor.getDeviceID() + " PIDF could not be set");
        return this;
    }

    @Override
    public void moveAtVelocity(double realVelocity) {
        if (getMotorTemperature() > 100){
            System.out.println("Im literally boiling chill out");
        } else
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
        //if (getMotorTemperature() > 100){
            //System.out.println("Im literally boiling chill out");
        //} else
            motor.set(PercentOutput, percent);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        if (motor.configOpenloopRamp(timeToMax) != ErrorCode.OK)
            throw new IllegalStateException("Victor Motor Controller with ID " + motor.getDeviceID() + " open loop ramp could not be set");
        return this;
    }

    @Override
    public double getMotorTemperature() {
        return motor.getTemperature();
    }
}
