package frc.shooter;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import frc.controllers.ButtonPanel;
import frc.controllers.JoystickController;
import frc.controllers.XBoxController;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;
import frc.util.Logger;
//import frc.util.Permalogger;
import frc.vision.GoalChameleon;
import frc.leds.ShooterLEDs;

public class Shooter{

    private boolean useFalcons = true;

    private CANSparkMax leader, follower;
    private TalonFX falconLeader, falconFollower;
    private CANPIDController speedo;
    private CANEncoder encoder;
    private XBoxController xbox;
    private boolean enabled = true; 
    private JoystickController joy;
    private ButtonPanel panel;

    private Timer timer = new Timer();
    private Logger logger = new Logger("shooter");
    //private Permalogger permalogger = new Permalogger("shooter");

    private double pulleyRatio = RobotNumbers.motorPulleySize/RobotNumbers.driverPulleySize;

    // private ShuffleboardTab tab = Shuffleboard.getTab("Shooter");
    // private NetworkTableEntry shooterSpeed = tab.add("Shooter Speed", 0).getEntry();
    // private NetworkTableEntry shooterToggle = tab.add("Shooter Toggle", false).getEntry();
    // private NetworkTableEntry manualSpeedOverride = tab.add("SPEED OVERRIDE", false).getEntry();
    // private NetworkTableEntry rampRate = tab.add("Ramp Rate", 40).getEntry();

    public String[] data = {
        "match time", "init time", 
        "speed", "target speed", 
        "motor temperature", "motor current", 
        "powered", 
        "P", "I", "D", 
        "rP", "rI", "rD",
        "distance"
    };
    public String[] units = {"seconds", "seconds", "rpm", "rpm", "C", "A", "T/F", "num", "num", "num", "num", "num", "num", "meters"};

    private double targetRPM;
    public double speed;
    private int ballsShot = 0;
    private boolean poweredState;
    public boolean atSpeed = false;
    private boolean spunUp = false;
    private boolean recoveryPID = false;

    // private NetworkTableEntry shooterP = tab.add("P", 0.00032).getEntry();
    // private NetworkTableEntry shooterI = tab.add("I", 0).getEntry();
    // private NetworkTableEntry shooterD = tab.add("D", 0).getEntry();
    // private NetworkTableEntry recP = tab.add("recP", 3e-4).getEntry();
    // private NetworkTableEntry recI = tab.add("recI", 1e-7).getEntry();
    // private NetworkTableEntry recD = tab.add("recD", 0.07).getEntry();
    // private NetworkTableEntry shooterF = tab.add("F", 0.000185).getEntry();

    // private NetworkTableEntry closeSpeedEntry = tab.add("close speed", 3000).getEntry();
    // private NetworkTableEntry farSpeedEntry = tab.add("far speed", 4000).getEntry();
    // private NetworkTableEntry farthestSpeedEntry = tab.add("farthest speed", 4700).getEntry();

    private double P, I, D, F, recoveryP, recoveryI, recoveryD;
    public double actualRPM;

    private GoalChameleon chameleon;
    private double lastSpeed;

    public boolean interpolationEnabled = false;

    public Shooter(){
        if(!useFalcons){
        leader = new CANSparkMax(RobotMap.shooterLeader, MotorType.kBrushless);
        follower = new CANSparkMax(RobotMap.shooterFollower, MotorType.kBrushless);
        if(RobotToggles.shooterPID){
            speedo = leader.getPIDController();
        }
        leader.setInverted(true);
        follower.follow(leader, true);
        }
        else{
            falconLeader = new TalonFX(RobotMap.shooterLeader);
            falconLeader.setInverted(TalonFXInvertType.Clockwise);
            falconFollower = new TalonFX(RobotMap.shooterFollower);
            falconFollower.setInverted(TalonFXInvertType.CounterClockwise);
            falconFollower.follow(falconLeader);
            falconLeader.setNeutralMode(NeutralMode.Coast);
            falconFollower.setNeutralMode(NeutralMode.Coast);
        }
        poweredState = false;
        chameleon = new GoalChameleon();
    }

