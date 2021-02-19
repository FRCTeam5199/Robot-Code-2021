package frc.motors;

import com.ctre.phoenix.music.Orchestra;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import frc.misc.Chirp;
import frc.robot.RobotNumbers;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Coast;

public class TalonMotor extends AbstractMotor {
    private final WPI_TalonFX motor;

    public TalonMotor(int id) {
        motor = new WPI_TalonFX(id);
        Chirp.talonMotorArrayList.add(this);
    }
    
    public void addToOrchestra(Orchestra orchestra){
        orchestra.addInstrument(motor);
    }

    //public void

    @Override
    public void moveAtRotations(double rpm) {
        moveAtVelocity(rpm / sensorToRevolutionFactor);
    }

    @Override
    public void setInverted(boolean invert) {
        motor.setInverted(invert);
    }

    @Override
    public void follow(AbstractMotor leader) {
        if (leader instanceof TalonMotor)
            motor.follow(((TalonMotor) leader).motor);
    }

    @Override
    public void resetEncoder() {
        motor.setSelectedSensorPosition(0);
    }

    @Override
    public void setPid(double p, double i, double d, double f) {
        motor.config_kP(0, p, RobotNumbers.DRIVE_TIMEOUT_MS);
        motor.config_kI(0, i, RobotNumbers.DRIVE_TIMEOUT_MS);
        motor.config_kD(0, d, RobotNumbers.DRIVE_TIMEOUT_MS);
        motor.config_kF(0, f, RobotNumbers.DRIVE_TIMEOUT_MS);
    }

    @Override
    public void moveAtVelocity(double amount) {
        motor.set(Velocity, amount);
    }

    @Override
    public void setBrake(boolean brake) {
        motor.setNeutralMode(brake ? Brake : Coast);
    }

    @Override
    public double getRotations() {
        return motor.getSelectedSensorVelocity() * sensorToRevolutionFactor;
    }

    @Override
    public void setCurrentLimit(int limit){
        SupplyCurrentLimitConfiguration config = new SupplyCurrentLimitConfiguration();
        config.currentLimit = limit;
        config.enable = true;
        motor.configSupplyCurrentLimit(config);
    }

    @Override
    public void moveAtVoltage(double voltage) {
        motor.setVoltage(voltage);
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
