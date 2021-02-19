package frc.controllers;

public class SixButtonGuitarController extends BaseController{

    public SixButtonGuitarController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.SixKeyGuitarButtons button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    @Override
    public double get(ControllerEnums.SixKeyGuitarAxis axis){
        return stick.getRawAxis(axis.AXIS_VALUE);
    }
}
