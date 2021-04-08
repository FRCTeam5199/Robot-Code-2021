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
    public static BaseController createOrGet(int channel) {
        if (channel < 0 || channel >= 6)
            throw new ArrayIndexOutOfBoundsException("You cant have a controller with id of " + channel);
        if (BaseController.allControllers[channel] == null)
            return BaseController.allControllers[channel] = new DrumTimeController(channel);
        if (BaseController.allControllers[channel] instanceof DrumTimeController)
            return BaseController.allControllers[channel];
        throw new ArrayStoreException("A different controller has already been made for channel " + channel);
    }

    private DrumTimeController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.Drums drum) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(8) && stick.getRawButton(drum.AXIS_VALUE));
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.DrumButton button) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
