package frc.motors.followers;

import com.ctre.phoenix.music.Orchestra;
import edu.wpi.first.wpilibj.Talon;
import frc.motors.AbstractMotor;
import frc.motors.TalonMotor;

public class TalonFollowerMotor extends AbstractFollowerMotor{
    public TalonFollowerMotor(int...ids) {
        motors = new TalonMotor[ids.length];
        for (int i = 0; i < ids.length; i++)
            motors[i] = new TalonMotor(ids[i]);
    }

    @Override
    public void invert(boolean invert) {
        for (AbstractMotor motor : motors)
            motor.setInverted(invert);
    }
    
    public void addToOrchestra(Orchestra orchestra) {
        for (AbstractMotor motor : motors) {
            ((TalonMotor)motor).addToOrchestra(orchestra);
        }
    }
}
