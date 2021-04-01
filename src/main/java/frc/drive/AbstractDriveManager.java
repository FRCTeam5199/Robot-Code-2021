package frc.drive;

import frc.misc.ISubsystem;
import frc.telemetry.RobotTelemetry;

public abstract class AbstractDriveManager implements ISubsystem {
    public RobotTelemetry guidance;

    protected AbstractDriveManager(){
        guidance = new RobotTelemetry(this);
        guidance.resetOdometry();
    }

    public abstract void resetDriveEncoders();

    public abstract void setBrake(boolean brake);
}
