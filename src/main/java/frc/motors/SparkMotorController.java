package frc.motors;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import static com.revrobotics.CANSparkMax.IdleMode.kBrake;
import static com.revrobotics.CANSparkMax.IdleMode.kCoast;
import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless;
import static com.revrobotics.ControlType.kVelocity;

/**
 * This works to wrap Neo's and maybe some other motors
 */
public class SparkMotorController extends AbstractMotorController {
    private final CANSparkMax motor;
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
        moveAtVelocity(rpm / sensorToRevolutionFactor);
    }

    @Override
    public void setInverted(boolean invert) {
        motor.setInverted(invert);
    }

    @Override
    public void follow(AbstractMotorController leader) {
        if (leader instanceof SparkMotorController)
            motor.follow(((SparkMotorController) leader).motor);
    }

    @Override
    public void resetEncoder() {
        motor.getEncoder().setPosition(0);
    }

    @Override
    public void setPid(double p, double i, double d, double f) {
        myPid.setP(p);
        myPid.setI(i);
        myPid.setD(d);
        myPid.setFF(f);
    }

    @Override
    public void moveAtVelocity(double amount) {
        myPid.setReference(amount, kVelocity);
    }

    @Override
    public void setBrake(boolean brake) {
        motor.setIdleMode(brake ? kBrake : kCoast);
    }

    @Override
    public double getRotations() {
        //why 9? i dunno
        return motor.getEncoder().getVelocity() * sensorToRevolutionFactor;
    }

    @Override
    public void setCurrentLimit(int limit) {
        motor.setSmartCurrentLimit(limit);
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(percent);
    }

    @Override
    public void setOpenLoopRampRate(double timeToMax) {
        motor.setOpenLoopRampRate(timeToMax);
    }
}
