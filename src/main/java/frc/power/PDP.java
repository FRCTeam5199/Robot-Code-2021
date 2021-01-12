package frc.power;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.util.Logger;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.RobotMap;
import frc.robot.RobotNumbers;
import frc.robot.RobotToggles;

import edu.wpi.first.wpilibj.RobotController;

//basically just used for logging
public class PDP{
    //private PowerDistributionPanel pdp = new PowerDistributionPanel();
    private Timer timer = new Timer();
    private Logger logger = new Logger("power");

    public String[] data = {"match time", "init time", "Battery Voltage", "Brounout State"};
    public String[] units = {"seconds", "seconds", "v", "state"};

    /**
     * Initialize the logger for the PDP, call during autonomousInit.
     */
    public void initLogger(){
        System.out.println("attempting to initialize logger - PDP");
        logger.init(data, units);
        timer.start();
    }

    /**
     * Initialize the PDP object.
     */
    public void init(){
        //empty lol
    }

    /**
     * Update the PDP object.
     */
    public void update(){
        double[] data = {Timer.getMatchTime(), timer.get(), RobotController.getBatteryVoltage(), brownout()};
        if(RobotToggles.logData){logger.writeData(data);}
    }

    private double brownout(){
        if(RobotController.isBrownedOut()){
            return 1;
        }
        return 0;
    }

    /**
     * Close the PDP logger, call during disabledInit.
     */
    public void closeLogger(){
        logger.close();
    }
}