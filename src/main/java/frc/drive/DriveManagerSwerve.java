package frc.drive;

import com.ctre.phoenix.sensors.CANCoder;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.XBoxController;
import frc.misc.PID;
import frc.misc.SubsystemStatus;
import frc.motors.SupportedMotors;
import frc.motors.SwerveMotorController;
import frc.robot.Robot;

import static frc.robot.Robot.robotSettings;

/*
notes n stuff

14wide x 22long between wheels

max speed 3.6 m/s

 */
public class DriveManagerSwerve extends AbstractDriveManager {
    private static final boolean DEBUG = false;
    private final Translation2d driftOffset = new Translation2d(-0.6, 0);
    private final double trackWidth = 13.25;
    private final double trackLength = 21.5;
    private final Translation2d frontLeftLocation = new Translation2d(-trackLength / 2 / 39.3701, trackWidth / 2 / 39.3701);
    private final Translation2d frontRightLocation = new Translation2d(-trackLength / 2 / 39.3701, -trackWidth / 2 / 39.3701);
    private final Translation2d backLeftLocation = new Translation2d(trackLength / 2 / 39.3701, trackWidth / 2 / 39.3701);
    private final Translation2d backRightLocation = new Translation2d(trackLength / 2 / 39.3701, -trackWidth / 2 / 39.3701);
    public final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            frontLeftLocation, frontRightLocation, backLeftLocation, backRightLocation
    );
    public SwerveModuleState[] moduleStates;
    public SwerveMotorController driverFR, driverBR, driverBL, driverFL;
    private PIDController FRpid, BRpid, BLpid, FLpid;
    private BaseController xbox;
    private CANCoder FRcoder, BRcoder, BLcoder, FLcoder;

    public DriveManagerSwerve() {
        super();
    }

    @Override
    public void init() {
        PID steeringPID = new PID(0.0035, 0.000001, 0);

        FLpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        FRpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        BLpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        BRpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        FLpid.enableContinuousInput(-180, 180);
        FRpid.enableContinuousInput(-180, 180);
        BLpid.enableContinuousInput(-180, 180);
        BRpid.enableContinuousInput(-180, 180);

        xbox = XBoxController.createOrGet(0);

        driverFR = new SwerveMotorController(1, SupportedMotors.CAN_SPARK_MAX, 2, SupportedMotors.CAN_SPARK_MAX);
        driverBR = new SwerveMotorController(4, SupportedMotors.CAN_SPARK_MAX, 3, SupportedMotors.CAN_SPARK_MAX);
        driverBL = new SwerveMotorController(6, SupportedMotors.CAN_SPARK_MAX, 5, SupportedMotors.CAN_SPARK_MAX);
        driverFL = new SwerveMotorController(7, SupportedMotors.CAN_SPARK_MAX, 8, SupportedMotors.CAN_SPARK_MAX);

        driverFR.driver.setBrake(true);
        driverFL.driver.setInverted(true).setBrake(true);
        driverBR.driver.setBrake(true);
        driverBL.driver.setInverted(true).setBrake(true);

        driverFR.steering.setInverted(true);
        driverBR.steering.setInverted(true);
        driverBL.steering.setInverted(true);
        driverFL.steering.setInverted(true);

        //setSteeringPIDS(new PID(0.005, 0.0000, 0.01));

        FRcoder = new CANCoder(11);
        BRcoder = new CANCoder(12);
        FLcoder = new CANCoder(13);
        BLcoder = new CANCoder(14);

        driverFR.driver.setSensorToRealDistanceFactor(robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER / 12 * Math.PI) / 60);
        driverBR.driver.setSensorToRealDistanceFactor(robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER / 12 * Math.PI) / 60);
        driverFL.driver.setSensorToRealDistanceFactor(robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER / 12 * Math.PI) / 60);
        driverBL.driver.setSensorToRealDistanceFactor(robotSettings.DRIVE_GEARING * (robotSettings.WHEEL_DIAMETER / 12 * Math.PI) / 60);
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        if (driverFR.driver.failureFlag || driverBR.driver.failureFlag || driverFL.driver.failureFlag || driverBL.driver.failureFlag || driverFR.steering.failureFlag || driverBR.steering.failureFlag || driverFL.steering.failureFlag || driverBL.steering.failureFlag)
            return SubsystemStatus.FAILED;
        return SubsystemStatus.NOMINAL;
    }

    @Override
    public void updateTest() {
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println(FRcoder.getAbsolutePosition() + " FR " + driverFR.steering.getRotations());
            System.out.println(FLcoder.getAbsolutePosition() + " FL " + driverFL.steering.getRotations());
            System.out.println(BRcoder.getAbsolutePosition() + " BR " + driverBR.steering.getRotations());
            System.out.println(BLcoder.getAbsolutePosition() + " BL " + driverBL.steering.getRotations());
            System.out.println();
            System.out.println(guidance.imu.relativeYaw());
        }
    }

    @Override
    public void updateTeleop() {
        driveSwerve();
        if (xbox.get(ControllerEnums.XBoxButtons.LEFT_BUMPER) == ControllerEnums.ButtonStatus.DOWN) {
            guidance.imu.resetOdometry();
        }
    }

    @Override
    public void updateAuton() {


    }

    @Override
    public void updateGeneric() {

    }

    @Override
    public void initTest() {
        resetSteeringEncoders();
        setupSteeringEncoders();
    }

    @Override
    public void initTeleop() {
        setupSteeringEncoders();
        resetSteeringEncoders();
    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    /**
     * reset steering motor encoders
     */
    private void resetSteeringEncoders() {
        driverFR.steering.resetEncoder();
        driverBR.steering.resetEncoder();
        driverFL.steering.resetEncoder();
        driverBL.steering.resetEncoder();
    }

    /**
     * set steering motors to return their encoder position in degrees
     */
    private void setupSteeringEncoders() {
        //12.8:1
        driverFR.steering.setSensorToRealDistanceFactor((1 / 12.8) * 360);
        driverBR.steering.setSensorToRealDistanceFactor((1 / 12.8) * 360);
        driverFL.steering.setSensorToRealDistanceFactor((1 / 12.8) * 360);
        driverBL.steering.setSensorToRealDistanceFactor((1 / 12.8) * 360);
    }

    private void driveSwerve() {
        double forwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) * (-1);
        double leftwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_X) * (1);
        double rotation = xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * (-3);

        driveMPS(forwards * robotSettings.DRIVE_SCALE * robotSettings.MAX_SPEED, leftwards * robotSettings.DRIVE_SCALE * robotSettings.MAX_SPEED, rotation * robotSettings.TURN_SCALE);
    }

    private boolean useFieldOriented() {
        return xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER) < 0.1;
    }


    private boolean dorifto() {
        return xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1;
    }

    private void driveWithChassisSpeeds(ChassisSpeeds speeds) {
        moduleStates = kinematics.toSwerveModuleStates(speeds);

        if (xbox.get(ControllerEnums.XBoxButtons.RIGHT_BUMPER) == ControllerEnums.ButtonStatus.DOWN) {
            moduleStates = kinematics.toSwerveModuleStates(speeds, frontRightLocation);
        } else if (dorifto()) {
            double driftOffset = 3;
            double offset = trackLength / 2 / 39.3701;
            offset -= speeds.vxMetersPerSecond / driftOffset;
            System.out.println("forwards: " + speeds.vxMetersPerSecond);
            moduleStates = kinematics.toSwerveModuleStates(speeds, new Translation2d(offset, 0));
        }

        // Front left module state
        SwerveModuleState frontLeft = moduleStates[0], frontRight = moduleStates[1], backLeft = moduleStates[2], backRight = moduleStates[3];

        //try continuous here
        setSteeringContinuous(frontLeft.angle.getDegrees(), frontRight.angle.getDegrees(), backLeft.angle.getDegrees(), backRight.angle.getDegrees());
        if (DEBUG && robotSettings.DEBUG) {
            System.out.printf("%4f %4f %4f %4f \n", frontLeft.speedMetersPerSecond, frontRight.speedMetersPerSecond, backLeft.speedMetersPerSecond, backRight.speedMetersPerSecond);
        }
        setDrive(frontLeft.speedMetersPerSecond, frontRight.speedMetersPerSecond, backLeft.speedMetersPerSecond, backRight.speedMetersPerSecond);
    }

    /**
     * Sets the drive steering
     *
     * @param FL Front left translation requested. units?
     * @param FR Front right translation requested. units?
     * @param BL Back left translation requested. units?
     * @param BR Back right translation requested. units?
     */
    private void setSteeringContinuous(double FL, double FR, double BL, double BR) {
        double FLoffset = -1, FRoffset = -1, BLoffset = 2, BRoffset = 2;

        FLpid.setSetpoint(FL + FLoffset);
        FRpid.setSetpoint(FR + FRoffset);
        BRpid.setSetpoint(BR + BRoffset);
        BLpid.setSetpoint(BL + BLoffset);

        driverFL.steering.moveAtPercent(FLpid.calculate(FLcoder.getAbsolutePosition()));
        driverFR.steering.moveAtPercent(FRpid.calculate(FRcoder.getAbsolutePosition()));
        driverBL.steering.moveAtPercent(BLpid.calculate(BLcoder.getAbsolutePosition()));
        driverBR.steering.moveAtPercent(BRpid.calculate(BRcoder.getAbsolutePosition()));
    }

    /**
     * Drives the bot in percent control mode based on inputs
     *
     * @param FL {@link #driverFL} requested drive (-3.5, 3.5)
     * @param FR {@link #driverFR} requested drive (-3.5, 3.5)
     * @param BL {@link #driverBL} requested drive (-3.5, 3.5)
     * @param BR {@link #driverBR} requested drive (-3.5, 3.5)
     */
    private void setDrive(double FL, double FR, double BL, double BR) {
        double num = 3.5;
        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("FL: " + FL);
            System.out.println("FR: " + FR);
            System.out.println("BL: " + BL);
            System.out.println("BR: " + BR);
        }
        driverFR.driver.moveAtPercent(FR / num);
        driverBR.driver.moveAtPercent(BR / num);
        driverFL.driver.moveAtPercent(FL / num);
        driverBL.driver.moveAtPercent(BL / num);
    }

    //TODO implement this in regard to telem
    @Override
    public void resetDriveEncoders() {
        driverFR.driver.resetEncoder();
        driverFL.driver.resetEncoder();
        driverBR.driver.resetEncoder();
        driverBL.driver.resetEncoder();
    }

    @Override
    public void setBrake(boolean brake) {
        driverFR.driver.setBrake(brake);
        driverFL.driver.setBrake(brake);
        driverBL.driver.setBrake(brake);
        driverBR.driver.setBrake(brake);
    }

    @Override
    public void driveMPS(double xMeters, double yMeters, double rotation) {
        ChassisSpeeds speeds;

        //x+ m/s forwards, y+ m/s left, omega+ rad/sec ccw
        if (useFieldOriented() && !dorifto()) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(xMeters, yMeters, rotation, Rotation2d.fromDegrees(-guidance.imu.relativeYaw()));
        } else if (dorifto()) {
            speeds = new ChassisSpeeds(xMeters, 0, rotation);
        } else {
            speeds = new ChassisSpeeds(xMeters, yMeters, rotation);
        }

        driveWithChassisSpeeds(speeds);
    }

    /**
     * Sets the pid for all steering motors
     *
     * @param pid the pid for the swerve steering motors
     * @deprecated (For now, dont use this since the PID in the motors arent continuous)
     */
    @Deprecated
    private void setSteeringPIDS(PID pid) {
        driverFR.steering.setPid(pid);
        driverBR.steering.setPid(pid);
        driverFL.steering.setPid(pid);
        driverBL.steering.setPid(pid);
    }

    /**
     * Sets the pid for all drive motors
     *
     * @param pid the pid for the swerve drive motors
     */
    private void setDrivingPIDS(PID pid) {
        driverFR.driver.setPid(pid);
        driverBR.driver.setPid(pid);
        driverFL.driver.setPid(pid);
        driverBL.driver.setPid(pid);
    }

    public SwerveModuleState[] getStates() {
        return new SwerveModuleState[]{
                driverFL.getState(), driverFR.getState(), driverBL.getState(), driverBR.getState()
        };
    }
}
