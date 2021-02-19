package frc.controllers;

public class BopItBasicController extends BaseController{
    public BopItBasicController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.BopItButtons button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
