package frc.drive;

import frc.misc.ISubsystem;
import frc.telemetry.RobotTelemetry;

/**
 * Chill out there is only vibing going on here, officer
 */
public abstract class AbstractDriveManager implements ISubsystem {
    /**
     * I dont know where I am going, but i do know that whatever drive manager i end up in will love me
     */
    public RobotTelemetry guidance;

    /**
     * Required by {@link RobotTelemetry} in order to reset position
     */
    public abstract void resetDriveEncoders();

    /**
     * Required by {@link frc.drive.auton.AbstractAutonManager} for stopping the robot on auton completion
     *
     * @param brake true to brake false to coast
     */
    public abstract void setBrake(boolean brake);

    protected AbstractDriveManager() {
        init();
        addToMetaList();
        createTelem();
    }

    protected void createTelem() {
        guidance = new RobotTelemetry(this);
        guidance.resetOdometry();
    }

    public String getSubsystemName() {
        return "Drivetrain";
    }
}
