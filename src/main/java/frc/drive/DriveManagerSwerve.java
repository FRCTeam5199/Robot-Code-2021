package frc.drive;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.XBoxController;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.XBoxController;
import frc.misc.ISubsystem;
import frc.misc.PID;
import frc.motors.AbstractMotorController;
import frc.motors.SparkMotorController;
import frc.motors.SupportedMotors;
import frc.telemetry.imu.AbstractIMU;
import frc.telemetry.imu.WrappedNavX2IMU;

/*
notes n stuff

14wide x 22long between wheels

max speed 3.6 m/s

 */
public class DriveManagerSwerve implements ISubsystem {

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
    private AbstractIMU IMU;
    private PIDController FRpid, BRpid, BLpid, FLpid;
    private AbstractMotorController driverFR, driverBR, driverBL, driverFL;
    private AbstractMotorController steeringFR, steeringBR, steeringBL, steeringFL;
    private BaseController xbox;
    private CANCoder FRcoder, BRcoder, BLcoder, FLcoder;

    private SwerveDriveOdometry odometry;
    private Pose2d pose;

    public DriveManagerSwerve() {
        init();
        addToMetaList();
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

        xbox = new XBoxController(0);

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
        resetSteeringEncoders();
        setupSteeringEncoders();
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

    @Override
    public String getSubsystemName() {
        return null;
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

        //x+ m/s forwards, y+ m/s left, omega+ rad/sec ccw
        ChassisSpeeds speeds = new ChassisSpeeds(forwards, leftwards, rotation);

        boolean useFieldOriented = xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER) < 0.1;
        boolean dorifto = xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1;

        if(useFieldOriented&&!dorifto){
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(forwards, leftwards, rotation, Rotation2d.fromDegrees(IMU.relativeYaw()));
        }

        SwerveModuleState[] moduleStates = kinematics.toSwerveModuleStates(speeds);


        if(dorifto){
            double offset = trackLength/2/39.3701;
            offset -= forwards/3;
            System.out.println("forwards: " + forwards);
            moduleStates = kinematics.toSwerveModuleStates(speeds, new Translation2d(offset,0));
        }

        if (xbox.get(ControllerEnums.XBoxButtons.RIGHT_BUMPER) == ControllerEnums.ButtonStatus.DOWN) {
            moduleStates = kinematics.toSwerveModuleStates(speeds, frontRightLocation);
        }

        // Front left module state
        SwerveModuleState frontLeft = moduleStates[0];
        //System.out.println(frontLeft.speedMetersPerSecond + "  |  " + frontLeft.angle.getDegrees());

        // Front right module state
        SwerveModuleState frontRight = moduleStates[1];

        // Back left module state
        SwerveModuleState backLeft = moduleStates[2];

        // Back right module state
        SwerveModuleState backRight = moduleStates[3];

        //try continuous here
        setSteeringContinuous(frontLeft.angle.getDegrees(), frontRight.angle.getDegrees(), backLeft.angle.getDegrees(), backRight.angle.getDegrees());
        System.out.printf("%4f %4f %4f %4f", frontLeft.speedMetersPerSecond, frontRight.speedMetersPerSecond, backLeft.speedMetersPerSecond, backRight.speedMetersPerSecond);
        setDrive(frontLeft.speedMetersPerSecond, frontRight.speedMetersPerSecond, backLeft.speedMetersPerSecond, backRight.speedMetersPerSecond);
    }

    private void setSteeringContinuous(double FL, double FR, double BL, double BR) {
        double FLoffset, FRoffset, BLoffset, BRoffset;
        FLoffset = -1;
        FRoffset = -1;
        BLoffset = 2;
        BRoffset = 2;

        //will this work? good question
        FLpid.setSetpoint(FL+FLoffset);
        FRpid.setSetpoint(FR+FRoffset);
        BRpid.setSetpoint(BR+BRoffset);
        BLpid.setSetpoint(BL+BLoffset);

        steeringFL.moveAtPercent(FLpid.calculate(FLcoder.getAbsolutePosition()));
        steeringFR.moveAtPercent(FRpid.calculate(FRcoder.getAbsolutePosition()));
        steeringBL.moveAtPercent(BLpid.calculate(BLcoder.getAbsolutePosition()));
        steeringBR.moveAtPercent(BRpid.calculate(BRcoder.getAbsolutePosition()));

        System.out.println("FL position: " + FLcoder.getAbsolutePosition());
        System.out.println("FL setpoint: " + (FL));
        System.out.println();
    }

    private void setDrive(double FL, double FR, double BL, double BR) {
        double num = 3.5;
        System.out.println("FL: " + FL);
        System.out.println("FR: " + FR);
        System.out.println("BL: " + BL);
        System.out.println("BR: " + BR);

        driverFR.moveAtPercent(FR / num);
        driverBR.moveAtPercent(BR / num);
        driverFL.moveAtPercent(FL / num);
        driverBL.moveAtPercent(BL / num);
    }

    private void setSteeringPIDS(PID pid) {
        steeringFR.setPid(pid);
        steeringBR.setPid(pid);
        steeringFL.setPid(pid);
        steeringBL.setPid(pid);
    }

    private void setDrivingPIDS(PID pid) {
        driverFR.setPid(pid);
        driverFL.setPid(pid);
        driverFL.setPid(pid);
        driverBL.setPid(pid);
    }

    private void printSPositions() {
        System.out.println("RF: " + steeringFR.getRotations());
    }

    /**
     * ???
     */
    private void jank() {
        driverFR.follow(driverFR);
        driverBR.follow(driverBR);
        driverBL.follow(driverBL);
        driverFL.follow(driverFL);

        steeringFR.follow(steeringFR);
        steeringBR.follow(steeringBR);
        steeringBL.follow(steeringBL);
        steeringFL.follow(steeringFL);
    }

    private void printEncoderPositions() {
        System.out.println(" ");
        System.out.println("LF: " + FLcoder.getPosition() + " | RF: " + FRcoder.getPosition());
        System.out.println("LR: " + BLcoder.getPosition() + " | RR: " + BRcoder.getPosition());
    }

    private void setSteering(double FL, double FR, double BL, double BR) {
        steeringFR.moveAtPosition(FR);
        steeringBR.moveAtPosition(BR);
        steeringFL.moveAtPosition(FL);
        steeringBL.moveAtPosition(BL);
    }
}
