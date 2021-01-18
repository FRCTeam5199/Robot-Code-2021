package frc.controllers;
import edu.wpi.first.wpilibj.Joystick;

public class ButtonPanel{
	private final Joystick button;

	public static int lastButton;

	public ButtonPanel(int n) {
		button = new Joystick(n);
		lastButton = -1;
	}

	public ControllerEnums.ButtonStatus getButton(int n) {
		return ControllerEnums.ButtonStatus.get(button.getRawButton(n));
	}

	public boolean getButtonDown(int n) {
		return button.getRawButtonPressed(n);
	}

	public boolean getButtonUp(int n){
		return button.getRawButtonReleased(n);
	}
}