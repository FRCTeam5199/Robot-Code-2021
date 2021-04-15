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
    private DrumTimeController(Integer channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.Drums drum) {
        return ControllerEnums.ButtonStatus.get(controller.getRawButton(8) && controller.getRawButton(drum.AXIS_VALUE));
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.DrumButton button) {
        return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.AXIS_VALUE));
    }
}
