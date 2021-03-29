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
        return this;
    }

    @Override
    public String getName() {
        return "Talon: " + motor.getDeviceID();
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader, boolean invert) {
        if (leader instanceof TalonMotorController)
            motor.follow(((TalonMotorController) leader).motor);
        else
            throw new IllegalArgumentException("I cant follow that");
        setInverted(invert);
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        return follow(leader, false);
    }

    @Override
    public void resetEncoder() {
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
        if (isTemperatureAcceptable(motor.getDeviceID()))
            motor.set(Velocity, realAmount / sensorToRealDistanceFactor);
        else
            motor.set(Velocity, 0);
        /// sensorToRealDistanceFactor);
        //System.out.println("I'm crying. RealAmount: " + realAmount + "\nSensortoDist: " + sensorToRealDistanceFactor + "\nSetting motors to " + realAmount / sensorToRealDistanceFactor);
    }

    @Override
    public void moveAtPosition(double pos) {
        motor.set(Position, pos);
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
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " current limit could not be set");
            else
                failureFlag = true;
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
                throw new IllegalStateException("Talon motor controller with ID " + motor.getDeviceID() + " could not set open ramp rate");
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
