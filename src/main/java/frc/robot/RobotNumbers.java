package frc.robot;
import com.revrobotics.CANSparkMax.FaultID;

//like RobotToggles but for numbers that I would prefer to keep all in one place(may add shuffleboard stuff later)
public class RobotNumbers{
    //public static final double [NAME] = [VALUE];
    //public static final double maxHeight = 40; <-- EXAMPLE

    public static final double shooterSpinUpP = 0.0001;
    public static final double shooterSpinUpI = 0.0000000;
    public static final double shooterSpinUpD = 0.00;
    public static final double shooterRecoveryP = 0;
    public static final double shooterRecoveryI = 0;
    public static final double shooterRecoveryD = 0;

    public static final double maxSpeed = 10; //max speed in fps - REAL IS 10(for 4in wheels)
    public static final double maxRotation = 11.2; //max rotational speed in radians per second - REAL IS 11.2(for 4in wheels)
    public static final double llTolerance = 3;
    public static final double driveScale = 1;
    public static final double turnScale = 0.7;

    public static final double drivebaseP = 0;
    public static final double drivebaseI = 0;
    public static final double drivebaseD = 0.000005;
    public static final double drivebaseF = 0.000001;

    public static final double headingP = 0.05;
    public static final double headingI = 0;
    public static final double headingD = 0;
    public static final double autoSpeedMultiplier = 3;
    public static final double autoSpeed = 1;
    public static final double autoRotationMultiplier = 0.2;
    /**
     * error tolerance between actual X and Y positions and waypoint X and Y positions
     */
    public static final double autoTolerance = 0.2;

    public static final double wheelDiameter = 6; //update: now it's used once
    public static final double maxMotorSpeed = 5000; //theoretical max motor speed

    public static final double motorPulleySize = 24;
    public static final double driverPulleySize = 18;
    public static final double driverWheelDiameter = 4;

    public static final double turretRotationSpeedMultiplier = 1; //multiplied to drive omega to calibrate the compensating rotation speed offset of the turret
    public static final double turretP = 0.00000000001;
    public static final double turretI = 0;
    public static final double turretD = 0;
    public static final double turretSprocketSize = 11.1;
    public static final double motorSprocketSize = 1;
    public static final double turretGearRatio = 7;
    public static final double turretMinPos = 0;
    public static final double turretMaxPos = 270;

    public static final double triggerSensitivity = 0.25;

    //junk for motor debug code, change names if needed
    public static final String[] sparkErrors = {"Brownout", "Overcurrent", "IWDTReset", "MotorFault", "SensorFault", "Stall", "EEPROMCRC", "CANTX", "CANRX", "HasReset", "DRVFault", "OtherFault"};
    public static final FaultID[] sparkErrorIDs = {FaultID.kBrownout, FaultID.kOvercurrent, FaultID.kIWDTReset, FaultID.kMotorFault, FaultID.kSensorFault, FaultID.kStall, FaultID.kEEPROMCRC, FaultID.kCANTX, FaultID.kCANRX, FaultID.kHasReset, FaultID.kDRVFault, FaultID.kOtherFault};
}