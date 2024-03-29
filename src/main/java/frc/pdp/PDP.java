package frc.pdp;

import edu.wpi.first.wpilibj.RobotController;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.misc.UserInterface;
import frc.selfdiagnostics.BrownoutIssue;
import frc.selfdiagnostics.UndervoltageIssue;

/**
 * PDP (Power Distribution Panel) contains information about power, current, and voltage for the robot Is cosmetic for
 * now, but should become more useful in the future in diagnosing critical failures
 */
public class PDP implements ISubsystem {
    /*
    private static final NetworkTableEntry allEnergy = UserInterface.PDP_TOTAL_ENERGY_ON_THIS_BOOT.getEntry(),
            peakCurrent = UserInterface.PDP_PEAK_CURRENT.getEntry(),
    //otherEnergy = UserInterface.PDP_OTHER_ENERGY.getEntry(),
    peakPower = UserInterface.PDP_PEAK_POWER.getEntry();
*/
    //private final PowerDistributionPanel powerDistributionPanel;
    private final double peakCurrentVal = 0;
    private final double peakPowerVal = 0;

    public PDP(int channelID) {
        addToMetaList();
        //powerDistributionPanel = new PowerDistributionPanel(channelID);
    }

    @Override
    public void init() {

    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return RobotController.getBatteryVoltage() > 8 ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {
        updateGeneric();
    }

    @Override
    public void updateGeneric() {
        double BatteryMinVoltage = UserInterface.PDP_BROWNOUT_MIN_OVERRIDE.getEntry().getBoolean(false) ? UserInterface.PDP_BROWNOUT_MIN_VAL.getEntry().getDouble(7) : 7;
        //System.out.println("Read voltage: " + RobotController.getBatteryVoltage() + "V");
        BrownoutIssue.handleIssue(this, RobotController.getBatteryVoltage() < BatteryMinVoltage && RobotController.getBatteryVoltage() > 0);
        UndervoltageIssue.handleIssue(this, RobotController.getBatteryVoltage() >= BatteryMinVoltage && RobotController.getBatteryVoltage() <= (BatteryMinVoltage + 2));
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

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
        return "PDP";
    }
}
