package frc.drive.auton;

import edu.wpi.first.wpilibj.util.Units;

public class Point {
    public final double X;
    public final double Y;

    public Point(double x, double y) {
        X = x;
        Y = y;
    }

    public boolean isWithin(double distance, Point otherPoint) /** how far away the robot is from point */ {
        return distance > getDistanceFromPoint(otherPoint);
    }

    public double getDistanceFromPoint(Point otherPoint) /**distance between new point and last point */{
        return Math.sqrt(Math.pow(X - otherPoint.X, 2) + Math.pow(Y - otherPoint.Y, 2));
    }
    
    public Point subtract(Point other){
        return new Point(X - other.X, Y - other.Y);
    }

    @Override
    public String toString(){
        return "(" + X + ", " + Y + ") ";
    }
}
