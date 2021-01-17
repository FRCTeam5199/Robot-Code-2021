package frc.drive;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.XBoxController;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

//import java.lang.Math;


public class DriveManager {
    private final ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    private final NetworkTableEntry driveRotMult = tab2.add("Rotation Factor", RobotNumbers.TURN_SCALE).getEntry();
    private final PigeonIMU pigeon = new PigeonIMU(RobotMap.PIGEON);
    // private Logger logger = new Logger("drive");
    // private Logger posLogger = new Logger("positions");
    // private Permalogger odo = new Permalogger("distance");
    // wheelbase 27"

    private final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Units.inchesToMeters(22));
    DifferentialDriveOdometry odometer;
    // private BallChameleon chameleon = new BallChameleon();

    private XBoxController controller;
    private CANSparkMax leaderL, leaderR;
    private FollowerMotors followerL, followerR;

    private WPI_TalonFX leaderLTalon, leaderRTalon;
    private WPI_TalonFX followerLTalon1, followerLTalon2, followerRTalon1, followerRTalon2;
    SpeedControllerGroup talonLeft = new SpeedControllerGroup(leaderLTalon, followerLTalon1);
    SpeedControllerGroup talonRight = new SpeedControllerGroup(leaderRTalon, followerRTalon1);
    DifferentialDrive differentialDrive = new DifferentialDrive(talonLeft, talonRight);


    private CANPIDController leftPID;
    private CANPIDController rightPID;


    // private double targetHeading;

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];

    public double currentOmega;

    // private boolean chaseBall;
    //private boolean pointBall;

    private boolean invert;

    public int autoStage = 0;
    public boolean autoComplete = false;
    //private double relLeft;
    //private double relRight;

    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;

    //private double feetDriven = 0;

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


    public void init() {
        autoStage = 0;
        autoComplete = false;
        createDriveMotors();
        initIMU();
        initPID();
        initMisc();
    }

    public void updateTeleop() {
        double turn = -controller.getStickRX();
        double drive;
        if (invert) {
            drive = -controller.getStickLY();
        } else {
            drive = controller.getStickLY();
        }
        if (RobotToggles.DRIVE_USE_SPARKS) {
            leaderL.set(0);
            leaderR.set(0);
            drive(drive, turn);
        } else {
            driveTalon(drive, turn);
            /*leaderLTalon.set(ControlMode.PercentOutput, drive);
            leaderRTalon.set(ControlMode.PercentOutput, drive); */
        }
    }

    public void updateTest() {

    }

    public void updateGeneric() {

    }


    private void initMisc() throws RuntimeException {
        try {
            controller = new XBoxController(0);
        } catch (Exception e) {
            throw new RuntimeException("Xbox controller errored during initialization. You're probably screwed.");
        }
    }

    private void initPID() throws RuntimeException {
        try {
            if (RobotToggles.DRIVE_USE_SPARKS) {
                leftPID = leaderL.getPIDController();
                rightPID = leaderR.getPIDController();
                setPID(RobotNumbers.DRIVEBASE_P, RobotNumbers.DRIVEBASE_I, RobotNumbers.DRIVEBASE_D, RobotNumbers.DRIVEBASE_F);
            } else {

            }
        } catch (Exception e) {
            throw new RuntimeException("PID Init errored during initialization.");
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

    private void drive(double forward, double rotation) {
        drivePure(adjustedDrive(forward), adjustedRotation(rotation));
    }


    private void driveTalon(double forward, double rotation) {
        differentialDrive.arcadeDrive(forward, rotation);
        //drivePure(adjustedDrive(forward), adjustedRotation(rotation));
    }

    private void drivePure(double FPS, double omega) {
        omega *= driveRotMult.getDouble(RobotNumbers.TURN_SCALE);
        currentOmega = omega;
        var chassisSpeeds = new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega);
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);
        double leftVelocity = Units.metersToFeet(wheelSpeeds.leftMetersPerSecond);
        double rightVelocity = Units.metersToFeet(wheelSpeeds.rightMetersPerSecond);
        double mult = 3.8 * 2.16 * RobotNumbers.DRIVE_SCALE;
        if (RobotToggles.DRIVE_USE_SPARKS) {
            //System.out.println("FPS: "+leftVelocity+"  "+rightVelocity+" RPM: "+convertFPStoRPM(leftVelocity)+" "+convertFPStoRPM(rightVelocity));
            leftPID.setReference(convertFPStoRPM(leftVelocity) * mult, ControlType.kVelocity);
            rightPID.setReference(convertFPStoRPM(rightVelocity) * mult, ControlType.kVelocity);
            //System.out.println(leaderL.getEncoder().getVelocity()+" "+leaderR.getEncoder().getVelocity());
        } else {
            //TODO change to closed loop system

            //double targetVelocity_UnitsPer100ms = leftYstick * 2000.0 * 2048.0 / 600.0;
            //leaderLTalon.set(ControlMode.Velocity, convertFPStoRPM(leftVelocity)*mult);
            //leaderRTalon.set(ControlMode.Velocity, convertFPStoRPM(rightVelocity)*mult);
        }
    }

    private double convertFPStoRPM(double FPS) {
        return FPS * (RobotNumbers.MAX_MOTOR_SPEED / RobotNumbers.MAX_SPEED);
    }

    private double adjustedDrive(double input) {
        return input * RobotNumbers.MAX_SPEED;
    }

    private double adjustedRotation(double input) {
        return input * RobotNumbers.MAX_ROTATION;
    }

    private void createDriveMotors() throws RuntimeException {
        try {
            if (RobotToggles.DRIVE_USE_SPARKS) {
                leaderL = new CANSparkMax(RobotMap.DRIVE_LEADER_L, MotorType.kBrushless);
                leaderR = new CANSparkMax(RobotMap.DRIVE_LEADER_R, MotorType.kBrushless);
                if (RobotToggles.DRIVE_USE_6_MOTORS) {
                    followerL = new FollowerMotors(true).createFollowers(MotorType.kBrushless, RobotMap.DRIVE_FOLLOWER_L1, RobotMap.DRIVE_FOLLOWER_L2);
                    followerR = new FollowerMotors(true).createFollowers(MotorType.kBrushless, RobotMap.DRIVE_FOLLOWER_R1, RobotMap.DRIVE_FOLLOWER_R2);
                }

                followerL.follow(leaderL);
                followerR.follow(leaderR);
                leaderL.setInverted(true);
                leaderR.setInverted(false);
            } else {
                leaderLTalon = new WPI_TalonFX(RobotMap.DRIVE_LEADER_L);
                leaderRTalon = new WPI_TalonFX(RobotMap.DRIVE_LEADER_R);
                followerLTalon1 = new WPI_TalonFX(RobotMap.DRIVE_FOLLOWER_L1);
                followerRTalon1 = new WPI_TalonFX(RobotMap.DRIVE_FOLLOWER_R1);


                followerLTalon1.follow(leaderLTalon);
                followerRTalon1.follow(leaderRTalon);

                leaderLTalon.setInverted(RobotToggles.DRIVE_INVERT_LEFT);
                leaderRTalon.setInverted(RobotToggles.DRIVE_INVERT_RIGHT);

                followerRTalon1.setInverted(InvertType.FollowMaster);
                followerLTalon1.setInverted(InvertType.FollowMaster);

                if (RobotToggles.DRIVE_USE_6_MOTORS) {
                    followerLTalon2 = new WPI_TalonFX(RobotMap.DRIVE_FOLLOWER_L2);
                    followerRTalon2 = new WPI_TalonFX(RobotMap.DRIVE_FOLLOWER_R2);

                    followerLTalon2.follow(leaderLTalon);
                    followerRTalon2.follow(leaderRTalon);

                    followerRTalon2.setInverted(InvertType.FollowMaster);
                    followerLTalon2.setInverted(InvertType.FollowMaster);
                }

                // TODO Implement Talon drive base motor system
                //throw new IllegalStateException("Non-Spark motors not implemented.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong creating Spark Max Motors in the drive base.");
        }
    }

    public void drivePIDSparks(double left, double right) {
        leftPID.setReference(left * RobotNumbers.MAX_MOTOR_SPEED, ControlType.kVelocity);
        rightPID.setReference(right * RobotNumbers.MAX_MOTOR_SPEED, ControlType.kVelocity);
    }

    private void setPID(double P, double I, double D, double F) {
        if (RobotToggles.DRIVE_USE_SPARKS) {
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
        } else {

        }
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
            if ((this.USE_TWO_MOTORS) == (ids.length == 2)) {
                throw new RuntimeException("I need to have an equal number of motor IDs as motors in use");
            }
            for (int i = 0; i < ids.length; i++) {
                this.motors[i] = new CANSparkMax(ids[i], motorType);
            }
            return this;
        }

        public void follow(CANSparkMax leader) {
            for (CANSparkMax follower : this.motors) {
                follower.follow(leader);
            }
        }
    }
}