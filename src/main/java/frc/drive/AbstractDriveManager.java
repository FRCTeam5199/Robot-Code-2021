package frc.drive;

import frc.misc.ISubsystem;
import frc.telemetry.AbstractRobotTelemetry;
import frc.telemetry.RobotTelemetryStandard;
import frc.robot.Robot;

/**
 * Chill out there is only vibing going on here, officer
 */
public abstract class AbstractDriveManager implements ISubsystem {
    /**
     * I dont know where I am going, but i do know that whatever drive manager i end up in will love me
     */
    public AbstractRobotTelemetry guidance;

    /**
     * Required by {@link RobotTelemetryStandard} in order to reset position
     */
    public abstract void resetDriveEncoders();

    /**
     * Required by {@link frc.drive.auton.AbstractAutonManager} for stopping the robot on auton completion
     *
     * @param brake true to brake false to coast
     */
    public abstract void setBrake(boolean brake);

    public abstract void driveMPS(double xMeters, double yMeters, double rotation);

    protected AbstractDriveManager() {
        init();
        addToMetaList();
        createTelem();
    }

    protected void createTelem() {
        if (Robot.robotSettings.ENABLE_IMU) {
            guidance = new RobotTelemetryStandard(this);
            guidance.resetOdometry();
        }
    }

    public String getSubsystemName() {
        return "Drivetrain";
    }
}
