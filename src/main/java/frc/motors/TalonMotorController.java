package frc.motors;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.music.Orchestra;
import frc.misc.Chirp;
import frc.misc.PID;
import frc.robot.Robot;

import static com.ctre.phoenix.motorcontrol.ControlMode.*;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Coast;

/**
 * This is the wrapper for falcon 500's and maybe some other stuff
 */
public class TalonMotorController extends AbstractMotorController {
    private final WPI_TalonFX motor;

    public TalonMotorController(int id) {
        super();
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
        if (orchestra.addInstrument(motor) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Talon " + motor.getDeviceID() + " could not join the orchestra");
            else
                failureFlag = true;
    }

    @Override
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.setInverted(invert);
        }
        return this;
    }

    @Override
    public int getID() {
        return motor.getDeviceID();
    }

    @Override
    public String getName() {
        return "Talon: " + motor.getDeviceID();
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader, boolean invert) {
        if (leader instanceof TalonMotorController)
            //motor.follow(((TalonMotorController) leader).motor);
            leader.motorFollowerList.add(this);
        else
            throw new IllegalArgumentException("I cant follow that");
        setInverted(invert);
        return this;
    }

    @Override
    public void resetEncoder() {
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.resetEncoder();
        }
        if (motor.setSelectedSensorPosition(0) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " could not be reset");
            else
                failureFlag = true;
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        if (motor.config_kP(0, pid.getP()) != ErrorCode.OK || motor.config_kI(0, pid.getI()) != ErrorCode.OK || motor.config_kD(0, pid.getD()) != ErrorCode.OK || motor.config_kF(0, pid.getF()) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " PIDF couldnt be set");
            else
                failureFlag = true;
        return this;
    }

    @Override
    public void moveAtVelocity(double realAmount) {
        if (isTemperatureAcceptable()) {
            motor.set(Velocity, realAmount / sensorToRealDistanceFactor);
            for (AbstractMotorController followerMotor : motorFollowerList) {
                followerMotor.moveAtVelocity(realAmount);
            }
        } else
            motor.set(Velocity, 0);
        /// sensorToRealDistanceFactor);
        //System.out.println("I'm crying. RealAmount: " + realAmount + "\nSensortoDist: " + sensorToRealDistanceFactor + "\nSetting motors to " + realAmount / sensorToRealDistanceFactor);
    }

    @Override
    public void moveAtPosition(double pos) {
        motor.set(Position, pos);
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.moveAtPosition(pos);
        }
    }

    @Override
    public AbstractMotorController setBrake(boolean brake) {
        motor.setNeutralMode(brake ? Brake : Coast);
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.setBrake(brake);
        }
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
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " current limit could not be set");
            else
                failureFlag = true;
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.setCurrentLimit(limit);
        }
        return this;
    }

    @Override
    public void moveAtPercent(double percent) {
        if (isTemperatureAcceptable()) {
            motor.set(PercentOutput, percent);
        } else {
            motor.set(PercentOutput, 0);
        }
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.moveAtPercent(percent);
        }
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        if (motor.configOpenloopRamp(timeToMax) != ErrorCode.OK)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " could not set open ramp rate");
            else
                failureFlag = true;
        for (AbstractMotorController followerMotor : motorFollowerList) {
            followerMotor.setOpenLoopRampRate(timeToMax);
        }
        return this;
    }

    @Override
    public double getMotorTemperature() {
        return motor.getTemperature();
    }

    @Override
    public boolean isFailed() {
        Faults falts = new Faults();
        motor.getFaults(falts);
        return falts.hasAnyFault() || failureFlag;
    }

    @Override
    public String getSuggestedFix() {
        Faults foundFaults = new Faults();
        motor.getFaults(foundFaults);
        failureFlag = foundFaults.hasAnyFault();
        if (foundFaults.UnderVoltage)
            potentialFix = "More power";
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
