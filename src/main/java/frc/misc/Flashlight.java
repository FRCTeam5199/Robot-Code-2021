package frc.misc;

import edu.wpi.first.wpilibj.PWM;

public class Flashlight implements ISubsystem {
    PWM pwm;

    public Flashlight() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        pwm = new PWM(0);
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return null;
    }

    @Override
    public void updateTest() {
        pwm.setRaw(255);
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
        System.out.println("Let there be light");
    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {
        pwm.setRaw(0);
    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return null;
    }
}
