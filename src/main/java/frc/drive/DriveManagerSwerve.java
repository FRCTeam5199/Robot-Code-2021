package frc.drive;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.controller.PIDController;
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

/*
notes n stuff

14wide x 22long between wheels

max speed 3.6 m/s

 */
public class DriveManagerSwerve implements ISubsystem {

    //    private Translation2d frontLeftLocation = new Translation2d(0.2794, 0.1778);
//    private Translation2d frontRightLocation = new Translation2d(0.2794, -0.1778);
//    private Translation2d backLeftLocation = new Translation2d(-0.2794, 0.1778);
//    private Translation2d backRightLocation = new Translation2d(-0.2794, -0.1778);
    private final Translation2d frontLeftLocation = new Translation2d(-0.2794, 0.1778);
    private final Translation2d frontRightLocation = new Translation2d(-0.2794, -0.1778);
    private final Translation2d backLeftLocation = new Translation2d(0.2794, 0.1778);
    private final Translation2d backRightLocation = new Translation2d(0.2794, -0.1778);
    double val;
    private PIDController FRpid, BRpid, BLpid, FLpid;
    private AbstractMotorController driverFR, driverBR, driverBL, driverFL;
    private AbstractMotorController steeringFR, steeringBR, steeringBL, steeringFL;
    private BaseController xbox;
    private CANCoder FRcoder, BRcoder, BLcoder, FLcoder;
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
        FLpid = new PIDController(steeringP, steeringI, steeringD);
        FLpid.enableContinuousInput(0, 360);
        FRpid = new PIDController(steeringP, steeringI, steeringD);
        FRpid.enableContinuousInput(0, 360);
        BLpid = new PIDController(steeringP, steeringI, steeringD);
        BLpid.enableContinuousInput(0, 360);
        BRpid = new PIDController(steeringP, steeringI, steeringD);
        BRpid.enableContinuousInput(0, 360);

        xbox = new XBoxController(0);

        driverFR = new SparkMotorController(1);//new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBR = new SparkMotorController(4);//new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL = new SparkMotorController(6);//new CANSparkMax(6, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverBL.setInverted(true);
        driverFL = new SparkMotorController(7);//new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
        driverFL.setInverted(true);

        steeringFR = new SparkMotorController(2);//new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFR.setInverted(true);
        steeringBR = new SparkMotorController(3);//new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringBR.setInverted(true);
        steeringBL = new SparkMotorController(5);//new CANSparkMax(5, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFL = new SparkMotorController(8);//new CANSparkMax(8, CANSparkMaxLowLevel.MotorType.kBrushless);
        steeringFL.setInverted(true);

        setSteeringPIDS(new PID(0.006, 0.0000, 0.01));

        FRcoder = new CANCoder(11);
        BRcoder = new CANCoder(12);
        FLcoder = new CANCoder(13);
        BLcoder = new CANCoder(14);
    }

    @Override
    public void updateTest() {
        driveSwerve();
    }

    @Override
    public void updateTeleop() {
//        printSPositions();
//        val+= 3;
//        System.out.println(val);
//        SteeringRF.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringRR.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringLF.getPIDController().setReference(val, ControlType.kPosition);
//        SteeringLR.getPIDController().setReference(val, ControlType.kPosition);
        //pass in resultant degrees because S2RDF is already set
        steeringFR.moveAtPosition(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * 180);
        steeringBR.moveAtPosition(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * 180);
        steeringFL.moveAtPosition(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * 180);
        steeringBL.moveAtPosition(xbox.get(ControllerEnums.XboxAxes.RIGHT_JOY_X) * 180);

        double triggerbal = xbox.get(ControllerEnums.XboxAxes.LEFT_TRIGGER) - xbox.get(ControllerEnums.XboxAxes.RIGHT_TRIGGER);
        System.out.println(triggerbal);
        //triggerbal = 0;
        driverFR.moveAtPercent(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) / 2 + triggerbal / 4);
        driverBR.moveAtPercent(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) / 2 + triggerbal / 4);
        driverFL.moveAtPercent(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) / 2 - triggerbal / 4);
        driverBL.moveAtPercent(xbox.get(ControllerEnums.XboxAxes.LEFT_JOY_Y) / 2 - triggerbal / 4);
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
     * reset steering motor encoders
     */
    private void resetSteeringEncoders() {
//        SteeringRF.getEncoder().setPosition(RFCoder.getAbsolutePosition());
//        SteeringRR.getEncoder().setPosition(RRCoder.getAbsolutePosition());
//        SteeringLF.getEncoder().setPosition(LFCoder.getAbsolutePosition());
//        SteeringLR.getEncoder().setPosition(LRCoder.getAbsolutePosition());
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

    private void setSteering(double FL, double FR, double BL, double BR) {
        steeringFR.moveAtPosition(FR);
        steeringBR.moveAtPosition(BR);
        steeringFL.moveAtPosition(FL);
        steeringBL.moveAtPosition(BL);
    }

    /*private void setDrivingPIDS(double P, double I, double D) {
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
    }*/

    private void setDrive(double FL, double FR, double BL, double BR) {
        /*
        DriverRF.getPIDController().setReference(FR, ControlType.kVelocity);
        DriverRR.getPIDController().setReference(BR, ControlType.kVelocity);
        DriverLF.getPIDController().setReference(FL, ControlType.kVelocity);
        DriverLR.getPIDController().setReference(BL, ControlType.kVelocity);
         */
        double num = 3;
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

    private void setSteeringContinuous(double FL, double FR, double BL, double BR) {
        //will this work? good question
        FLpid.setSetpoint(FL);
        FRpid.setSetpoint(FR);
        BRpid.setSetpoint(BR);
        BLpid.setSetpoint(BL);

        steeringFL.moveAtPercent(FLpid.calculate(FLcoder.getPosition()));
        steeringFR.moveAtPercent(FRpid.calculate(FRcoder.getPosition()));
        steeringBL.moveAtPercent(BLpid.calculate(BLcoder.getPosition()));
        steeringBR.moveAtPercent(BRpid.calculate(BRcoder.getPosition()));
    }
}
