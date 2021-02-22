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

    public SixButtonGuitarController(int channel) {
        super(channel);
    }

    @Override
    public ControllerEnums.ButtonStatus get(ControllerEnums.SixKeyGuitarButtons button) {
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }

    @Override
    public double get(ControllerEnums.SixKeyGuitarAxis axis) {
        return stick.getRawAxis(axis.AXIS_VALUE);
    }
}
