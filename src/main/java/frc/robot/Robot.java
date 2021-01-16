package frc.robot;

import com.revrobotics.Rev2mDistanceSensor.RangeProfile;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.RobotToggles;

import frc.drive.*;

public class Robot extends TimedRobot {
    public DriveManager driver;

    @Override
    public void robotInit() {
        if (RobotToggles.ENABLE_DRIVE){
            driver = new DriveManager();
            driver.init();
        }
        if (RobotToggles.ENABLE_INTAKE){
            //init intake
        }
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void disabledInit() {
    }
}
