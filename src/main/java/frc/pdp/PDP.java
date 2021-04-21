package frc.pdp;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotController;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
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
    private double peakCurrentVal = 0;
    private double peakPowerVal = 0;

    public PDP(int channelID) {
        addToMetaList();
        //powerDistributionPanel = new PowerDistributionPanel(channelID);
    }

    @Override
    public void init() {

    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return SubsystemStatus.FAILED;
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
        BrownoutIssue.handleIssue(this, RobotController.getBatteryVoltage() < 9 && RobotController.getBatteryVoltage() > 0);
        UndervoltageIssue.handleIssue(this, RobotController.getBatteryVoltage() >= 9 && RobotController.getBatteryVoltage() <= 9.5);
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
