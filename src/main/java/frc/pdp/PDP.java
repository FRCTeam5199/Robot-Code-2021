package frc.pdp;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * PDP (Power Distribution Panel) contains information about power, current, and voltage for the robot Is cosmetic for
 * now, but should become more useful in the future in diagnosing critical failures
 */
public class PDP {
    private final PowerDistributionPanel powerDistributionPanel;

    private static final ShuffleboardTab POWER_TAB = Shuffleboard.getTab("Lectricity");
    private static final NetworkTableEntry allEnergy = POWER_TAB.add("Total energy on this boot", 0).getEntry(),
            peakCurrent = POWER_TAB.add("Peak current", 0).getEntry(),
    //otherEnergy = POWER_TAB.add("Energy on current enable", 0).getEntry(),
    peakPower = POWER_TAB.add("Peak power", 0).getEntry();

    private double peakCurrentVal = 0, peakPowerVal = 0;

    public PDP(int channelID) {
        powerDistributionPanel = new PowerDistributionPanel(channelID);
    }

    /**
     * Run this constantly for updated stats and brownout protection
     *
     * @throws IllegalStateException on brownout, forces a disable
     */
    public void update() throws IllegalStateException {
        peakPowerVal = Math.max(peakPowerVal, powerDistributionPanel.getTotalCurrent() * powerDistributionPanel.getVoltage());
        peakPower.setDouble(peakPowerVal);
        allEnergy.setDouble(powerDistributionPanel.getTotalEnergy());
        peakCurrentVal = Math.max(peakCurrentVal, powerDistributionPanel.getTotalCurrent());
        peakCurrent.setDouble(peakCurrentVal);

        if (powerDistributionPanel.getVoltage() < 9.5) {
            System.err.println(">>>>>>>POSSIBLE BROWNOUT DETECTED<<<<<<<");
            //throw new IllegalStateException("The robot is browning out. Replace the battery or else I will call a programmer and they will be cranky. Plus I doubt the robot will drive anyways as a result of this message.");
        }
    }
}
