package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;

public class WiiController {
    private final Joystick itsaWII_REMOTE;

    public WiiController(int n){
        itsaWII_REMOTE = new Joystick(n);
    }

    public double get(ControllerEnums.WiiAxis axis){
        return itsaWII_REMOTE.getRawAxis(axis.AXIS_VALUE);
    }

    public double get(ControllerEnums.WiiButton button){
        return itsaWII_REMOTE.getRawAxis(button.AXIS_VALUE);
    }
}
