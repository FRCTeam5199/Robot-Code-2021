package frc.pdp;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * PDP (Power Distribution Panel) contains information about power, current, and voltage for the robot Is cosmetic for
 * now, but should become more useful in the future in diagnosing critical failures
 */
public class PDP {
    /*
    private static final NetworkTableEntry allEnergy = UserInterface.PDP_TOTAL_ENERGY_ON_THIS_BOOT.getEntry(),
            peakCurrent = UserInterface.PDP_PEAK_CURRENT.getEntry(),
    //otherEnergy = UserInterface.PDP_OTHER_ENERGY.getEntry(),
    peakPower = UserInterface.PDP_PEAK_POWER.getEntry();
*/
    private final PowerDistributionPanel powerDistributionPanel;
    private final double peakCurrentVal = 0;
    private final double peakPowerVal = 0;

    public PDP(int channelID) {
        powerDistributionPanel = new PowerDistributionPanel(channelID);
    }

    /**
     * Run this constantly for updated stats and brownout protection
     *
     * @throws IllegalStateException on brownout, forces a disable
     */
    public void update() throws IllegalStateException {
        /*
        peakPowerVal = Math.max(peakPowerVal, powerDistributionPanel.getTotalCurrent() * powerDistributionPanel.getVoltage());
        peakPower.setDouble(peakPowerVal);
        allEnergy.setDouble(powerDistributionPanel.getTotalEnergy());
        peakCurrentVal = Math.max(peakCurrentVal, powerDistributionPanel.getTotalCurrent());
        peakCurrent.setDouble(peakCurrentVal);


        if (powerDistributionPanel.getVoltage() < 9.5) {
            System.err.println(">>>>>>>POSSIBLE BROWNOUT DETECTED<<<<<<<");
            //throw new IllegalStateException("The robot is browning out. Replace the battery or else I will call a programmer and they will be cranky. Plus I doubt the robot will drive anyways as a result of this message.");
        }
         */
    }
}
