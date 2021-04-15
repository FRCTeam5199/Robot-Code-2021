package frc.controllers;

/**
 * Some call it meme drive, i call it the only drive. Since the buildy bois are actively trying to screw over the
 * programmers, its time we take a shot back by giving them op controllers
 *
 * @see BaseController
 * @see ControllerEnums.BopItButtons
 * @see ControllerEnums.ButtonStatus
 */
public class BopItBasicController extends BaseController {
    private BopItBasicController(Integer channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.BopItButtons button) {
        return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.AXIS_VALUE));
    }
}
