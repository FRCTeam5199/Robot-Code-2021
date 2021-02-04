package frc.controllers;

/**
 * For mario kart drive. Google it if u dunno what it is
 * Wii remote driver for windows: https://www.julianloehr.de/educational-work/hid-wiimote/
 */
public class WiiController extends BaseController{
    
    public WiiController(int n){
        super(n);
    }

    /**
     * Gets the Raw axis value starting at 0
     *
     * @see #get(ControllerEnums.WiiButton)
     * @return the state of passed axis on a scale of [-1,1]
     */
    public double get(ControllerEnums.WiiAxis axis){
        return stick.getRawAxis(axis.AXIS_VALUE);
    }

    /**
     * Gets the Raw button value and returns true if it is pressed when it is run
     *
     * @see #get(ControllerEnums.WiiAxis)
     * @return the status of the passed button
     */
    public ControllerEnums.ButtonStatus get(ControllerEnums.WiiButton button){
        return ControllerEnums.ButtonStatus.get(stick.getRawButton(button.AXIS_VALUE));
    }
}
