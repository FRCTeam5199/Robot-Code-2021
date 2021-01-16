package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;

public class JoystickController {

	private final Joystick joy;

	public JoystickController(int n) {
		joy = new Joystick(n);
	}

	public int getHat() {
		return joy.getPOV();
	}

	public double getXAxis() {
		return joy.getRawAxis(0);
	}

	public double getYAxis() {
		return joy.getRawAxis(1);
	}

	public double getZAxis() {
		return joy.getRawAxis(2);
	}

	public double getSlider() {
		return ((1 - joy.getRawAxis(3)) / 2);
		// return -(meme.getRawAxis(3));
	}

	public boolean getButton(int n) {
		return joy.getRawButton(n);
	}

	public boolean getButtonDown(int n) {
		return joy.getRawButtonPressed(n);
	}

	public boolean getButtonUp(int n){
		return joy.getRawButtonReleased(n);
	}

	public boolean hatUp() {
		int output = joy.getPOV();
		return output == 315 || output == 0 || output == 45;
	}

	public boolean hatDown() {
		int output = joy.getPOV();
		return output == 135 || output == 180 || output == 225;
	}
	
	public boolean hatLeft() {
		return joy.getPOV() == 270;
	}

	public boolean hatRight() {
		return joy.getPOV() == 90;
	}

}
