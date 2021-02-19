package frc.motors;

public abstract class AbstractMotor {
    //Double not double so it will throw a NPE if not inited
    protected Double sensorToRevolutionFactor;

    public abstract void moveAtRotations(double rpm);

    public void setSensorToRevolutionFactor(double s2rf){
        sensorToRevolutionFactor = s2rf;
    }

    public abstract void setInverted(boolean invert);

    public abstract void follow(AbstractMotor leader);

    public abstract void resetEncoder();

    public abstract void setPid(double p, double i, double d, double f);

    public abstract void moveAtVelocity(double amount);

    public abstract void setBrake(boolean brake);

    public abstract double getRotations();

    public abstract void setCurrentLimit(int limit);

    public abstract void moveAtVoltage(double voltage);

    public abstract void moveAtPercent(double percent);

    public abstract void setOpenLoopRampRate(double timeToMax);
}
