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
    public static BaseController createOrGet(int channel) {
        if (channel < 0 || channel >= 6)
            throw new ArrayIndexOutOfBoundsException("You cant have a controller with id of " + channel);
        if (BaseController.allControllers[channel] == null)
            return BaseController.allControllers[channel] = new BopItBasicController(channel);
        if (BaseController.allControllers[channel] instanceof BopItBasicController)
            return BaseController.allControllers[channel];
        throw new ArrayStoreException("A different controller has already been made for channel " + channel);
    }

    private BopItBasicController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.BopItButtons button) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
