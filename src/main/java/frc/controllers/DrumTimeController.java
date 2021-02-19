package frc.controllers;

public class DrumTimeController extends BaseController{
    public DrumTimeController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.DrumButton button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.Drums drum){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(8) && stick.getRawButton(drum.AXIS_VALUE));
    }
}
