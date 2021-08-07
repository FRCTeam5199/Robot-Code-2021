package frc.drive.auton.pointtopoint;

import edu.wpi.first.wpilibj.Timer;
import frc.drive.AbstractDriveManager;
import frc.misc.ISubsystem;
import frc.misc.SubsystemStatus;
import frc.robot.Robot;

import static frc.robot.Robot.robotSettings;

public class AutonManager implements ISubsystem {
    public final Timer timer = new Timer();
    public AbstractDriveManager DRIVING_CHILD;
    public AutonRoutines autonPath;

    public AutonManager(AutonRoutines routine, AbstractDriveManager driveManager) { //Routine should be in the form of "YourPath" (paths/YourPath.wpilib.json)
        addToMetaList();
        DRIVING_CHILD = driveManager;
        autonPath = routine;
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return DRIVING_CHILD.getSubsystemStatus() == SubsystemStatus.NOMINAL && DRIVING_CHILD.guidance.getSubsystemStatus() == SubsystemStatus.NOMINAL ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    @Override
    public void updateTest() {

    }

    @Override
    public void updateTeleop() {

    }

    @Override
    public void updateAuton() {

    }

    @Override
    public void updateGeneric() {

    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {
        robotSettings.autonComplete = false;
        if (robotSettings.ENABLE_IMU) {
            DRIVING_CHILD.guidance.resetOdometry();
        }
        timer.stop();
        timer.reset();
        timer.start();
    }

    @Override
    public void initDisabled() {

    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "PointToPoint Auton Manager";
    }

    /**
     * When the path finishes, we have flags to set, brakes to prime, and music to jam to
     */
    public void onFinish() {
        robotSettings.autonComplete = true;
        if (robotSettings.ENABLE_MUSIC && !robotSettings.AUTON_COMPLETE_NOISE.equals("")) {
            DRIVING_CHILD.setBrake(true);
            Robot.chirp.loadMusic(robotSettings.AUTON_COMPLETE_NOISE);
            Robot.chirp.play();
        }
    }
}