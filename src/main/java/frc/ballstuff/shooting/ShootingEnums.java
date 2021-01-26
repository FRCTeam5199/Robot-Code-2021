package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;

import java.util.function.Consumer;

import static frc.ballstuff.shooting.ShootingStyles.fireIndexerDependent;
import static frc.robot.Robot.hopper;

public enum ShootingEnums {

    FIRE_HIGH_ACCURACY(shooter -> {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        boolean runDisable = false;//hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        hopper.setAgitator((shooter.atSpeed() || false));//&&(validTarget()||visOverride)&&!runDisable);
        hopper.setIndexer((shooter.atSpeed() || false));//&&(validTarget()||visOverride)&&!runDisable);
    }),

    FIRE_HIGH_SPEED(shooter -> {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        // boolean runDisable = hopper.disableOverride.getBoolean(false);
        
        //shooter.toggle(true);
        shooter.setSpeed(0);
        hopper.setAgitator((shooter.spunUp() || shooter.recovering() || false) && (shooter.validTarget() || false) && !false);
        hopper.setIndexer((shooter.spunUp() || shooter.recovering() || false) && (shooter.validTarget() || false) && !false);
    }),

    FIRE_INDEXER_INDEPENDENT(shooter -> {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            hopper.setIndexer(shooter.atSpeed() && hopper.indexed);
        }
    }),

    FIRE_TIMED(shooter -> {
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
    }),

    FIRE_MIXED(shooter -> {
        shooter.shooting = shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN;
        if (shooter.shooting) {
            if (shooter.atSpeed() && hopper.indexed) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.1)) {
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                hopper.setAll(false);
                shooter.resetShootTimer();
            }
        } else {
            hopper.setAll(false);
            shooter.resetShootTimer();
        }
    });

    public final Consumer<Shooter> function;

    ShootingEnums(Consumer<Shooter> f){
        function = f;
    }

    public void shoot(Shooter shooter){
        this.function.accept(shooter);
    }
}
