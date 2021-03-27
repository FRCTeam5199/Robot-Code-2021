package frc.drive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;
import com.ctre.phoenix.sensors.CANCoder;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.controllers.BaseController;
import frc.controllers.ControllerEnums;
import frc.controllers.XBoxController;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;


import frc.misc.ISubsystem;

/*
notes n stuff

14wide x 22long between wheels

max speed 3.6 m/s

 */
public class DriveManagerSwerve implements ISubsystem {

    private PIDController FRpid, BRpid, BLpid, FLpid;

    private CANSparkMax driverFR, driverBR, driverBL, driverFL;
    private CANSparkMax steeringFR, steeringBR, steeringBL, steeringFL;
    private BaseController xbox;
    private CANCoder RFCoder, RRCoder, LRCoder, LFCoder;

//    private Translation2d frontLeftLocation = new Translation2d(0.2794, 0.1778);
//    private Translation2d frontRightLocation = new Translation2d(0.2794, -0.1778);
//    private Translation2d backLeftLocation = new Translation2d(-0.2794, 0.1778);
//    private Translation2d backRightLocation = new Translation2d(-0.2794, -0.1778);
    private final Translation2d frontLeftLocation = new Translation2d(-0.2794, 0.1778);
    private final Translation2d frontRightLocation = new Translation2d(-0.2794, -0.1778);
    private final Translation2d backLeftLocation = new Translation2d(0.2794, 0.1778);
    private final Translation2d backRightLocation = new Translation2d(0.2794, -0.1778);

    private SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            frontLeftLocation, frontRightLocation, backLeftLocation, backRightLocation
    );

    public DriveManagerSwerve() {
        init();
        addToMetaList();
    }

    @Override
    public void init() {
        double steeringP = 0;
        double steeringI = 0;
        double steeringD = 0;
        //https://first.wpi.edu/wpilib/allwpilib/docs/release/java/edu/wpi/first/wpilibj/controller/PIDController.html
        FLpid = new PIDController(steeringP,steeringI,steeringD);
        FLpid.enableContinuousInput(0, 360);
        FRpid = new PIDController(steeringP,steeringI,steeringD);
        FRpid.enableContinuousInput(0, 360);
        BLpid = new PIDController(steeringP,steeringI,steeringD);
        BLpid.enableContinuousInput(0, 360);
        BRpid = new PIDController(steeringP,steeringI,steeringD);
        BRpid.enableContinuousInput(0, 360);

        xbox = new XBoxController(0);

        driverFR = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBR = new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL = new CANSparkMax(6, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL.setInverted(true);
        driverFL = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverFL.setInverted(true);

        steeringFR = new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFR.setInverted(true);
        steeringBR = new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringBR.setInverted(true);
        steeringBL = new CANSparkMax(5, CANSparkMaxLowLevel.MotorType.kBrushless);

        steeringFL = new CANSparkMax(8, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFL.setInverted(true);

        steeringFR.getPIDController();
        steeringBR.getPIDController();
        steeringFL.getPIDController();
        steeringBL.getPIDController();

        driverFR.getPIDController();
        driverBR.getPIDController();
        driverFL.getPIDController();
        driverBL.getPIDController();

        //jank();

        setSteeringPIDS(0.006, 0.0000, 0.01);

        RFCoder = new CANCoder(11);
        RRCoder = new CANCoder(12);
        LFCoder = new CANCoder(13);
        LRCoder = new CANCoder(14);
    }

    @Override
    public void updateTest() {
        driveSwerve();
    }

    double val;
    @Override
    public void updateTeleop() {
//        printSPositions();
//        val+= 3;
//        System.out.println(val);
//        SteeringRF.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringRR.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringLF.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringLR.getPIDController().setReference(val, ControlType.kPosition);
        steeringFR.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);
        steeringBR.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);
        steeringFL.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);
        steeringBL.getPIDController().setReference(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*180, ControlType.kPosition);

        double triggerbal = xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER)-xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER);
        System.out.println(triggerbal);
        //triggerbal = 0;
        driverFR.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 + triggerbal/4);
        driverBR.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 + triggerbal/4);
        driverFL.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 - triggerbal/4);
        driverBL.set(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)/2 - triggerbal/4);
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
        val = 0;
        resetSteeringEncoders();
        setupSteeringEncoders();

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
    private void jank(){
        driverFR.follow(driverFR);
        driverBR.follow(driverBR);
        driverBL.follow(driverBL);
        driverFL.follow(driverFL);

        steeringFR.follow(steeringFR);
        steeringBR.follow(steeringBR);
        steeringBL.follow(steeringBL);
        steeringFL.follow(steeringFL);
    }

    private void printEncoderPositions(){
        System.out.println(" ");
        System.out.println("LF: " + LFCoder.getPosition() + " | RF: " + RFCoder.getPosition());
        System.out.println("LR: " + LRCoder.getPosition() + " | RR: " + RRCoder.getPosition());
    }

    private void driveSwerve(){
        double forwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y)*(-2);
        double leftwards = xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_X)*(2);
        double rotation = xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X)*(-3);

        //x+ m/s forwards, y+ m/s left, omega+ rad/sec ccw
        ChassisSpeeds speeds = new ChassisSpeeds(forwards, leftwards, rotation);
        SwerveModuleState[] moduleStates = kinematics.toSwerveModuleStates(speeds);

        // Front left module state
        SwerveModuleState frontLeft = moduleStates[0];
        System.out.println(frontLeft.speedMetersPerSecond + "  |  " + frontLeft.angle.getDegrees());

        // Front right module state
        SwerveModuleState frontRight = moduleStates[1];

        // Back left module state
        SwerveModuleState backLeft = moduleStates[2];

        // Back right module state
        SwerveModuleState backRight = moduleStates[3];

        setSteering(frontLeft.angle.getDegrees(), frontRight.angle.getDegrees(), backLeft.angle.getDegrees(), backRight.angle.getDegrees());
        setDrive(frontLeft.speedMetersPerSecond, frontRight.speedMetersPerSecond, backLeft.speedMetersPerSecond, backRight.speedMetersPerSecond);

    }

    private void setSteering(double FL, double FR, double BL, double BR){
        steeringFR.getPIDController().setReference(FR, ControlType.kPosition);
        steeringBR.getPIDController().setReference(BR, ControlType.kPosition);
        steeringFL.getPIDController().setReference(FL, ControlType.kPosition);
        steeringBL.getPIDController().setReference(BL, ControlType.kPosition);
    }

    private void setDrive(double FL, double FR, double BL, double BR){
        /*
        DriverRF.getPIDController().setReference(FR, ControlType.kVelocity);
        DriverRR.getPIDController().setReference(BR, ControlType.kVelocity);
        DriverLF.getPIDController().setReference(FL, ControlType.kVelocity);
        DriverLR.getPIDController().setReference(BL, ControlType.kVelocity);
         */
        double num = 3;
        driverFR.set(FR/num);
        driverBR.set(BR/num);
        driverFL.set(FL/num);
        driverBL.set(BL/num);
    }
}
