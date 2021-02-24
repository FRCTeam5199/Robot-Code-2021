package frc.motors;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.misc.PID;

import static com.revrobotics.CANSparkMax.IdleMode.kBrake;
import static com.revrobotics.CANSparkMax.IdleMode.kCoast;
import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless;
import static com.revrobotics.ControlType.kVelocity;

/**
 * This works to wrap Neo's and maybe some other motors
 */
public class SparkMotorController extends AbstractMotorController {
    public final CANSparkMax motor;
    private final CANPIDController myPid;

    public SparkMotorController(int channelID) {
        this(channelID, kBrushless);
    }

    public SparkMotorController(int channelID, CANSparkMaxLowLevel.MotorType type) {
        motor = new CANSparkMax(channelID, type);
        myPid = motor.getPIDController();
        //I dont know if talons do this or if we ever dont do this so here it is
        myPid.setOutputRange(-1, 1);
    }

    @Override
    public void moveAtRotations(double rpm) {
        moveAtVelocity(rpm * sensorToRealDistanceFactor);
    }

    @Override
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        if (leader instanceof SparkMotorController)
            motor.follow(((SparkMotorController) leader).motor);
        return this;
    }

    @Override
    public void resetEncoder() {
        motor.getEncoder().setPosition(0);
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        myPid.setP(pid.getP());
        myPid.setI(pid.getI());
        myPid.setD(pid.getD());
        myPid.setFF(pid.getF());
        return this;
    }

    @Override
    public void moveAtVelocity(double realDistance) {
        myPid.setReference(realDistance / sensorToRealDistanceFactor, kVelocity);
    }

    @Override
    public AbstractMotorController setBrake(boolean brake) {
        motor.setIdleMode(brake ? kBrake : kCoast);
        return this;
    }

    @Override
    public double getRotations() {
        //why 9? i dunno
        return motor.getEncoder().getPosition() * sensorToRealDistanceFactor;
        //return motor.getEncoder().getVelocity() * sensorToRevolutionFactor;
    }

    @Override
    public double getSpeed() {
        return motor.getEncoder().getVelocity() * sensorToRealDistanceFactor;
    }

    @Override
    public AbstractMotorController setCurrentLimit(int limit) {
        motor.setSmartCurrentLimit(limit);
        return this;
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(percent);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        motor.setOpenLoopRampRate(timeToMax);
        return this;
    }
}