    /**
     * Update the Shooter object.
     */
    public void update(){
        if(!useFalcons){
            actualRPM = leader.getEncoder().getVelocity();
        }
        else{
            actualRPM = falconLeader.getSelectedSensorVelocity(); //do math: 4096 units/rotation, units/100ms
        }
        checkState();
        //speed = shooterSpeed.getDouble(0);
        //put code here to set speed based on distance to goal
        boolean disabled = false;
        double closeDist = 3; //close zone low end distance
        // double closeSpeed = closeSpeedEntry.getDouble(3000); //close zone speed
        // double farDist = 4; //far zone low end distance
        // double farSpeed = farSpeedEntry.getDouble(4500); //far zone speed
        // double farthestDist = 6; //farthest zone low end distance
        // double farthestSpeed = farthestSpeedEntry.getDouble(4700); //farthest zone speed

        // if(chameleon.getGoalDistance()>closeDist && chameleon.getGoalDistance()<farDist){//close zone
        //     SmartDashboard.putString("ZONE", "close");
        //     speed = closeSpeed;
        // }
        // else if(chameleon.getGoalDistance()>farDist && chameleon.getGoalDistance()<farthestDist){ //far zone
        //     SmartDashboard.putString("ZONE", "far");
        //     speed = farSpeed;
        // }
        // else if(chameleon.getGoalDistance()>farthestDist){ //farthest zone
        //     SmartDashboard.putString("ZONE", "farthest");
        //     speed = farthestSpeed;
        // }
        // else{
        //     SmartDashboard.putString("ZONE", "screwed up?");
        // }

        // if(manualSpeedOverride.getBoolean(false)){
        //     speed = shooterSpeed.getDouble(0);
        // }

        //speed = 4040; //set in stone speed
        // if(!joy.getButton(1)){
        //     speed = interpolateSpeed();
        //     lastSpeed = speed;
        // }
        // else{
        //     speed = lastSpeed;
        // }
        if(!panel.getButton(13)){
            speed = 4200*((joy.getSlider()*0.25)+1); //4200
            disabled = false;
        }
        else{
            speed = 0;
            disabled = true;
        }

        if(!interpolationEnabled){
            speed = 4200;
        }




        // if(!joy.getButton(1)){
        //     speed = interpolateSpeed();
        // }

        //double rate = rampRate.getDouble(40);
        //boolean toggle = shooterToggle.getBoolean(false);

        // if(leader.getOpenLoopRampRate()!=rate){
        //     leader.setOpenLoopRampRate(rate);
        //     System.out.println("Ramp Rate Set to "+rate+", now "+leader.getOpenLoopRampRate());
        // }

        //3.00E-04	3.50E-07	0.02
        P = RobotNumbers.shooterSpinUpP; //shooterP.getDouble(0);
        I = RobotNumbers.shooterSpinUpI; //shooterI.getDouble(0);
        D = RobotNumbers.shooterSpinUpD; //shooterD.getDouble(0);
        // P = shooterP.getDouble(0.00032);
        // I = shooterI.getDouble(0);
        // D = shooterD.getDouble(0);
        P = 0.00035;
        I = 0;
        D = 0;
        F = 0.00019;//shooterF.getDouble(0.000185);
        

        //3.00E-04	1.00E-07	0.07 (tentative values, not perfect yet)
        recoveryP = 0.00037; //recP.getDouble(3e-4);
        recoveryI = 0; //recI.getDouble(1e-7);
        recoveryD = 0; //recD.getDouble(0.07);

        //setPID(P,I,D);

        // if(P!=Pold || I!=Iold || D!=Dold){
        //     setPID(P,I,D);
        //     System.out.println("PID reset");
        // }
        //if(enabled){
            //leader.set(0.05);
        //toggle(toggle);
        //enabled = true; //REMOVE IF RUNNING THE SHOOTER ALL THE TIME IS BAD
        //speed = 4150;
        //speed = 3000;
        if(!disabled){
            setSpeed(speed);
        }
        else{
            if(!useFalcons){
                leader.set(0);
            }
            else{
                falconLeader.set(ControlMode.PercentOutput, 0);
            }
        }
        // if(!panel.getButton(13)/*enabled||panel.getButton(13)*/){
        //     //poweredState = true;
        //     if(RobotToggles.shooterPID){
        //         setSpeed(speed);
        //     }
        //     else{
        //         leader.set(speed);
        //     }
        // }
        // else{
        //     //poweredState = false;
        //     if(RobotToggles.shooterPID){
        //         //do nothing because the voltage being set to 0 *should* coast it?
        //         //to past me: it does
        //         leader.set(speed);
        //     }
        //     else{
        //         leader.set(0);
        //     }
        // }

        SmartDashboard.putNumber("RPM", actualRPM);
        SmartDashboard.putNumber("Target RPM", speed);
        // SmartDashboard.putNumber("Drive Wheel RPM", actualRPM*pulleyRatio);
        // SmartDashboard.putNumber("Drive Wheel IPS", actualRPM*pulleyRatio*RobotNumbers.driverWheelDiameter*Math.PI);
        // SmartDashboard.putNumber("Motor Current", leader.getOutputCurrent());
        // SmartDashboard.putNumber("Motor Temp", leader.getMotorTemperature());
        // SmartDashboard.putNumber("I accumulator", speedo.getIAccum());
        // SmartDashboard.putBoolean("RecMode", recoveryPID);

        // SmartDashboard.putNumber("Target Size", chameleon.getGoalSize());
        // SmartDashboard.putNumber("Calculated Shooter Speed", interpolateSpeed());
        // SmartDashboard.putNumber("Battery Voltage", RobotController.getBatteryVoltage());
        // SmartDashboard.putNumber("Interpolated Speed", interpolateSpeed());

        
        
        // if(poweredState == true){
        //     leader.setVoltage(12);
        //     follower.setVoltage(12);
        // }
        // else{
        //     leader.setVoltage(0);
        //     follower.setVoltage(0);
        // }
        
        if(RobotToggles.logData){writeData();}
        //System.out.println(leader.getEncoder().getVelocity());
        SmartDashboard.putBoolean("atSpeed", atSpeed);
        SmartDashboard.putNumber("ballsShot", ballsShot);
        SmartDashboard.putBoolean("shooter enable", enabled);
    }

