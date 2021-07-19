package frc.misc;

/**
 * I accidentally deleted this, so here we go again.
 * Allows you to control all of the solenoids for all of your air powered needs
 * @author Smaltin
 */
public class Pneumatics implements ISubsystem {
    public Pneumatics() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return SubsystemStatus.NOMINAL;
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

    @Override
    public void updateGeneric() {

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
        return "Pneumatics";
    }
}
