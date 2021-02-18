package frc.controllers;

public class BopItBasic extends BaseController{
    public BopItBasic(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.BopItButtons button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
