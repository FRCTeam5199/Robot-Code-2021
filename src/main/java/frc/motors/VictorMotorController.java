package frc.motors;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.misc.PID;
import frc.robot.Robot;

import static com.ctre.phoenix.motorcontrol.ControlMode.*;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Coast;

/**
 * This works to wrap 775 Pros and maybe some other motors
 */
public class VictorMotorController extends AbstractMotorController {
    private final VictorSPX motor;

    public VictorMotorController(int id) {
        super();
        motor = new VictorSPX(id);
    }

    @Override
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        return this;
    }

    @Override
    public String getName() {
        return "Victor: " + motor.getDeviceID();
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        if (leader instanceof VictorMotorController) {
            motor.follow(((VictorMotorController) leader).motor);
        } else
            throw new IllegalArgumentException("I cant follow that!");
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader, boolean invert) {
        follow(leader);
        setInverted(invert);
        return this;
    }

    @Override
    public void resetEncoder() {
        if (motor.setSelectedSensorPosition(0) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Victor Motor Controller with ID " + motor.getDeviceID() + " could not be reset");
            else
                failureFlag = true;
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        if (motor.config_kP(0, pid.getP()) != ErrorCode.OK || motor.config_kI(0, pid.getI()) != ErrorCode.OK || motor.config_kD(0, pid.getD()) != ErrorCode.OK || motor.config_kF(0, pid.getF()) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Victor Motor Controller with ID " + motor.getDeviceID() + " PIDF could not be set");
            else
                failureFlag = true;
        return this;
    }

    @Override
    public void moveAtVelocity(double realVelocity) {
        if (isTemperatureAcceptable(motor.getDeviceID()))
            motor.set(Velocity, realVelocity / sensorToRealDistanceFactor);
        else
            motor.set(Velocity, 0);
    }

    @Override
    public void moveAtPosition(double pos) {
        motor.set(Position, pos / sensorToRealDistanceFactor);
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
        if (isTemperatureAcceptable(motor.getDeviceID()))
            motor.set(PercentOutput, percent);
        else
            motor.set(PercentOutput, 0);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        if (motor.configOpenloopRamp(timeToMax) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Victor Motor Controller with ID " + motor.getDeviceID() + " open loop ramp could not be set");
            else
                failureFlag = true;
        return this;
    }

    @Override
    public double getMotorTemperature() {
        return motor.getTemperature();
    }

    @Override
    public String getSuggestedFix() {
        Faults foundFaults = new Faults();
        motor.getFaults(foundFaults);
        failureFlag = foundFaults.hasAnyFault();
        if (foundFaults.UnderVoltage) ;
            //report to PDP
        else if (foundFaults.RemoteLossOfSignal)
            potentialFix = "Ensure that motor %d is plugged into can AND power";
        else if (foundFaults.APIError)
            potentialFix = "Update the software for motor %d";
        else if (foundFaults.hasAnyFault())
            potentialFix = "Idk youre probably screwed";
        else
            potentialFix = "";
        return potentialFix;
    }
}
