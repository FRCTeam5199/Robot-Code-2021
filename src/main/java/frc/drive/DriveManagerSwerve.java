package frc.drive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import com.ctre.phoenix.sensors.CANCoder;
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

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;


import frc.misc.ISubsystem;

/*
notes n stuff

14wide x 22long between wheels

max speed 3.6 m/s

 */
public class DriveManagerSwerve implements ISubsystem {

    AHRS ahrs;
    double startAngle;

    private PIDController FRpid, BRpid, BLpid, FLpid;

    private CANSparkMax driverFR, driverBR, driverBL, driverFL;
    private CANSparkMax steeringFR, steeringBR, steeringBL, steeringFL;
    private BaseController xbox;
    private CANCoder FRcoder, BRcoder, BLcoder, FLcoder;

    //    private Translation2d frontLeftLocation = new Translation2d(0.2794, 0.1778);
//    private Translation2d frontRightLocation = new Translation2d(0.2794, -0.1778);
//    private Translation2d backLeftLocation = new Translation2d(-0.2794, 0.1778);
//    private Translation2d backRightLocation = new Translation2d(-0.2794, -0.1778);

    private final Translation2d driftOffset = new Translation2d(-0.6, 0);

    private final double trackWidth = 13.25;
    private final double trackLength = 21.5;

