package frc.controllers;

public class WiiController extends BaseController{
    public WiiController(int n){
        super(n);
    }

    public double get(ControllerEnums.WiiAxis axis){
        return stick.getRawAxis(axis.AXIS_VALUE);
    }

    public double get(ControllerEnums.WiiButton button){
        return stick.getRawAxis(button.AXIS_VALUE);
    }
}
