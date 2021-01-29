package frc.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotMap;

public class BallPhoton {
    public NetworkTableEntry yaw;
    public NetworkTableEntry size;
    public NetworkTableEntry isValid;

    public BallPhoton() {
        init();
    }

    public void init() {
        NetworkTableInstance table = NetworkTableInstance.getDefault();
        NetworkTable cameraTable = table.getTable("photonvision").getSubTable(RobotMap.BALL_CAM_NAME);
        yaw = cameraTable.getEntry("targetYaw");
        size = cameraTable.getEntry("targetFittedWidth");
        isValid = cameraTable.getEntry("isValid");
    }

    public void update() {

    }

    /**
     * Get angle between crosshair and ball.
     *
     * @return angle between crosshair and ball, left negative, 29.8 degrees in both directions.
     */
    public double getBallAngle() {
        double angle = yaw.getDouble(0);
        if (isValid.getBoolean(false)) {
            return angle;
        }
        return 0;
    }

    /**
     * Get the size of the ball onscreen.
     *
     * @return size of the ball in % of the screen, 0-100.
     */
    public double getBallSize() {
        double ballSize = size.getDouble(0);
        if (isValid.getBoolean(false)) {
            return ballSize;
        }
        return 0;
    }
}