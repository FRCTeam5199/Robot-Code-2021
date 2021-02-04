package frc.drive.auton;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;
import frc.drive.DriveManager;
import frc.misc.UtilFunctions;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

public class RobotTelemetry {
    public Pose2d robotPose;
    public Translation2d robotTranslation;
    public Rotation2d robotRotation;
    private DriveManager driver;
    private final PigeonIMU pigeon = new PigeonIMU(RobotMap.PIGEON);
    public PIDController headingPID;

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];
    private double startYaw;

    public RobotTelemetry(DriveManager driver){
        this.driver = driver;
        headingPID = new PIDController(RobotNumbers.HEADING_P, RobotNumbers.HEADING_I, RobotNumbers.HEADING_D);
    }

    //pls yes
    /**
     * @param x 
     * @param y 
     */
    public double headingErrorWraparound(double x, double y) {
        return UtilFunctions.mathematicalMod(headingError(x, y) + 180, 360) - 180;
    }

    private double headingError(double wayX, double wayY) {
        return angleToPos(wayX, wayY) - fieldHeading();
    }

    private double angleToPos(double wayX, double wayY) {
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

    /**
     * @return absolute yaw of pigeon
     */
    public double yawAbs() {  //get absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }

    /**
     * @return wheel FPS from encoder
     */
    public double getFPSLeft() { //get wheel FPS from encoder
        return getIPSLeft() / 12;
    }

    //getIPS - get wheel IPS from encoder
    /**
     * @return wheel IPS from encoder
     */
    public double getIPSLeft() {
        return (getRPMLeft() * wheelCircumference()) / 60;
    }

    //getRPM - get wheel RPM from encoder
    /** 
     * @return wheel RPM from encoder for lL
     */
    public double getRPMLeft() {
        return (driver.leaderL.getEncoder().getVelocity()) / 9;
    }

    //position conversion -------------------------------------------------------------------------------------------------------
    /**
     * @return circumference of wheel
     */
    private double wheelCircumference() {
        return RobotNumbers.WHEEL_DIAMETER * Math.PI;
    }

    /**
     * @return FPS from encoder
     */
    public double getFPSRight() {
        return getIPSRight() / 12;
    }

    /**
     * @return IPS from encoder
     */
    public double getIPSRight() {
        return (getRPMRight() * wheelCircumference()) / 60;
    }

    /**
     * @return wheel RPM from encoder for lR
     */
    public double getRPMRight() {
        return (driver.leaderR.getEncoder().getVelocity()) / 9;
    }

    //getInches - get wheel inches traveled
    /**
     * @return wheel inches traveled
     */
    public double getInchesLeft() {
        return (getRotationsLeft() * wheelCircumference());
    }

    //getRotations - get wheel rotations on encoder
    /**
     * @return wheel rotations for lL
     */
    public double getRotationsLeft() {
        return (driver.leaderL.getEncoder().getPosition()) / 9;
    }

    /**
     * @return wheel inches traveled
     */
    public double getInchesRight() {
        return (getRotationsRight() * wheelCircumference());
    }

    /**
     * @return wheel rotations for lR
     */
    public double getRotationsRight() {
        return (driver.leaderR.getEncoder().getPosition()) / 9;
    }

    //getMeters - get wheel meters traveled
    /**
     * @return wheel meters traveled
     */
    public double getMetersLeft() {
        return Units.feetToMeters(getFeetLeft());
    }

    //getFeet - get wheel feet traveled
    /**
     * @return wheel feet traveled
     */
    public double getFeetLeft() {
        return (getRotationsLeft() * wheelCircumference() / 12);
    }

    /**
     * @return wheel meters traveled
     */
    public double getMetersRight() {
        return Units.feetToMeters(getFeetRight());
    }

    /**
     * @return wheel feet right
     */
    public double getFeetRight() {
        return (getRotationsRight() * wheelCircumference() / 12);
    }

    /**
     * @return velocity from encoder for lL
     */
    public double getLeftVelocity() {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            return driver.leaderL.getEncoder().getVelocity();
        } else {

        }
        return 0;
    }

    /**
     * @return velocity from encoder for lR
     */
    public double getRightVelocity() {
        if (RobotToggles.DRIVE_USE_SPARKS) {
            return driver.leaderR.getEncoder().getVelocity();
        } else {

        }
        return 0;
    }

    public void updateGeneric() {
        robotTranslation = robotPose.getTranslation();
        robotRotation = robotPose.getRotation();
    }
}