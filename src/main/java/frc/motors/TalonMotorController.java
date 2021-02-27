package frc.motors;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.music.Orchestra;
import frc.misc.Chirp;
import frc.misc.PID;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Coast;

/**
 * This is the wrapper for falcon 500's and maybe some other stuff
 */
public class TalonMotorController extends AbstractMotorController {
    private final WPI_TalonFX motor;

    public TalonMotorController(int id) {
        motor = new WPI_TalonFX(id);
        Chirp.talonMotorArrayList.add(this);
    }

    /**
     * The talons are the motors that make music and this is the method to register tham as musick makers. The orchestra
     * is wrapped inside {@link Chirp} using the {@link Chirp#talonMotorArrayList meta talon registry}
     *
     * @param orchestra the {@link Orchestra} object this motor should join
     * @see Chirp
     */
    public void addToOrchestra(Orchestra orchestra) {
        orchestra.addInstrument(motor);
    }

    @Override
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        if (leader instanceof TalonMotorController)
            motor.follow(((TalonMotorController) leader).motor);
        return this;
    }

    @Override
    public void resetEncoder() {
        if (motor.setSelectedSensorPosition(0) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " could not be reset");
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        if (motor.config_kP(0, pid.getP()) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " P in PIDF couldnt be reset");
        if (motor.config_kI(0, pid.getI()) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " I in PIDF couldnt be reset");
        if (motor.config_kD(0, pid.getD()) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " D in PIDF couldnt be reset");
        if (motor.config_kF(0, pid.getF()) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " F in PIDF couldnt be reset");
        return this;
    }

    @Override
    public void moveAtVelocity(double realAmount) {
        if (getMotorTemperature() > 100){
            System.out.println("Im literally boiling chill out");
        } else
            motor.set(Velocity, realAmount / sensorToRealDistanceFactor);/// sensorToRealDistanceFactor);
        //System.out.println("I'm crying. RealAmount: " + realAmount + "\nSensortoDist: " + sensorToRealDistanceFactor + "\nSetting motors to " + realAmount / sensorToRealDistanceFactor);
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

    @Override
    public AbstractMotorController setCurrentLimit(int limit) {
        SupplyCurrentLimitConfiguration config = new SupplyCurrentLimitConfiguration();
        config.currentLimit = limit;
        config.enable = true;
        if (motor.configSupplyCurrentLimit(config) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " current limit could not be set");
        return this;
    }

    @Override
    public void moveAtPercent(double percent) {
        if (getMotorTemperature() > 100){
            System.out.println("Im literally boiling chill out");
        } else
            motor.set(PercentOutput, percent);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        if (motor.configOpenloopRamp(timeToMax) != ErrorCode.OK)
            throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " could not set open ramp rate");
        return this;
    }

    @Override
    public double getMotorTemperature() {
        return motor.getTemperature();
    }
}
