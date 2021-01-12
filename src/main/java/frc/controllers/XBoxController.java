package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import frc.robot.RobotNumbers;

public class XBoxController {
	private Joystick stick;
	private double deadzone = 0.07;
	private boolean triggerFlag = false;

	public XBoxController(int n) {
		stick = new Joystick(n);
	}

	

	public double getStickLX() {
		if (Math.abs(stick.getRawAxis(0)) > deadzone){
			return stick.getRawAxis(0);
		}
		else{
			return 0;
		}
	}

	public double getStickLY() {
		if (Math.abs(stick.getRawAxis(1)) > deadzone){
			return -stick.getRawAxis(1);
		}
		else{
			return 0;
		}
	}

	public double getStickRX() {
		if (Math.abs(stick.getRawAxis(4)) > deadzone){
			return stick.getRawAxis(4);
		}
		else{
			return 0;
		}
	}

	public double getStickRY() {
		if (Math.abs(stick.getRawAxis(5)) > deadzone){
			return -stick.getRawAxis(5);
		}
		else{
			return 0;
		}
	}

	public double getLTrigger() {
		return stick.getRawAxis(2);
	}

	public double getRTrigger() {
		return stick.getRawAxis(3);
	}

	public boolean getButton(int n) {
		return stick.getRawButton(n);
	}

	public boolean getButtonDown(int n){
		return stick.getRawButtonPressed(n);
	}

	public boolean getButtonUp(int n){
		return stick.getRawButtonReleased(n);
	}

	public void setLRumble(double n) {
		stick.setRumble(RumbleType.kLeftRumble, n);
	}

	public void setRRumble(double n) {
		stick.setRumble(RumbleType.kRightRumble, n);
	}

	//conor junk
	double sensitivity = RobotNumbers.triggerSensitivity;

	public void setTriggerSensitivity(double sens){
		sensitivity = sens;
	}

	public boolean getRTriggerPressed(){
		return getRTrigger()>sensitivity;
	}
	public boolean getRTriggerMomentary(){
		boolean returnBool = false;
		if(getRTriggerPressed()&&!triggerFlag){
			triggerFlag = true;
			returnBool = true;
		}
		else if(!getRTriggerPressed()&&triggerFlag){
			triggerFlag = false;
		}
		else{
			returnBool = false;
		}
		return returnBool;
	}

	public boolean getLTriggerPressed(){
		return getRTrigger()>sensitivity;
	}
	public boolean getLTriggerMomentary(){
		boolean returnBool = false;
		if(getLTriggerPressed()&&!triggerFlag){
			triggerFlag = true;
			returnBool = true;
		}
		else if(!getLTriggerPressed()&&triggerFlag){
			triggerFlag = false;
		}
		else{
			returnBool = false;
		}
		return returnBool;
	}
}
