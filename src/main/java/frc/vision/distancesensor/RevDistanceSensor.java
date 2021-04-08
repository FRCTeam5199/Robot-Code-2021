package frc.vision.distancesensor;

import com.revrobotics.Rev2mDistanceSensor;
import frc.selfdiagnostics.DistanceSensorNonOpIssue;

/**
 * A distance sensor because the builders are too insecure to use physical switches that go unresponsive less often,
 * have a negligable false-positive and false-negative rate and are less frustrating and less suceptable to things like
 * blemishes in otherwise flawless craftsmanship
 */
public class RevDistanceSensor extends Rev2mDistanceSensor implements IDistanceSensor {
    public RevDistanceSensor(Rev2mDistanceSensor.Port port) {
        super(port);
        init();
    }

    @Override
    public void init() {
        addToMetaList();
        setEnabled(true);
        setAutomaticMode(true);
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    /**
     * Since the sensor should be auto updating, we only need to assert that we are getting data and report it
     * accordingly
     *
     * @see DistanceSensorNonOpIssue
     */
    @Override
    public void updateGeneric() {
        if (getRange() <= 0)
            DistanceSensorNonOpIssue.reportIssue(this, getSubsystemName());
        else
            DistanceSensorNonOpIssue.resolveIssue(this);
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

    public RevDistanceSensor(Rev2mDistanceSensor.Port port, Rev2mDistanceSensor.Unit units, Rev2mDistanceSensor.RangeProfile profile) {
        super(port, units, profile);
        init();
    }

    @Override
    public double getDistance() {
        return getRange();
    }

    @Override
    public String getSubsystemName() {
        return "Rev 2m distance sensor";
    }
}
