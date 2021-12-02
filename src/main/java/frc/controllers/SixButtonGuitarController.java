package frc.controllers;

/**
 * Who doesn't like playing guitar hero while also driving a robot, am I right? Now I can jam out while slowly and
 * methodically destroying the robot.
 *
 * @author Smaltin
 * @see BaseController
 * @see ControllerEnums.SixKeyGuitarButtons
 * @see ControllerEnums.SixKeyGuitarAxis
 * @see ControllerEnums.ButtonStatus
 */
public class SixButtonGuitarController extends BaseController {
    SixButtonGuitarController(Integer channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.SixKeyGuitarButtons button) {
        return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.AXIS_VALUE));
    }

    @Override
    public double get(ControllerEnums.SixKeyGuitarAxis axis) {
        return controller.getRawAxis(axis.AXIS_VALUE);
    }
}
