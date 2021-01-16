package frc.drive;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

import frc.controllers.XBoxController;

import java.io.IOException;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.controller.PIDController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

//import frc.util.Logger;

//import frc.vision.BallChameleon;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.networktables.*;

import java.lang.Math;

public class DriveManager {
    private PigeonIMU pigeon = new PigeonIMU(RobotMap.pigeon);
    // private Logger logger = new Logger("drive");
    // private Logger posLogger = new Logger("positions");
    // private Permalogger odo = new Permalogger("distance");
    // wheelbase 27"
    private DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Units.inchesToMeters(22));
    DifferentialDriveOdometry odometer;
    // private BallChameleon chameleon = new BallChameleon();

    private XBoxController controller;
    private CANSparkMax leaderL, leaderR;
    private FollowerMotors followerL, followerR;

    private CANPIDController leftPID;
    private CANPIDController rightPID;

    // private double targetHeading;

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];

    public double currentOmega;

    // private boolean chaseBall;
    private boolean pointBall;

    private boolean invert;

    public int autoStage = 0;
    public boolean autoComplete = false;
    private double relLeft;
    private double relRight;

    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;

    private double feetDriven = 0;

    // private ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    // private NetworkTableEntry driveP = tab2.add("P",
    // RobotNumbers.drivebaseP).getEntry();
    // private NetworkTableEntry driveI = tab2.add("I",
    // RobotNumbers.drivebaseI).getEntry();
    // private NetworkTableEntry driveD = tab2.add("D",
    // RobotNumbers.drivebaseD).getEntry();
    // private NetworkTableEntry driveF = tab2.add("F",
    // RobotNumbers.drivebaseF).getEntry();
    // private NetworkTableEntry driveRotMult = tab2.add("Rotation Factor",
    // RobotNumbers.turnScale).getEntry();
    // private DoubleSolenoid solenoidShifterL, solenoidShifterR;
    // private Solenoid shifter;

    public DriveManager() {
        init();
        // headControl = new PIDController(Kp, Ki, Kd);
    }

    /**
     * Initialize the Driver object.
     */
    public void init(){
        autoStage = 0;
        autoComplete = false;
        createDriveMotors();
        initIMU();
        initPID();
        initMisc();
    }

    public void updateTest(){
        double turn = -controller.getStickRX();
        double drive;
        if(invert){
            drive = -controller.getStickLY();
        }
        else{
            drive = controller.getStickLY();
        }
        leaderL.set(0);
        leaderR.set(0);
    }


    private void initMisc() throws RuntimeException{
        try {
            controller = new XBoxController(0);
        } catch (Exception e) {
            throw new RuntimeException("Xbox controller errored during initialization. You're probably screwed.");
        }
    }

    private void initPID() throws RuntimeException {
        try {
            leftPID = leaderL.getPIDController();
            rightPID = leaderR.getPIDController();
            setPID(RobotNumbers.drivebaseP, RobotNumbers.drivebaseI, RobotNumbers.drivebaseD, RobotNumbers.drivebaseF);
        } catch (Exception e) {
            throw new RuntimeException("Pigeon has caused some problems during initialization.");
        }
    }

    private void initIMU() throws RuntimeException {
        try {
            if (RobotToggles.ENABLE_IMU) {
                resetPigeon();
                updatePigeon();
            }
        } catch (Exception e) {
            throw new RuntimeException("Pigeon IMU Failed");
        }
    }

    private void createDriveMotors() throws RuntimeException {
        try {
            if (RobotToggles.DRIVE_USE_SPARKS) {
                leaderL = new CANSparkMax(RobotMap.DRIVE_LEADER_L, MotorType.kBrushless);
                leaderR = new CANSparkMax(RobotMap.DRIVE_LEADER_R, MotorType.kBrushless);
                if (RobotToggles.DRIVE_USE_6_WHEELS) {
                    followerL = new FollowerMotors(true).createFollowers(MotorType.kBrushless,
                            RobotMap.DRIVE_FOLLOWER_L1, RobotMap.DRIVE_FOLLOWER_L2);
                    followerR = new FollowerMotors(true).createFollowers(MotorType.kBrushless,
                            RobotMap.DRIVE_FOLLOWER_R1, RobotMap.DRIVE_FOLLOWER_R2);
                }

                followerL.follow(leaderL);
                followerR.follow(leaderR);
                leaderL.setInverted(true);
                leaderR.setInverted(false);
            } else {
                // TODO Implement Victor drive base motor system
                throw new IllegalStateException("Non-Spark motors not implemented.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong creating Spark Max Motors in the drive base.");
        }
    }

    public void drivePID(double left, double right) {
        leftPID.setReference(left * RobotNumbers.maxMotorSpeed, ControlType.kVelocity);
        rightPID.setReference(right * RobotNumbers.maxMotorSpeed, ControlType.kVelocity);
    }

    private void setPID(double P, double I, double D, double F) {
        leftPID.setP(P);
        leftPID.setI(I);
        leftPID.setD(D);
        leftPID.setFF(F);
        rightPID.setP(P);
        rightPID.setI(I);
        rightPID.setD(D);
        rightPID.setFF(F);

        leftPID.setOutputRange(-1, 1);
        rightPID.setOutputRange(-1, 1);
    }

    // Pigeon IMU
    private double startYaw;

    public void updatePigeon() {
        pigeon.getYawPitchRoll(ypr);
    }

    public void resetPigeon() {
        updatePigeon();
        startypr = ypr;
        startYaw = yawAbs();
    }

    public double yawAbs() { // return absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }

    // Any Operation that you do on one motor, implement in here so that a seamless
    // transition can occur
    public class FollowerMotors {
        private final boolean USE_TWO_MOTORS;

        private final CANSparkMax[] motors;

        public FollowerMotors(boolean USE_TWO_MOTORS) {
            this.USE_TWO_MOTORS = USE_TWO_MOTORS;
            this.motors = new CANSparkMax[2];
        }

        // I assume that both motors are of the same type
        // if using two followers, the first int is the first motor id, and the second
        // the second
        public FollowerMotors createFollowers(MotorType motorType, int... ids) {
            if ((this.USE_TWO_MOTORS) == (ids.length == 2))
                throw new RuntimeException("I need to have an equal number of motor IDs as motors in use");
            for (int i = 0; i < ids.length; i++) {
                this.motors[i] = new CANSparkMax(ids[i], motorType);
            }
            return this;
        }

        public void follow(CANSparkMax leader) {
            for (CANSparkMax follower : this.motors)
                follower.follow(leader);
        }
    }
}