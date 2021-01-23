package frc.controllers;
import edu.wpi.first.wpilibj.Joystick;

public class ButtonPanel{
	private final Joystick buttonPanel;

	public static int lastButton;

	public ButtonPanel(int n) {
		buttonPanel = new Joystick(n);
		lastButton = -1;
	}

	public ControllerEnums.ButtonStatus get(ControllerEnums.ButtonPanelButtons n) {
		return ControllerEnums.ButtonStatus.get(buttonPanel.getRawButton(n.AXIS_VALUE));
	}
}