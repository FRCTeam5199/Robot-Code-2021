package frc.controllers;

/**
 * Some call it meme drive, i call it the only drive. Since the buildy bois are actively trying to screw over the
 * programmers, its time we take a shot back by giving them op controllers
 *
 * @author jojo2357
 * @see BaseController
 * @see ControllerEnums.DrumButton
 * @see ControllerEnums.Drums
 * @see ControllerEnums.ButtonStatus
 */
public class DrumTimeController extends BaseController {
    public DrumTimeController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.DrumButton button) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.Drums drum) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(8) && stick.getRawButton(drum.AXIS_VALUE));
    }
}