    public void spinUp(){
        setSpeed(speed);
    }

    /**
     * Get motor speed based 
     * @param distance
     * @return
     */
    private double getSpeedBasedOnDistance(double distance){
        //speed of motor = wheel speed/1.5
        //wheel speed = ips/4pi
        //therefore speed of motor = (ips/4pi)/1.5
        //velocity(m/s) = sqrt((49)/(sin(theta))^2)
        
        return 0;
    }

    public boolean validTarget(){
        return chameleon.validTarget();
    }

    public boolean atSpeed(){
        return leader.getEncoder().getVelocity()>speed-80;
    }

    private void checkState(){
        if(actualRPM >= speed-50){
            atSpeed = true;
            spunUp = true;
        }
        if(actualRPM < speed-30){
            atSpeed = false;
        }

        if(spunUp && actualRPM<speed-55){
            recoveryPID = true;
        }
        if(actualRPM<speed-1200){
            recoveryPID = false;
            spunUp = false;
        }

        if(recoveryPID){
            setPID(recoveryP, recoveryI, recoveryD, F);
            //setPID(P,I,D, F);
        }
        else{
            setPID(P,I,D, F);
        }
    }

    public boolean spunUp(){
        return spunUp;
    }
    public boolean recovering(){
        return recoveryPID;
    }

    /**
     * Set drive wheel RPM
     * @param rpm
     */
    public void setSpeed(double rpm){
        //System.out.println("setSpeed1");
        speedo.setReference(rpm, ControlType.kVelocity);
        //System.out.println("setSpeed2");
    }

    /**
     * Enable or disable the shooter being spun up.
     * @param toggle - spun up true or false
     */
    public void toggle(boolean toggle){
        enabled = toggle;
    }

