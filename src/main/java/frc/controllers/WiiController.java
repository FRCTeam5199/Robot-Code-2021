package frc.controllers;

public class WiiController extends BaseController{
    public WiiController(int n){
        super(n);
    }

    public double get(ControllerEnums.WiiAxis axis){
        return stick.getRawAxis(axis.AXIS_VALUE);
    }

    public ControllerEnums.ButtonStatus get(ControllerEnums.WiiButton button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
