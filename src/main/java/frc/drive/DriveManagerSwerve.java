package frc.drive;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.XBoxController;
import frc.misc.PID;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.SupportedMotors;
import frc.telemetry.imu.AbstractIMU;
import frc.telemetry.imu.WrappedNavX2IMU;

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
    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            frontLeftLocation, frontRightLocation, backLeftLocation, backRightLocation
    );
    public SwerveModuleState[] moduleStates;
    private AbstractIMU IMU;
    private PIDController FRpid, BRpid, BLpid, FLpid;
    private AbstractMotorController driverFR, driverBR, driverBL, driverFL;
    private AbstractMotorController steeringFR, steeringBR, steeringBL, steeringFL;
    private BaseController xbox;
    private CANCoder FRcoder, BRcoder, BLcoder, FLcoder;
    private SwerveDriveOdometry odometry;
    private Pose2d pose;

    public DriveManagerSwerve() {
        super();
    }

    @Override
    public void init() {
        PID steeringPID = new PID(0.0035, 0.000001, 0);
        //https://first.wpi.edu/wpilib/allwpilib/docs/release/java/edu/wpi/first/wpilibj/controller/PIDController.html
        FLpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        FRpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        BLpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        BRpid = new PIDController(steeringPID.P, steeringPID.I, steeringPID.D);
        FLpid.enableContinuousInput(-180, 180);
        FRpid.enableContinuousInput(-180, 180);
        BLpid.enableContinuousInput(-180, 180);
        BRpid.enableContinuousInput(-180, 180);

        xbox = XBoxController.createOrGet(0);

        driverFR = new SparkMotorController(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBR = new SparkMotorController(4, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL = new SparkMotorController(6, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverFL = new SparkMotorController(7, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL.setInverted(true);
        driverFL.setInverted(true);

        driverFR.setBrake(true);
        driverFL.setBrake(true);
        driverBR.setBrake(true);
        driverBL.setBrake(true);

        steeringFR = new SparkMotorController(2, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringBR = new SparkMotorController(3, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringBL = new SparkMotorController(5, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFL = new SparkMotorController(8, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFR.setInverted(true);
        steeringBR.setInverted(true);
        steeringBL.setInverted(true);
        steeringFL.setInverted(true);

        //setSteeringPIDS(new PID(0.005, 0.0000, 0.01));

        FRcoder = new CANCoder(11);
        BRcoder = new CANCoder(12);
        FLcoder = new CANCoder(13);
        BLcoder = new CANCoder(14);

        driverFR.setSensorToRealDistanceFactor(1.0 / SupportedMotors.CAN_SPARK_MAX.MAX_SPEED_RPM);
        driverBR.setSensorToRealDistanceFactor(1.0 / SupportedMotors.CAN_SPARK_MAX.MAX_SPEED_RPM);
        driverFL.setSensorToRealDistanceFactor(1.0 / SupportedMotors.CAN_SPARK_MAX.MAX_SPEED_RPM);
        driverBL.setSensorToRealDistanceFactor(1.0 / SupportedMotors.CAN_SPARK_MAX.MAX_SPEED_RPM);
        IMU = new WrappedNavX2IMU();
    }

    @Override
    public void updateTest() {
        System.out.println(FRcoder.getAbsolutePosition() + " FR " + steeringFR.getRotations());
        System.out.println(FLcoder.getAbsolutePosition() + " FL " + steeringFL.getRotations());
        System.out.println(BRcoder.getAbsolutePosition() + " BR " + steeringBR.getRotations());
        System.out.println(BLcoder.getAbsolutePosition() + " BL " + steeringBL.getRotations());
        System.out.println();
        System.out.println(IMU.relativeYaw());
    }

    @Override
    public void updateTeleop() {
        driveSwerve();
        if (xbox.get(ControllerEnums.XBoxButtons.LEFT_BUMPER) == ControllerEnums.ButtonStatus.DOWN) {
            IMU.resetOdometry();
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
        steeringFR.resetEncoder();
        steeringBR.resetEncoder();
        steeringFL.resetEncoder();
        steeringBL.resetEncoder();
    }

    /**
     * set steering motors to return their encoder position in degrees
     */
    private void setupSteeringEncoders() {
        //12.8:1
        steeringFR.setSensorToRealDistanceFactor((1 / 12.8) * 360);
        steeringBR.setSensorToRealDistanceFactor((1 / 12.8) * 360);
        steeringFL.setSensorToRealDistanceFactor((1 / 12.8) * 360);
        steeringBL.setSensorToRealDistanceFactor((1 / 12.8) * 360);
    }

    private void driveSwerve() {
        double forwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) * (-2);
        double leftwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_X) * (2);
        double rotation = xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * (-3);

        boolean useFieldOriented = xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER) < 0.1;
        boolean dorifto = xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1;

        ChassisSpeeds speeds;

        //x+ m/s forwards, y+ m/s left, omega+ rad/sec ccw
        if (useFieldOriented && !dorifto) {
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(forwards, leftwards, rotation, Rotation2d.fromDegrees(-IMU.relativeYaw()));
        } else if (dorifto) {
            speeds = new ChassisSpeeds(forwards, 0, rotation);
        } else {
            speeds = new ChassisSpeeds(forwards, leftwards, rotation);
        }


        moduleStates = kinematics.toSwerveModuleStates(speeds);

        if (xbox.get(ControllerEnums.XBoxButtons.RIGHT_BUMPER) == ControllerEnums.ButtonStatus.DOWN) {
            moduleStates = kinematics.toSwerveModuleStates(speeds, frontRightLocation);
        } else if (dorifto) {
            double driftOffset = 3; //3
            double offset = trackLength / 2 / 39.3701;
            offset -= forwards / driftOffset;
            System.out.println("forwards: " + forwards);
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

        //ODOMETRY
        //Pose2d robotPose = odometry.update(Rotation2d.fromDegrees(IMU.relativeYaw()), frontLeft, frontRight, backLeft, backRight);
        //System.out.println("X: " +  + "\nY: " + robotPose.getY() + "\nRot: " + robotPose.getRotation());
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

        steeringFL.moveAtPercent(FLpid.calculate(FLcoder.getAbsolutePosition()));
        steeringFR.moveAtPercent(FRpid.calculate(FRcoder.getAbsolutePosition()));
        steeringBL.moveAtPercent(BLpid.calculate(BLcoder.getAbsolutePosition()));
        steeringBR.moveAtPercent(BRpid.calculate(BRcoder.getAbsolutePosition()));
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
        driverFR.moveAtPercent(FR / num);
        driverBR.moveAtPercent(BR / num);
        driverFL.moveAtPercent(FL / num);
        driverBL.moveAtPercent(BL / num);
    }

    //TODO implement this in regard to telem
    @Override
    public void resetDriveEncoders() {
        driverFR.resetEncoder();
        driverFL.resetEncoder();
        driverBR.resetEncoder();
        driverBL.resetEncoder();
    }

    @Override
    public void setBrake(boolean brake) {
        driverFR.setBrake(brake);
        driverFL.setBrake(brake);
        driverBL.setBrake(brake);
        driverBR.setBrake(brake);
    }

    /**
     * Sets the pid for all steering motors
     *
     * @param pid the pid for the swerve steering motors
     * @deprecated (For now, dont use this since the PID in the motors arent continuous)
     */
    @Deprecated
    private void setSteeringPIDS(PID pid) {
        steeringFR.setPid(pid);
        steeringBR.setPid(pid);
        steeringFL.setPid(pid);
        steeringBL.setPid(pid);
    }

    /**
     * Sets the pid for all drive motors
     *
     * @param pid the pid for the swerve drive motors
     */
    private void setDrivingPIDS(PID pid) {
        driverFR.setPid(pid);
        driverFL.setPid(pid);
        driverFL.setPid(pid);
        driverBL.setPid(pid);
    }
}
