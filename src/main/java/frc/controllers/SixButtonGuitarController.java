package frc.controllers;

import frc.robot.Robot;

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
    public ControllerEnums.ButtonStatus get(ControllerInterfaces.IDiscreteInput button) {
        if (button instanceof ControllerEnums.SixKeyGuitarButtons || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return ControllerEnums.ButtonStatus.get(controller.getRawButton(button.getChannel()));
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.SixKeyGuitarButtons.class.toString() + " but got " + button.getClass().toString() + " instead");
    }

    public double get(ControllerInterfaces.IContinuousInput axis) {
        if (axis instanceof ControllerEnums.SixKeyGuitarAxis || Robot.robotSettings.PERMIT_ROUGE_INPUT_MAPPING)
            return controller.getRawAxis(axis.getChannel());
        throw new IllegalArgumentException("Wrong mapping. Expected an enum of type " + ControllerEnums.SixKeyGuitarButtons.class.toString() + " but got " + axis.getClass().toString() + " instead");
    }
}
