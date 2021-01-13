package frc.vision;

import edu.wpi.first.networktables.NetworkTableInstance;

public class BallLimelight{
    
    public void init(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(2);
    }

    public void update(){
        
    }

    /**
     * Get angle between crosshair and ball.
     * @return angle between crosshair and ball, left negative, 29.8 degrees in both directions.
     */
    public double getBallAngle(){ 
        double angle = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        return angle;
    }

    /**
     * Get the size of the ball onscreen.
     * @return size of the ball in % of the screen, 0-100.
     */
    public double getBallSize(){
        double size = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);
        return size;
    }
}