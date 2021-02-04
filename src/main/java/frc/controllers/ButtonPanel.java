package frc.controllers;

import frc.controllers.ControllerEnums.ButtonPanelButtons;

public class ButtonPanel extends BaseController{
	public ButtonPanel(int n) {
		super(n);
	}

	/**
	 * Gets the Raw button value and returns true if it is pressed when it is run
	 */
	@Override
	public ControllerEnums.ButtonStatus get(ButtonPanelButtons n) {
		return ControllerEnums.ButtonStatus.get(stick.getRawButton(n.AXIS_VALUE));
	}
}