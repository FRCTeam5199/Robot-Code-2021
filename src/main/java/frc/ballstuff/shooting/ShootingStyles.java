package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;
import frc.robot.RobotToggles;

import static frc.robot.Robot.hopper;
import static frc.robot.Robot.shooter;

public class ShootingStyles {
    public static void fireMixed() {
        shooter.shooting = shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN;
        if (shooter.shooting) {
            if (shooter.atSpeed() && (!RobotToggles.ENABLE_HOPPER || hopper.indexed)) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.1)) {
                    if (RobotToggles.ENABLE_HOPPER)
                        hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                if (RobotToggles.ENABLE_HOPPER)
                    hopper.setAll(false);
                shooter.resetShootTimer();
            }
        } else {
            if (RobotToggles.ENABLE_HOPPER)
                hopper.setAll(false);
            shooter.resetShootTimer();
        }
    }

    public void fireTimed() {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            //shooter.shooting = true;
            if (shooter.atSpeed()) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.5)) {
                    if (RobotToggles.ENABLE_HOPPER)
                        hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                if (RobotToggles.ENABLE_HOPPER)
                    hopper.setAll(false);
                shooter.resetShootTimer();
            }
        } else {
            shooter.shooting = false;
            if (RobotToggles.ENABLE_HOPPER)
                hopper.setAll(false);
            shooter.resetShootTimer();
        }
    }

    public static void fireIndexerDependent() {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            if (RobotToggles.ENABLE_HOPPER)
                hopper.setIndexer(shooter.atSpeed() && hopper.indexed);
        }
    }

    public void fireHighSpeed() {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        // boolean runDisable = hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        if (RobotToggles.ENABLE_HOPPER) {
            hopper.setAll((shooter.spunUp() || shooter.recovering()) && (shooter.validTarget()));
        }
    }

    public void fireHighAccuracy() {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        boolean runDisable = false;//hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        if (RobotToggles.ENABLE_HOPPER)
            hopper.setAll(shooter.atSpeed());
    }

    //TODO Likely broken please review
    /*public static void fireThreeBalls() {
        fireHighAccuracy();
        shooter.shooting = true;
        shooter.allBallsFired = false;
        //return true if speed has been at target speed for a certain amount of time
        // if(atSpeed&&shooterTimer.get()>2){
        //     shooterTimer.stop();   //stop the timerasw
        //     //shooterTimer.reset();  //set the timer to zero
        //     stopFiring();          //stop firing
        //     allBallsFired = true;
        // }

        //if the shooter is at speed, reset and start the timer

        timerFlag = atSpeed();
        if (atSpeed()) {
            if (!timerFlag) {
                shooterTimer.reset();
                shooterTimer.start();
                timerFlag = true;
                System.out.println("Starting Timer");
            }
        } else {
            timerFlag = false;
            shooterTimer.stop();
            System.out.println("Stopping Timer");
            //shooterTimer.reset();
        }
        if ((atSpeed()) && shooterTimer.get() > 0.4) {
            stopFiring();
            shooterTimer.stop();
            shooterTimer.reset();
            allBallsFired = true;
            System.out.println("STOPPING THINGS!!!!!!");
        }

        System.out.println(shooterTimer.get() + " " + (actualRPM > speed - 50));
    }*/
}
