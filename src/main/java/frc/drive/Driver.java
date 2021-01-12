package frc.drive;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;

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

import frc.util.Logger;
//import frc.util.Permalogger;

import frc.vision.BallChameleon;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.networktables.*;

import java.lang.Math;

public class Driver{
    private PigeonIMU pigeon = new PigeonIMU(RobotMap.pigeon);
    private Logger logger = new Logger("drive");
    private Logger posLogger = new Logger("positions");
    //private Permalogger odo = new Permalogger("distance");
    //wheelbase 27"
    private DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(Units.inchesToMeters(22));
    DifferentialDriveOdometry odometer;
    private BallChameleon chameleon = new BallChameleon();

    private XBoxController controller;
    private CANSparkMax leaderL, followerL1, followerL2;
    private CANSparkMax leaderR, followerR1, followerR2;

    private CANPIDController leftPID;
    private CANPIDController rightPID;

    //private double targetHeading;

    public double[] ypr = new double[3];
    public double[] startypr = new double[3];

    public double currentOmega;

    //private boolean chaseBall;
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

    private ShuffleboardTab tab2 = Shuffleboard.getTab("drive");
    // private NetworkTableEntry driveP = tab2.add("P", RobotNumbers.drivebaseP).getEntry();
    // private NetworkTableEntry driveI = tab2.add("I", RobotNumbers.drivebaseI).getEntry();
    // private NetworkTableEntry driveD = tab2.add("D", RobotNumbers.drivebaseD).getEntry();
    // private NetworkTableEntry driveF = tab2.add("F", RobotNumbers.drivebaseF).getEntry();
    private NetworkTableEntry driveRotMult = tab2.add("Rotation Factor", RobotNumbers.turnScale).getEntry();
    //private DoubleSolenoid solenoidShifterL, solenoidShifterR;
    private Solenoid shifter; 

    public Driver(){
        
        //headControl = new PIDController(Kp, Ki, Kd);
    }

    /**
     * Initialize the Driver object.
     */
    public void init(){
        controller = new XBoxController(0);
        leaderL = new CANSparkMax(RobotMap.driveLeaderL, MotorType.kBrushless);
        leaderR = new CANSparkMax(RobotMap.driveLeaderR, MotorType.kBrushless);
        followerL1 = new CANSparkMax(RobotMap.driveFollowerL1, MotorType.kBrushless);
        followerR1 = new CANSparkMax(RobotMap.driveFollowerR1, MotorType.kBrushless);
        followerL2 = new CANSparkMax(RobotMap.driveFollowerL2, MotorType.kBrushless);
        followerR2 = new CANSparkMax(RobotMap.driveFollowerR2, MotorType.kBrushless);
        leftPID = leaderL.getPIDController();
        rightPID = leaderR.getPIDController();
        shifter = new Solenoid(RobotMap.pcm, RobotMap.shifters);
        chameleon.init();
        followerL1.follow(leaderL);
        followerR1.follow(leaderR);
        followerL2.follow(leaderL);
        followerR2.follow(leaderR);
        leaderL.setInverted(true);
        leaderR.setInverted(false);
        resetPigeon();
        updatePigeon();
        setPID(RobotNumbers.drivebaseP, RobotNumbers.drivebaseI, RobotNumbers.drivebaseD, RobotNumbers.drivebaseF);
        autoStage = 0;
        autoComplete = false;
        setLowGear(false);
        //setupPathfinderAuto();
        setPID(0,0,0.000005,0.00002);
        //setPID(0.1e-5,0,30e-4,0.00001);
    }

    public void setCurrentLimits(double limit){
        leaderL.setSmartCurrentLimit((int)limit);
        followerL1.setSmartCurrentLimit((int)limit);
        followerL2.setSmartCurrentLimit((int)limit);
        leaderR.setSmartCurrentLimit((int)limit);
        followerR1.setSmartCurrentLimit((int)limit);
        followerR2.setSmartCurrentLimit((int)limit);
    }

