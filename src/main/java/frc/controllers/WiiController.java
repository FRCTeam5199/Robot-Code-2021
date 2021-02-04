package frc.controllers;

public class WiiController extends BaseController{
    
    public WiiController(int n){
        super(n);
    }

    /**
     * Gets the Raw axis value starting at 0
     */
    public double get(ControllerEnums.WiiAxis axis){
        return stick.getRawAxis(axis.AXIS_VALUE);
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     */
    public ControllerEnums.ButtonStatus get(ControllerEnums.WiiButton button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
