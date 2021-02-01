package frc.controllers;

import frc.controllers.ControllerEnums.ButtonPanelButtons;

public class ButtonPanel extends BaseController{
	public ButtonPanel(int n) {
		super(n);
	}

	@Override
	public ControllerEnums.ButtonStatus get(ButtonPanelButtons n) {
		return ControllerEnums.ButtonStatus.get(stick.getRawButton(n.AXIS_VALUE));
	}
}