    /**
     * Meant to be run during all periodic modes except robotPeriodic().
     */
    public void updateGeneric(){
        robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(yawAbs())), getMetersLeft(), getMetersRight());
        robotTranslation = robotPose.getTranslation();
        robotRotation = robotPose.getRotation();
        double[] dataElements = {robotTranslation.getX(), robotTranslation.getY(), Logger.boolToDouble(controller.getButtonDown(5))};
        logger.writeData(dataElements);
        feetDriven = (getFeetLeft()+getFeetRight())/2;
        //setPID(driveP.getDouble(RobotNumbers.drivebaseP), driveI.getDouble(RobotNumbers.drivebaseI), driveD.getDouble(RobotNumbers.drivebaseD), driveF.getDouble(RobotNumbers.drivebaseF));
        //setPID(0,0,0.000005,0.000001);
        SmartDashboard.putNumber("Left Speed", leaderL.getEncoder().getVelocity());
    }

    /**
     * Update the Driver object(for teleop mode).
     */
    public void updateTeleop(){
        updateGeneric();
        invert = false;//controller.getButton(6);
        SmartDashboard.putBoolean("invert", invert);
        setLowGear(controller.getButton(5));
        //drive(0.5,1);
        double turn = -controller.getStickRX();
        double drive;
        if(invert){
            drive = -controller.getStickLY();
        }
        else{
            drive = controller.getStickLY();
        }
        pointBall = controller.getButton(6); //DISABLED BECAUSE WE YOINKED THE LL
        //chaseBall = false;//controller.getRTriggerPressed(); //ALSO DISABLED BECAUSE WE YOINKED THE LL

        //!!!!!
        //if statement for ball tracking should add an omega offset proportional to the ball's left/rightness in the limelight
        if(pointBall){
            double angleOffset = -chameleon.getBallAngle();
            //double driveOffset = chameleon.getBallSize();
            //System.out.println("attempting to aim");
            if(Math.abs(angleOffset)>RobotNumbers.llTolerance){
                //System.out.println("attempting to drive");
                turn += angleOffset/70; //pulled number out of nowhere, bigger value makes the limelight have a smaller effect
            }
            // if(chaseBall){
            //     drive += 70/driveOffset;
            // }
            //System.out.println("turn: "+turn);
        }
        
        //drivePID((controller.getStickLY()*(1)) + turnSpeed, (controller.getStickLY()*(1)) - turnSpeed);
        drive(drive, turn);
        //drivePure(adjustedDrive(controller.getStickLY()), adjustedRotation(controller.getStickRX()));
        
    }

    public void updateTest(){
        updateGeneric();
        //log position on left bumper(?) presses(useful for getting auton points)
        double[] dataElements = {robotTranslation.getX(), robotTranslation.getY(), Logger.boolToDouble(controller.getButtonDown(5))};
        if(controller.getButtonDown(5)){posLogger.writeData(dataElements);}

        invert = false;//controller.getButton(6);
        SmartDashboard.putBoolean("invert", invert);
        //drive(0.5,1);
        double turn = -controller.getStickRX();
        double drive;
        if(invert){
            drive = -controller.getStickLY();
        }
        else{
            drive = controller.getStickLY();
        }
        pointBall = controller.getButton(6); //DISABLED BECAUSE WE YOINKED THE LL
        //chaseBall = false;//controller.getRTriggerPressed(); //ALSO DISABLED BECAUSE WE YOINKED THE LL

        //!!!!!
        //if statement for ball tracking should add an omega offset proportional to the ball's left/rightness in the limelight
        if(pointBall){
            double angleOffset = -chameleon.getBallAngle();
            //double driveOffset = chameleon.getBallSize();
            //System.out.println("attempting to aim");
            if(Math.abs(angleOffset)>RobotNumbers.llTolerance){
                //System.out.println("attempting to drive");
                turn += angleOffset/70; //pulled number out of nowhere, bigger value makes the limelight have a smaller effect
            }
            // if(chaseBall){
            //     drive += 70/driveOffset;
            // }
            //System.out.println("turn: "+turn);
        }

        leaderL.set(0);
        leaderR.set(0);
        System.out.println("X: "+ -robotTranslation.getY()+"  Y: "+robotTranslation.getX());
        
        //drivePID((controller.getStickLY()*(1)) + turnSpeed, (controller.getStickLY()*(1)) - turnSpeed);
        //drive(drive, turn);
        //drivePure(adjustedDrive(controller.getStickLY()), adjustedRotation(controller.getStickRX()));
    }

    

    /**
     * Drive each side based on inputs -1 to 1.
     */
    private void drive(double forward, double rotation){ 
        drivePure(adjustedDrive(forward), adjustedRotation(rotation));
    }

    /**
     * Drive each side based on a -1 to 1 scale but with PID
     */
    public void drivePID(double left, double right){ 
        leftPID.setReference(left*RobotNumbers.maxMotorSpeed, ControlType.kVelocity);
        rightPID.setReference(right*RobotNumbers.maxMotorSpeed, ControlType.kVelocity);
    }

    /**
     * Drive based on FPS and omega(speed of rotation in rad/sec)
     */
    private void drivePure(double FPS, double omega){
        omega *= driveRotMult.getDouble(RobotNumbers.turnScale);
        currentOmega = omega;
        var chassisSpeeds = new ChassisSpeeds(Units.feetToMeters(FPS), 0, omega);
        DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);
        double leftVelocity = Units.metersToFeet(wheelSpeeds.leftMetersPerSecond);
        double rightVelocity = Units.metersToFeet(wheelSpeeds.rightMetersPerSecond);
        double mult = 3.8*2.16*RobotNumbers.driveScale;
        //System.out.println("FPS: "+leftVelocity+"  "+rightVelocity+" RPM: "+convertFPStoRPM(leftVelocity)+" "+convertFPStoRPM(rightVelocity));
        leftPID.setReference(convertFPStoRPM(leftVelocity)*mult, ControlType.kVelocity);
        rightPID.setReference(convertFPStoRPM(rightVelocity)*mult, ControlType.kVelocity);
        //System.out.println(leaderL.getEncoder().getVelocity()+" "+leaderR.getEncoder().getVelocity());
    }
    
    /**
     * Set the gear of the transmissions.
     * @param shifted whether or not the transmissions are to be shifted to low gear
     */
    public void setLowGear(boolean shifted){
        shifter.set(shifted);
    }

    /**
     * Set P, I, and D values for the drivetrain.
     */
    private void setPID(double P, double I, double D, double F){
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

    private double adjustedDrive(double input){
        return input*RobotNumbers.maxSpeed;
    }

    private double adjustedRotation(double input){
        return input*RobotNumbers.maxRotation;
    }
    
    private double convertFPStoRPM(double FPS){
        return FPS*(RobotNumbers.maxMotorSpeed/RobotNumbers.maxSpeed);
    }

    /**
     * Get the current Omega value.
     * @return speed of rotation in rad/sec
     */
    public double omega(){
        double[] gyro = new double[3];
        pigeon.getRawGyro(gyro);
        double omegaOut = Units.degreesToRadians(gyro[0]);
        return omegaOut;
    }
    
    //pigeon code ------------------------------------------------------------------------------------------------------------------
    private double startYaw;
    public void updatePigeon(){
        pigeon.getYawPitchRoll(ypr);
    }
    public void resetPigeon(){
        updatePigeon();
        startypr = ypr;
        startYaw = yawAbs();
    }
    //absolute ypr -----------------------------------------------------------------------------------------------------------------
    public double yawAbs(){ //return absolute yaw of pigeon
        updatePigeon();
        return ypr[0];
    }
    public double pitchAbs(){ //return absolute pitch of pigeon
        updatePigeon();
        return ypr[1];
    }
    public double rollAbs(){ //return absolute roll of pigeon
        updatePigeon();
        return ypr[2];
    }
    //relative ypr ----------------------------------------------------------------------------------------------------------------
    public double yawRel(){ //return relative(to start) yaw of pigeon
        updatePigeon();
        return (ypr[0]-startYaw);
    }
    public double pitchRel(){ //return relative pitch of pigeon
        updatePigeon();
        return ypr[1]-startypr[1];
    }
    public double rollRel(){ //return relative roll of pigeon
        updatePigeon();
        return ypr[2]-startypr[2];
    }
    public double adjustedYaw(){
        return 90-yawRel();
    }
    public double yawWraparound(){
        double yaw = adjustedYaw();
        while(!(360>=yaw || yaw<0)){
            if(yaw>=360){
                yaw -= 360;
            }
            else if(yaw<0){
                yaw += 360;
            }
        }
        return yaw;
    }
    /**
     * get the heading from -180 to 180, negative CCW
     * @return heading based on the front of the bot at start
     */
    public double yawWraparoundAhead(){
        double yaw = yawRel();
        while(180<yaw || yaw<-180){
            if(yaw>180){
                yaw -= 360;
            }
            else if(yaw<-180){
                yaw += 360;
            }
        }
        return yaw;
    }

    //position conversion -------------------------------------------------------------------------------------------------------
    private double wheelCircumference(){
        return RobotNumbers.wheelDiameter*Math.PI;
    }

    //getRotations - get wheel rotations on encoder
    public double getRotationsLeft(){
        return (leaderL.getEncoder().getPosition())/9;
    }
    public double getRotationsRight(){
        return (leaderR.getEncoder().getPosition())/9;
    }

    //getRPM - get wheel RPM from encoder
    public double getRPMLeft(){
        return (leaderL.getEncoder().getVelocity())/9;
    }
    public double getRPMRight(){
        return (leaderR.getEncoder().getVelocity())/9;
    }

    //getIPS - get wheel IPS from encoder
    public double getIPSLeft(){
        return (getRPMLeft()*wheelCircumference())/60;
    }
    public double getIPSRight(){
        return (getRPMRight()*wheelCircumference())/60;
    }

    //getFPS - get wheel FPS from encoder
    public double getFPSLeft(){
        return getIPSLeft()/12;
    }
    public double getFPSRight(){
        return getIPSRight()/12;
    }

    //getInches - get wheel inches traveled
    public double getInchesLeft(){
        return (getRotationsLeft()*wheelCircumference());
    }
    public double getInchesRight(){
        return (getRotationsRight()*wheelCircumference());
    }

    //getFeet - get wheel feet traveled
    public double getFeetLeft(){
        return (getRotationsLeft()*wheelCircumference()/12);
    }
    public double getFeetRight(){
        return (getRotationsRight()*wheelCircumference()/12);
    }

    //getMeters - get wheel meters traveled
    public double getMetersLeft(){
        return Units.feetToMeters(getFeetLeft());
    }
    public double getMetersRight(){
        return Units.feetToMeters(getFeetRight());
    }

    //auto ----------------------------------------------------------------------------------------------------------------------    
    private PIDController headingPID;
    //private int arrayAutoStage;
    private ShuffleboardTab tab = Shuffleboard.getTab("auto");
    private NetworkTableEntry x = tab.add("x position", 0).getEntry();
    private NetworkTableEntry y = tab.add("y position", 0).getEntry();

    public boolean setup = false;
    /**
     * set stuff up for auto
     */
    public void setupAuto(){
        if(!setup){
            headingPID = new PIDController(RobotNumbers.headingP, RobotNumbers.headingI, RobotNumbers.headingD);
            odometer = new DifferentialDriveOdometry(Rotation2d.fromDegrees(yawAbs()), new Pose2d(0, 0, new Rotation2d()));
            initLogger();
            resetPigeon();
            leaderL.getEncoder().setPosition(0);
            leaderR.getEncoder().setPosition(0);
            //headingPID.enableContinuousInput(0, 360);
            //arrayAutoStage = 0;
            setup = true;
        }
    }

    private void put(String name, double num){
        SmartDashboard.putNumber(name, num);
    }
    /**
     * @return the robot's X position in relation to its starting position(right positive) 
     * typically facing away from opposing alliance station
     */
    private double fieldX(){
        return -robotTranslation.getY();
    }
    /**
     * @return the robot's Y position in relation to its starting position(away positive) 
     * typically facing away from opposing alliance station
     */
    private double fieldY(){
        return robotTranslation.getX();
    }
    /**
     * get the field heading between -180 and 180, negative CCW
     * @return
     */
    private double fieldHeading(){
        return -yawWraparoundAhead();
    }
    /**
     * get the angle between the bot's current field position and the waypoint coordinates
     * @param wayX - waypoint X
     * @param wayY - waypoint Y
     * @return the angle to the waypoint in the same format as fieldHeading
     */
    private double angleToPos(double wayX, double wayY){
        double xDiff = wayX-fieldX();
        double yDiff = wayY-fieldY();
        return Math.toDegrees(Math.atan2(xDiff, yDiff));
    }
    /** 
     * @return error between the bot's current heading and the direction towards a waypoint
     */
    private double headingError(double wayX, double wayY){
        return angleToPos(wayX, wayY)-fieldHeading();
    }
    private double headingErrorWraparound(double wayX, double wayY){
        double error = headingError(wayX, wayY);
        if(error>180){
            return error-360;
        }
        else if(error<-180){
            return error+360;
        }
        return error;
    }

    /**
     * "Attack"(drive towards) a point on the field. Units are in meters and its scary.
     * @param targetX - x position of the waypoint in meters
     * @param targetY - y position of the waypoint in meters
     * @return Boolean representing whether the robot is within tolerance of the waypoint or not.
     */
    public boolean headTowardsPoint(double targetX, double targetY, double speed){
        double xDiff = targetX-fieldX();
        double yDiff = targetY-fieldY();
        //logic: use PID to drive in such a way that the robot's heading is adjusted towards the target as it moves forward
        //wait is this just pure pursuit made by an idiot?
        double rotationOffset = headingPID.calculate(headingErrorWraparound(targetX, targetY), 0);
        boolean xInTolerance = Math.abs(xDiff) < RobotNumbers.autoTolerance;
        boolean yInTolerance = Math.abs(yDiff) < RobotNumbers.autoTolerance;
        boolean inTolerance = yInTolerance && xInTolerance;
        if(!inTolerance){
            drive(0, rotationOffset*RobotNumbers.autoRotationMultiplier);
            // leaderL.set(0);
            // leaderR.set(0);
        }
        else{
            drive(0,0);
            // leaderL.set(0);
            // leaderR.set(0);
        }
        return inTolerance;
    }

    /**
     * "Attack"(drive towards) a point on the field. Units are in meters and its scary.
     * @param targetX - x position of the waypoint in meters
     * @param targetY - y position of the waypoint in meters
     * @return Boolean representing whether the robot is within tolerance of the waypoint or not.
     */
    public boolean attackPoint(double targetX, double targetY, double speed){
        double xDiff = targetX-fieldX();
        double yDiff = targetY-fieldY();
        //logic: use PID to drive in such a way that the robot's heading is adjusted towards the target as it moves forward
        //wait is this just pure pursuit made by an idiot?
        double rotationOffset = headingPID.calculate(headingErrorWraparound(targetX, targetY), 0);
        boolean xInTolerance = Math.abs(xDiff) < RobotNumbers.autoTolerance;
        boolean yInTolerance = Math.abs(yDiff) < RobotNumbers.autoTolerance;
        boolean inTolerance = yInTolerance && xInTolerance;
        if(!inTolerance){
            drive(RobotNumbers.autoSpeed*speed, rotationOffset*RobotNumbers.autoRotationMultiplier);
            // leaderL.set(0);
            // leaderR.set(0);
        }
        else{
            drive(0,0);
            // leaderL.set(0);
            // leaderR.set(0);
        }
        // put("x", fieldX());
        // put("y", fieldY());
        // put("head", fieldHeading());
        // put("angleTo", angleToPos(x.getDouble(0), y.getDouble(0)));
        // // put("error", headingError(x.getDouble(0), y.getDouble(0)));
        // put("wrap", headingErrorWraparound(x.getDouble(0), y.getDouble(0)));
        // put("rotationOffset", rotationOffset);
        // SmartDashboard.putNumber("xDiff", xDiff);
        // SmartDashboard.putNumber("yDiff", yDiff);
        // SmartDashboard.putNumber("xpos", robotTranslation.getY());
        // SmartDashboard.putNumber("ypos", -robotTranslation.getX());
        // SmartDashboard.putNumber("angleTarget", angleTarget);
        // SmartDashboard.putNumber("heading", yawWraparound());
        // SmartDashboard.putNumber("abs", yawAbs());
        // SmartDashboard.putNumber("rotationOffset", -rotationOffset*RobotNumbers.autoRotationMultiplier); //number being fed into drive()
        // SmartDashboard.putNumber("rotationDifference", -(angleTarget-yawWraparound()));
        // SmartDashboard.putBoolean("inTolerance", inTolerance);
        // SmartDashboard.putNumber("left", getMetersLeft());
        // SmartDashboard.putNumber("right", getMetersRight());
        return inTolerance;
    }

    /**
     * "Attack"(drive towards) a point on the field but in reverse. Units are in meters. 
     * Gotta drive backwards for that sweet sweet 10 ball auto.
     * Why rebuild the entire auto to run backwards when you can just drive backwards and point away from the target?
     * @param targetX - x position of the waypoint in meters
     * @param targetY - y position of the waypoint in meters
     * @return Boolean representing whether the robot is within tolerance of the waypoint or not.
     */
    public boolean attackPointReverse(double targetX, double targetY, double speed){
        double xDiff = targetX-fieldX();
        double yDiff = targetY-fieldY();
        //logic: use PID to drive in such a way that the robot's heading is adjusted towards the target as it moves forward
        //wait is this just pure pursuit made by an idiot?
        double rotationOffset = headingPID.calculate(headingErrorWraparound(-targetX+2*fieldX(), -targetY+2*fieldY()), 0);
        boolean xInTolerance = Math.abs(xDiff) < RobotNumbers.autoTolerance;
        boolean yInTolerance = Math.abs(yDiff) < RobotNumbers.autoTolerance;
        boolean inTolerance = yInTolerance && xInTolerance;
        if(!inTolerance){
            drive(-RobotNumbers.autoSpeed*speed, rotationOffset*RobotNumbers.autoRotationMultiplier);
            // leaderL.set(0);
            // leaderR.set(0);
        }
        else{
            drive(0,0);
            // leaderL.set(0);
            // leaderR.set(0);
        }
        // put("x", fieldX());
        // put("y", fieldY());
        // put("head", fieldHeading());
        // put("angleTo", angleToPos(x.getDouble(0), y.getDouble(0)));
        // // put("error", headingError(x.getDouble(0), y.getDouble(0)));
        // put("wrap", headingErrorWraparound(x.getDouble(0), y.getDouble(0)));
        // put("rotationOffset", rotationOffset);
        // SmartDashboard.putNumber("xDiff", xDiff);
        // SmartDashboard.putNumber("yDiff", yDiff);
        // SmartDashboard.putNumber("xpos", robotTranslation.getY());
        // SmartDashboard.putNumber("ypos", -robotTranslation.getX());
        // SmartDashboard.putNumber("angleTarget", angleTarget);
        // SmartDashboard.putNumber("heading", yawWraparound());
        // SmartDashboard.putNumber("abs", yawAbs());
        // SmartDashboard.putNumber("rotationOffset", -rotationOffset*RobotNumbers.autoRotationMultiplier); //number being fed into drive()
        // SmartDashboard.putNumber("rotationDifference", -(angleTarget-yawWraparound()));
        SmartDashboard.putBoolean("inTolerance", inTolerance);
        // SmartDashboard.putNumber("left", getMetersLeft());
        // SmartDashboard.putNumber("right", getMetersRight());
        return inTolerance;
    }
    


    /**
     * arc follower code(bad, don't use)
     */
    public boolean driveSidesToPos(double leftFeet, double rightFeet){
        double leftSpeed, rightSpeed;
        double reverser = RobotNumbers.autoSpeedMultiplier;
        double leftPos = (leaderL.getEncoder().getPosition()-relLeft)/6.8*(RobotNumbers.wheelDiameter*Math.PI)/12; //motor rots > feet: encoder/(geardown)*(diameter*pi)/12
        double rightPos = (leaderR.getEncoder().getPosition()-relRight)/6.8*(RobotNumbers.wheelDiameter*Math.PI)/12;
        SmartDashboard.putNumber("leftPos", leftPos);
        SmartDashboard.putNumber("rightPos", rightPos);
        if(leftFeet<0 || rightFeet<0){
            reverser = -1*RobotNumbers.autoSpeedMultiplier;
        }

        if(leftFeet>rightFeet){
            leftSpeed = reverser;
            rightSpeed = rightFeet/leftFeet*reverser;
        }
        else if(rightFeet>leftFeet){
            rightSpeed = reverser;
            leftSpeed = leftFeet/rightFeet*reverser;
        }
        else{ //distances are equal
            rightSpeed = reverser;
            leftSpeed = reverser;
        }
        SmartDashboard.putNumber("lspeed", rightSpeed);
        SmartDashboard.putNumber("rspeed", leftSpeed);
        if(reverser>0){ //if not driving in reverse
            if(leftPos<leftFeet||rightPos<rightFeet){
                drivePID(leftSpeed, rightSpeed);
            }
            else{
                drivePID(0, 0);
                return true;
            }
        }
        else if(reverser<0){ //if driving in reverse
            if(leftPos>leftFeet||rightPos>rightFeet){
                drivePID(leftSpeed, rightSpeed);
            }
            else{
                drivePID(0, 0);
                return true;
            }
        }
        return false;
    }

    /**
     * only used for the garbage drive arc code
     */
    private void setRelativePositions(){
        relLeft = leaderL.getEncoder().getPosition();
        relRight = leaderR.getEncoder().getPosition();
    }

    /**
     * used literally nowhere, resets auto stage to 0 and completion to false but 
     * like, auto can just kinda chill at the end with my new array stuff?
     */
    public void resetAuton(){
        autoStage = 0;
        autoComplete = false;
    }
    /**
     * shouldn't be needed anymore
     */
    public void updateAuto1(){
        robotPose = odometer.update(new Rotation2d(Units.degreesToRadians(yawAbs())), getMetersLeft(), getMetersRight());
        robotTranslation = robotPose.getTranslation();
        robotRotation = robotPose.getRotation();
        double[] dataElements = {robotTranslation.getX(), robotTranslation.getY(), 0};
        logger.writeData(dataElements);
        switch(autoStage){
            case(0):
                System.out.println("Stage 0");
                if(attackPoint(1, 2, 1)){
                    setRelativePositions();
                    autoStage++;
                }
                break;
            case(1):
                System.out.println("Stage 1");
                if(attackPoint(0, 4, 1)){
                    setRelativePositions();
                    autoStage++;
                }
                break;
            case(2):
                System.out.println("Stage 2");
                if(attackPoint(0 ,5, 1)){
                    setRelativePositions();
                    autoStage++;
                }
                break;
            default:
                autoComplete = true;
                break;    
        }
    }

    // private SpeedController m_left_motor;
    // private SpeedController m_right_motor;

    // private Encoder m_left_encoder;
    // private Encoder m_right_encoder;

    // private AnalogGyro m_gyro;

    // private EncoderFollower m_left_follower;
    // private EncoderFollower m_right_follower;

    // private Notifier m_follower_notifier;

    // private static int k_ticks_per_rev = 2048;
    // private static double k_wheel_diameter = 6.0 / 12.0;
    // private static double k_max_velocity = 8;

    // private static int k_left_channel = 0;
    // private static int k_right_channel = 1;

    // private static int k_left_encoder_port_a = 0;
    // private static int k_left_encoder_port_b = 1;
    // private static int k_right_encoder_port_a = 2;
    // private static int k_right_encoder_port_b = 3;

    // private static int k_gyro_port = 0;

    // private static String k_path_name = "RunTowardsTrench";

    // /**
    //  * run during robot init
    //  */
    // public void setupPathfinderAuto(){
    //     m_left_encoder = new Encoder(k_left_encoder_port_a, k_left_encoder_port_b);
    //     m_right_encoder = new Encoder(k_right_encoder_port_a, k_right_encoder_port_b);
    // }
    // /**
    //  * run during auton init
    //  */
    // public void initPathfinderAuto(){
    //     try {
    //         Trajectory left_trajectory = PathfinderFRC.getTrajectory(k_path_name + ".left");
    //         Trajectory right_trajectory = PathfinderFRC.getTrajectory(k_path_name + ".right");
        
    //         m_left_follower = new EncoderFollower(left_trajectory);
    //         m_right_follower = new EncoderFollower(right_trajectory);
        
    //         m_left_follower.configureEncoder(m_left_encoder.get(), k_ticks_per_rev, k_wheel_diameter);
    //         // You must tune the PID values on the following line!
    //         m_left_follower.configurePIDVA(8.0, 0.0, 0.0, 1 / k_max_velocity, 0);
        
    //         m_right_follower.configureEncoder(m_right_encoder.get(), k_ticks_per_rev, k_wheel_diameter);
    //         // You must tune the PID values on the following line!
    //         m_right_follower.configurePIDVA(8.0, 0.0, 0.0, 1 / k_max_velocity, 0);
        
    //         m_follower_notifier = new Notifier(this::followPath);
    //         m_follower_notifier.startPeriodic(left_trajectory.get(0).dt);
    //       } catch (IOException e) {
    //         e.printStackTrace();
    //       }
    // }

    // private void followPath() {
    //     if (m_left_follower.isFinished() || m_right_follower.isFinished()) {
    //       m_follower_notifier.stop();
    //     } else {
    //       double left_speed = m_left_follower.calculate(m_left_encoder.get());
    //       double right_speed = m_right_follower.calculate(m_right_encoder.get());
    //       double heading = yawRel();
    //       double desired_heading = Pathfinder.r2d(m_left_follower.getHeading());
    //       double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);
    //       double turn =  0.8 * (-1.0/80.0) * heading_difference;
    //       leaderL.set(left_speed + turn);
    //       leaderR.set(right_speed - turn);
    //     }
    // }

    // /**
    //  * stop, and deactivate ~~robots~~ motors
    //  */
    // public void stopMotors(){
    //     m_follower_notifier.stop();
    //     leaderL.set(0);
    //     leaderR.set(0);
    // }

    /**
     * initialize special logger for logging position when the left bumper is pressed in test mode
     */
    public void initPoseLogger(){
        String[] dataFields = {"X", "Y", "Flag"};
        String[] units = {"Meters", "Meters", ""};
        posLogger.init(dataFields, units);
    }

    public void initLogger(){
        String[] dataFields = {"X", "Y", "Flag"};
        String[] units = {"Meters", "Meters", ""};
        logger.init(dataFields, units);
        //odo.init();
    }

    public void setBrake(boolean brake){
        if(brake){
            leaderL.setIdleMode(IdleMode.kBrake);
            leaderR.setIdleMode(IdleMode.kBrake);
            followerL1.setIdleMode(IdleMode.kBrake);
            followerL2.setIdleMode(IdleMode.kBrake);
            followerR1.setIdleMode(IdleMode.kBrake);
            followerR2.setIdleMode(IdleMode.kBrake);
        }
        else{
            leaderL.setIdleMode(IdleMode.kCoast);
            leaderR.setIdleMode(IdleMode.kCoast);
            followerL1.setIdleMode(IdleMode.kCoast);
            followerL2.setIdleMode(IdleMode.kCoast);
            followerR1.setIdleMode(IdleMode.kCoast);
            followerR2.setIdleMode(IdleMode.kCoast);
        }
    }

    public void unlockWheels(){
        leaderR.set(0);
        leaderL.set(0);
        setBrake(false);
    }
    
    /**
     * close all loggers
     */
    public void closeLogger(){
        double[] odoData = {feetDriven};
        //odo.writeData(odoData);
        //odo.close();
        logger.close();
        posLogger.close();
    }
}