    /**
     * Initialize the Shooter object.
     */
    public void init(){
        // shooterP.getDouble(0);
        // shooterI.getDouble(0);
        // shooterD.getDouble(0);

        leader.setSmartCurrentLimit(80);
        follower.setSmartCurrentLimit(80);
        leader.setIdleMode(IdleMode.kCoast);
        follower.setIdleMode(IdleMode.kCoast);

        leader.getEncoder().setPosition(0);
        leader.setOpenLoopRampRate(40);
        
        ballsShot = 0;

        //leader.setInverted(false);
        // follower.setInverted(true);

        speedo = leader.getPIDController();
        encoder = leader.getEncoder();
        //setPID(4e-5, 0, 0);
        speedo.setOutputRange(-1, 1);
        //setPID(1,0,0);

        speedo.setOutputRange(-1, 1);

        chameleon.init();
        SmartDashboard.putString("ZONE", "none");
        joy = new JoystickController(1);
        panel = new ButtonPanel(2);
    }

    /**
     * Set the P, I, and D values for the shooter.
     * @param P - P value
     * @param I - I value
     * @param D - D value
     */
    private void setPID(double P, double I, double D, double F){
        speedo.setP(P);
        speedo.setI(I);
        speedo.setD(D);
        speedo.setFF(F);
    }

    /**
     * Initialize the Shooter logger, run during autonomousInit.
     */
    public void initLogger(){
        System.out.println("attempting to initialize logger - Shooter");
        logger.init(data, units);
        timer.start();
        //permalogger.init();
    }

    /**
     * Close the Shooter logger, call during disabledInit().
     */
    public void closeLogger(){
        //permalogger.writeData(ballsShot);
        //permalogger.close();
        logger.close();
    }
    /**
     * Write shooter data to the log file.
     */
    private void writeData(){
        double powered;
        if(enabled){powered = 1;}else{powered = 0;}
        double[] data = {
            Timer.getMatchTime(), 
            timer.get(), 
            leader.getEncoder().getVelocity(), 
            speed, leader.getMotorTemperature(), 
            leader.getOutputCurrent(), 
            powered, 
            P, I, D, 
            recoveryP, recoveryI, recoveryD,
            chameleon.getGoalDistance()
        };
        logger.writeData(data);
    }

    private double[][] sizeSpeedsArray = {
        {0, 0},
        {45,4100},
        {55, 4150},
        {65, 4170},
        {75, 4150},
        {85, 4500},
    };

    private double speedMult = 1;
    private double interpolateSpeed(){
        double size = chameleon.getGoalSize();
        double finalMult = (joy.getSlider()*0.25)+1;
        int index = 0;
        for(int i = 0; i<sizeSpeedsArray.length ; i++){
            if(size>sizeSpeedsArray[i][0]){
                index = i;
            }
        }
        //now index is the index of the low end, index+1 = high end
        if(index+1>=sizeSpeedsArray.length){
            return sizeSpeedsArray[sizeSpeedsArray.length-1][1];
        }
        double sizeGap = sizeSpeedsArray[index][0]-sizeSpeedsArray[index+1][0];
        double gapFromLowEnd = size-sizeSpeedsArray[index][0];
        double portionOfGap = gapFromLowEnd/sizeGap;

        double speedGap = sizeSpeedsArray[index][1]-sizeSpeedsArray[index+1][1];
        double outSpeed = sizeSpeedsArray[index][1] + speedGap*portionOfGap; //low end + gap * portion
        SmartDashboard.putNumber("Interpolating Shooter Speed", outSpeed*speedMult*finalMult);
        return outSpeed*speedMult*finalMult;
    }
    
    private double[][] voltageFFArray = {
        {0, 0},
        {11, 190},
        {13, 185}
    };

    private double interpolateFF(){
        double voltage = RobotController.getBatteryVoltage();
        int index = 0;
        for(int i = 0; i<voltageFFArray.length ; i++){
            if(voltage>voltageFFArray[i][0]){
                index = i;
            }
        }
        //now index is the index of the low end, index+1 = high end
        if(index+1>=voltageFFArray.length){
            return voltageFFArray[sizeSpeedsArray.length-1][1];
        }
        double sizeGap = voltageFFArray[index][0]-voltageFFArray[index+1][0];
        double gapFromLowEnd = voltage-voltageFFArray[index][0];
        double portionOfGap = gapFromLowEnd/sizeGap;

        double speedGap = voltageFFArray[index][1]-voltageFFArray[index+1][1];
        double outSpeed = voltageFFArray[index][1] + speedGap*portionOfGap; //low end + gap * portion
        return 0;
    }
}
