package frc.drive.auton;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.misc.ISubsystem;
import frc.misc.UtilFunctions;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

public class RobotTelemetry implements ISubsystem {
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;
    private final DriveManager driver;
    private PigeonIMU pigeon;
    public PIDController headingPID;
    public DifferentialDriveOdometry odometer;

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];
    private double startYaw;

    public RobotTelemetry(DriveManager driver){
        this.driver = driver;
        init();
    }

    //pls yes
    public double realHeadingError(double x, double y) {
        return UtilFunctions.mathematicalMod(headingError(x, y) + 180, 360) - 180;
    }

    private double headingError(double wayX, double wayY) {
        return angleFromHere(wayX, wayY) - fieldHeading();
    }

    private double angleFromHere(double wayX, double wayY) {
        return Math.toDegrees(Math.atan2(wayY - fieldY(), wayX - fieldX()));
    }

    private double fieldHeading() {
        return yawWraparoundAhead();
    }

    /**
     * @return the robot's X position in relation to its starting position(right positive)
     * typically facing away from opposing alliance station
     */
    public double fieldX() {
        return robotTranslation.getX();
    }

    /**
     * @return the robot's Y position in relation to its starting position(away positive)
     * typically facing away from opposing alliance station
     */
    public double fieldY() {
        return robotTranslation.getY();
    }

    public double yawWraparoundAhead() {
        return UtilFunctions.mathematicalMod(yawRel() + 180, 360) - 180;
    }


    /**
     * @return relative to start yaw of pigeon
     */
    public double yawRel() { //return relative(to start) yaw of pigeon
        updatePigeon();
        return (ypr[0] - startYaw);
    }

    /**
     * Updates the Pigeon IMU
     */
    public void updatePigeon() {
        pigeon.getYawPitchRoll(ypr);
    }

    /**
     * Resets the Pigeon IMU
     */
    public void resetPigeon() {
        updatePigeon();
        startypr = ypr;
        startYaw = yawAbs();
    }
    
    public void resetEncoders() {
        driver.resetEncoders();
    }

    public void resetOdometry(Pose2d pose, Rotation2d rotation){
        //odometer.resetPosition(pose, rotation);
        resetPigeon();
        resetEncoders();
        
    }

    /**
     * @return absolute yaw of pigeon
     */
    public double yawAbs() {  //get absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }
    //position conversion -------------------------------------------------------------------------------------------------------

    //getRPM - get wheel RPM from encoder
    //TODO implement for falcos

    public double getRPMLeft() {
        return (driver.leaderL.getEncoder().getVelocity()) / 9;
    }

    public double getRPMRight() {
        return (driver.leaderR.getEncoder().getVelocity()) / 9;
    }

    //getRotations - get wheel rotations on encoder
    //TODO implement for falcos
    public double getRotationsLeft() {
        return (driver.leaderL.getEncoder().getPosition()) / 9;
    }

    public double getRotationsRight() {
        return (driver.leaderR.getEncoder().getPosition()) / 9;
    }

    //getMeters - get wheel meters traveled
    /**
     * @return wheel meters traveled
     */
    public double getMetersLeft() {
        return Units.feetToMeters(getRotationsLeft() * UtilFunctions.wheelCircumference() / 12);
    }

    /**
     * @return wheel meters traveled
     */
    public double getMetersRight() {
        return Units.feetToMeters(getRotationsRight() * UtilFunctions.wheelCircumference() / 12);
    }

    @Override
    public void init() {
        pigeon = new PigeonIMU(RobotMap.PIGEON);
        headingPID = new PIDController(RobotNumbers.HEADING_P, RobotNumbers.HEADING_I, RobotNumbers.HEADING_D);
        odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(yawAbs()), new Pose2d(0, 0, new Rotation2d()));
    }

    @Override
    public void updateTest() {
        //updateGeneric();
    }

    @Override
    public void updateTeleop() {
        //updateGeneric();
    }

    @Override
    public void updateAuton() {
        updateGeneric();
    }

    @Override
    public void updateGeneric() {
        if (RobotToggles.ENABLE_IMU) {
            robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(yawAbs())), getMetersLeft(), getMetersRight());
            robotTranslation = robotPose.getTranslation();
            robotRotation = robotPose.getRotation();
        }
    }
}