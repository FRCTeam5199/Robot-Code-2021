package frc.drive;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import frc.misc.ISubsystem;
import frc.misc.UserInterface;
import frc.robot.Robot;
import frc.telemetry.AbstractRobotTelemetry;
import frc.telemetry.RobotTelemetryStandard;

import static frc.robot.Robot.robotSettings;

/**
 * Chill out there is only vibing going on here, officer
 */
public abstract class AbstractDriveManager implements ISubsystem {
    protected final NetworkTableEntry driveRotMult = UserInterface.DRIVE_ROT_MULT.getEntry(),
            driveScaleMult = UserInterface.DRIVE_SCALE_MULT.getEntry();
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

    public abstract void driveWithChassisSpeeds(ChassisSpeeds speeds);

    protected AbstractDriveManager() {
        init();
        addToMetaList();
        createTelem();
    }

    protected void createTelem() {
        if (Robot.robotSettings.ENABLE_IMU) {
            guidance = AbstractRobotTelemetry.createTelem(this);
            guidance.resetOdometry();
        }
    }

    public String getSubsystemName() {
        return "Drivetrain";
    }

    /**
     * Takes a -1 to 1 scaled value and returns it scaled based on the max sped
     *
     * @param input -1 to 1 drive amount
     * @return input scaled based on the bot's max speed
     */
    protected double adjustedDrive(double input) {
        return input * robotSettings.MAX_SPEED * driveScaleMult.getDouble(robotSettings.DRIVE_SCALE);
    }

    /**
     * Takes a -1 to 1 scaled value and returns it scaled based on the max turning
     *
     * @param input -1 to 1 drive amount
     * @return input scaled based on max turning
     */
    protected double adjustedRotation(double input) {
        return input * robotSettings.MAX_ROTATION * driveRotMult.getDouble(robotSettings.TURN_SCALE);
    }
}
