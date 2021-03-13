package frc.vision.distancesensor;

import com.revrobotics.Rev2mDistanceSensor;
import frc.misc.SubsystemStatus;
import frc.selfdiagnostics.DistanceSensorNonOpIssue;

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

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return getDistance() <= 0 ? SubsystemStatus.FAILED : SubsystemStatus.NOMINAL;
    }

    @Override
    public double getDistance() {
        return getRange();
    }

    @Override
    public String getSubsystemName() {
        return "Rev 2m distance sensor";
    }

    public RevDistanceSensor(Rev2mDistanceSensor.Port port, Rev2mDistanceSensor.Unit units, Rev2mDistanceSensor.RangeProfile profile) {
        super(port, units, profile);
        init();
    }
}
