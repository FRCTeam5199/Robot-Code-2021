package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;

import static frc.robot.Robot.hopper;
import static frc.robot.Robot.shooter;

public class ShootingStyles {
    public static void fireMixed() {
        shooter.shooting = shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN;
        if (shooter.shooting) {
            if (shooter.atSpeed() && hopper.indexed) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.1)) {
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                hopper.setIndexer(false);
                hopper.setAgitator(false);
                shooter.resetShootTimer();
            }
        } else {
            hopper.setIndexer(false);
            hopper.setAgitator(false);
            shooter.resetShootTimer();
        }
    }

    public void fireTimed() {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            shooter.shooting = true;
            if (shooter.atSpeed()) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.5)) {
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                hopper.setAll(false);
                shooter.resetShootTimer();
            }
        } else {
            shooter.shooting = false;
            hopper.setAll(false);
            shooter.resetShootTimer();
        }
    }

    public static void fireIndexerDependent() {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            hopper.setIndexer(shooter.atSpeed() && hopper.indexed);
        }
    }

    public void fireHighSpeed() {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        // boolean runDisable = hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        hopper.setAgitator((shooter.spunUp() || shooter.recovering() || false) && (shooter.validTarget() || false) && !false);
        hopper.setAgitator((shooter.spunUp() || shooter.recovering() || false) && (shooter.validTarget() || false) && !false);
        hopper.setIndexer((shooter.spunUp() || shooter.recovering() || false) && (shooter.validTarget() || false) && !false);
    }

    public void fireHighAccuracy() {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        boolean runDisable = false;//hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        hopper.setAgitator((shooter.atSpeed() || false));//&&(validTarget()||visOverride)&&!runDisable);
        hopper.setIndexer((shooter.atSpeed() || false));//&&(validTarget()||visOverride)&&!runDisable);
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