    private final Translation2d frontLeftLocation = new Translation2d(-trackLength/2/39.3701, trackWidth/2/39.3701);
    private final Translation2d frontRightLocation = new Translation2d(-trackLength/2/39.3701, -trackWidth/2/39.3701);
    private final Translation2d backLeftLocation = new Translation2d(trackLength/2/39.3701, trackWidth/2/39.3701);
    private final Translation2d backRightLocation = new Translation2d(trackLength/2/39.3701, -trackWidth/2/39.3701);

    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            frontLeftLocation, frontRightLocation, backLeftLocation, backRightLocation
    );

    private SwerveDriveOdometry odometry;
    private Pose2d pose;

    public DriveManagerSwerve() {
        init();
        addToMetaList();
    }

    @Override
    public void init() {
        double steeringP = 0.004;
        double steeringI = 0.000001;
        double steeringD = 0;
        //https://first.wpi.edu/wpilib/allwpilib/docs/release/java/edu/wpi/first/wpilibj/controller/PIDController.html
        FLpid = new PIDController(steeringP, steeringI, steeringD);
        FLpid.enableContinuousInput(-180, 180);
        //FLpid.disableContinuousInput();
        FRpid = new PIDController(steeringP, steeringI, steeringD);
        FRpid.enableContinuousInput(-180, 180);
        BLpid = new PIDController(steeringP, steeringI, steeringD);
        BLpid.enableContinuousInput(-180, 180);
        BRpid = new PIDController(steeringP, steeringI, steeringD);
        BRpid.enableContinuousInput(-180, 180);

        xbox = new XBoxController(0);

        driverFR = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBR = new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL = new CANSparkMax(6, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL.setInverted(true);
        driverFL = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverFL.setInverted(true);

        driverFR.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driverFL.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driverBR.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driverBL.setIdleMode(CANSparkMax.IdleMode.kBrake);

        steeringFR = new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFR.setInverted(true);
        steeringBR = new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringBR.setInverted(true);
        steeringBL = new CANSparkMax(5, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringBL.setInverted(true);

        steeringFL = new CANSparkMax(8, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFL.setInverted(true);

//        steeringFR.getPIDController();
//        steeringBR.getPIDController();
//        steeringFL.getPIDController();
//        steeringBL.getPIDController();
//
//        driverFR.getPIDController();
//        driverBR.getPIDController();
//        driverFL.getPIDController();
//        driverBL.getPIDController();

        //jank();

        setSteeringPIDS(0.006, 0.0000, 0.01);

        FRcoder = new CANCoder(11);
        BRcoder = new CANCoder(12);
        FLcoder = new CANCoder(13);
        BLcoder = new CANCoder(14);

        try {

            ahrs = new AHRS(SPI.Port.kMXP);
            ahrs.calibrate();
            ahrs.reset();
            startAngle = ahrs.getYaw();
        } catch (RuntimeException ex ) {
            System.out.println("gyro machine broke");
        }

        odometry = new SwerveDriveOdometry(kinematics, Rotation2d.fromDegrees(compassHeading()), new Pose2d(0, 0, new Rotation2d()));
        pose = new Pose2d(new Translation2d(0,0),Rotation2d.fromDegrees(compassHeading()));
    }

    @Override
    public void updateTest() {
        //pose = odometry.update(compassHeading(), m_frontLeftModule.getState(), m_frontRightModule.getState(), m_backLeftModule.getState(), m_backRightModule.getState());
//        driveSwerve();
//        System.out.println(FRcoder.getAbsolutePosition() + " FR " + steeringFR.getEncoder().getPosition());
//        System.out.println(FLcoder.getAbsolutePosition() + " FL " + steeringFL.getEncoder().getPosition());
//        System.out.println(BRcoder.getAbsolutePosition() + " BR " + steeringBR.getEncoder().getPosition());
//        System.out.println(BLcoder.getAbsolutePosition() + " BL " + steeringBL.getEncoder().getPosition());
//        System.out.println();
        //setSteeringContinuous(0,0,0,0);

        System.out.println(compassHeading());
        //System.out.println(ahrs.getCompassHeading());
    }

    double val;

    @Override
    public void updateTeleop() {

        driveSwerve();

        if(xbox.get(ControllerEnums.XBoxButtons.LEFT_BUMPER) == ControllerEnums.ButtonStatus.DOWN){
            startAngle = ahrs.getYaw();
        }
//        printSPositions();
//        val+= 3;
//        System.out.println(val);
//        SteeringRF.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringRR.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringLF.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringLR.getPIDController().setReference(val, ControlType.kPosition);
//        steeringFR.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);
//        steeringBR.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);
//        steeringFL.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);
//        steeringBL.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);

//        double triggerbal = xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER)-xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER);
//        System.out.println(triggerbal);
//        //triggerbal = 0;
//        driverFR.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 + triggerbal/4);
//        driverBR.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 + triggerbal/4);
//        driverFL.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 - triggerbal/4);
//        driverBL.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 - triggerbal/4);
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

        driverFR.setIdleMode(CANSparkMax.IdleMode.kCoast);
        driverFL.setIdleMode(CANSparkMax.IdleMode.kCoast);
        driverBR.setIdleMode(CANSparkMax.IdleMode.kCoast);
        driverBL.setIdleMode(CANSparkMax.IdleMode.kCoast);
    }

    @Override
    public void initTeleop() {
        val = 0;
        resetSteeringEncoders();
        setupSteeringEncoders();
        startAngle = ahrs.getYaw();

        driverFR.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driverFL.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driverBR.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driverBL.setIdleMode(CANSparkMax.IdleMode.kBrake);

//        DriverRF.setIdleMode(CANSparkMax.IdleMode.kBrake);
//        DriverLF.setIdleMode(CANSparkMax.IdleMode.kBrake);
//        DriverLR.setIdleMode(CANSparkMax.IdleMode.kBrake);
//        DriverRR.setIdleMode(CANSparkMax.IdleMode.kBrake);
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
     * set steering motors to return their encoder position in degrees
     */
    private void setupSteeringEncoders() {
        //12.8:1
        steeringFR.getEncoder().setPositionConversionFactor((1 / 12.8) * 360);
        steeringBR.getEncoder().setPositionConversionFactor((1 / 12.8) * 360);
        steeringFL.getEncoder().setPositionConversionFactor((1 / 12.8) * 360);
        steeringBL.getEncoder().setPositionConversionFactor((1 / 12.8) * 360);
    }

    /**
     * reset steering motor encoders
     */
    private void resetSteeringEncoders() {
//        SteeringRF.getEncoder().setPosition(RFCoder.getAbsolutePosition());
//        SteeringRR.getEncoder().setPosition(RRCoder.getAbsolutePosition());
//        SteeringLF.getEncoder().setPosition(LFCoder.getAbsolutePosition());
//        SteeringLR.getEncoder().setPosition(LRCoder.getAbsolutePosition());
        steeringFR.getEncoder().setPosition(0);
        steeringBR.getEncoder().setPosition(0);
        steeringFL.getEncoder().setPosition(0);
        steeringBL.getEncoder().setPosition(0);
    }

    private void printSPositions() {
        System.out.println("RF: " + steeringFR.getEncoder().getPosition());
    }

    private void setSteeringPIDS(double P, double I, double D) {
        steeringFR.getPIDController().setP(P);
        steeringFR.getPIDController().setI(I);
        steeringFR.getPIDController().setD(D);

        steeringBR.getPIDController().setP(P);
        steeringBR.getPIDController().setI(I);
        steeringBR.getPIDController().setD(D);

        steeringFL.getPIDController().setP(P);
        steeringFL.getPIDController().setI(I);
        steeringFL.getPIDController().setD(D);

        steeringBL.getPIDController().setP(P);
        steeringBL.getPIDController().setI(I);
        steeringBL.getPIDController().setD(D);
    }

    private void setDrivingPIDS(double P, double I, double D) {
        steeringFR.getPIDController().setP(P);
        steeringFR.getPIDController().setI(I);
        steeringFR.getPIDController().setD(D);

        steeringBR.getPIDController().setP(P);
        steeringBR.getPIDController().setI(I);
        steeringBR.getPIDController().setD(D);

        steeringFL.getPIDController().setP(P);
        steeringFL.getPIDController().setI(I);
        steeringFL.getPIDController().setD(D);

        steeringBL.getPIDController().setP(P);
        steeringBL.getPIDController().setI(I);
        steeringBL.getPIDController().setD(D);
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

    private void driveSwerve() {
        double forwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) * (-2);
        double leftwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_X) * (2);
        double rotation = xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * (-3);

        //x+ m/s forwards, y+ m/s left, omega+ rad/sec ccw
        ChassisSpeeds speeds = new ChassisSpeeds(forwards, leftwards, rotation);

        boolean useFieldOriented = xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER) < 0.1;
        boolean dorifto = xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER) > 0.1;

        if(useFieldOriented&&!dorifto){
            speeds = ChassisSpeeds.fromFieldRelativeSpeeds(forwards, leftwards, rotation, Rotation2d.fromDegrees(compassHeading()));
        }

        SwerveModuleState[] moduleStates = kinematics.toSwerveModuleStates(speeds);


        if(dorifto){
            double offset = trackLength/2/39.3701;
            offset -= forwards/3;
            System.out.println("forwards: " + forwards);
            moduleStates = kinematics.toSwerveModuleStates(speeds, new Translation2d(offset,0));
        }

        if(xbox.get(ControllerEnums.XBoxButtons.RIGHT_BUMPER) == ControllerEnums.ButtonStatus.DOWN){
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
        setDrive(frontLeft.speedMetersPerSecond, frontRight.speedMetersPerSecond, backLeft.speedMetersPerSecond, backRight.speedMetersPerSecond);

    }

    private void setSteering(double FL, double FR, double BL, double BR) {
        steeringFR.getPIDController().setReference(FR, ControlType.kPosition);
        steeringBR.getPIDController().setReference(BR, ControlType.kPosition);
        steeringFL.getPIDController().setReference(FL, ControlType.kPosition);
        steeringBL.getPIDController().setReference(BL, ControlType.kPosition);
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

        steeringFL.set(FLpid.calculate(FLcoder.getAbsolutePosition()));
        steeringFR.set(FRpid.calculate(FRcoder.getAbsolutePosition()));
        steeringBL.set(BLpid.calculate(BLcoder.getAbsolutePosition()));
        steeringBR.set(BRpid.calculate(BRcoder.getAbsolutePosition()));

        //System.out.println("FL pid out: " + FLpid.calculate(FLcoder.getAbsolutePosition()));
        System.out.println("FL position: " + FLcoder.getAbsolutePosition());
        System.out.println("FL setpoint: " + (FL));
//        System.out.println("FR: " + FRpid.calculate(FRcoder.getAbsolutePosition()));
//        System.out.println("BR: " + BRpid.calculate(BRcoder.getAbsolutePosition()));
//        System.out.println("BL: " + BLpid.calculate(BLcoder.getAbsolutePosition()));
        System.out.println();
        //System.out.println("error: " + FLpid.getPositionError());
    }

    private void setDrive(double FL, double FR, double BL, double BR) {
        /*
        DriverRF.getPIDController().setReference(FR, ControlType.kVelocity);
        DriverRR.getPIDController().setReference(BR, ControlType.kVelocity);
        DriverLF.getPIDController().setReference(FL, ControlType.kVelocity);
        DriverLR.getPIDController().setReference(BL, ControlType.kVelocity);
         */
        double num = 3;
        driverFR.set(FR / num);
        driverBR.set(BR / num);
        driverFL.set(FL / num);
        driverBL.set(BL / num);
    }

    private double compassHeading(){
        return ahrs.getYaw()-startAngle;
    }
}
