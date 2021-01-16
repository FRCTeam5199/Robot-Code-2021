package frc.robot;

//should probably do in RobotMap, but I personally prefer dumping all the toggles in here. Could also maybe integrate Shuffleboard later.
public class RobotToggles{
    //public static final boolean [NAME] = [STATE];
    //public static final boolean happiness = false; <-- EXAMPLE

    public static boolean postAnything = true; //master info posting toggle, nothing unimportant will be posted with this disabled

    public static boolean postPositionInfo = true;
    public static boolean postPositionInfoInches = false;
    public static boolean postPigeonPitch = true;
    public static boolean postPigeonYaw = true;
    public static boolean postPigeonRoll = true;
    public static boolean postMotorDebug = true;

    public static boolean logData = false;

    public static boolean shooterPID = true;

    public static boolean drive = true;
    public static boolean useDrivePID = false; //use PID based drive controller
}