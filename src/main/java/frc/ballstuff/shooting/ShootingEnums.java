package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;
import frc.misc.UserInterface;

import java.util.function.Consumer;

import static frc.robot.Robot.*;
import static frc.robot.Robot.shooter;


/**
 * Contains Multiple different firing modes for the shooter
 */
public enum ShootingEnums {

    //Used when solid speed button is held down
    //TODO make controller dynamic
    FIRE_SOLID_SPEED_FLIGHTSTICK(shooter -> {
        shooter.setSpeed(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1));
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed() && shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    FIRE_SOLID_SPEED_OFFSEASON21(shooter -> {
        shooter.setSpeed(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1));
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed() && (shooter.isValidTarget() || shooter.joystickController.get(ControllerEnums.JoystickButtons.TWO) == ControllerEnums.ButtonStatus.DOWN) && shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    FIRE_SOLID_SPEED_WII(shooter -> {
        shooter.setSpeed(shooter.speed);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed() && shooter.joystickController.get(ControllerEnums.WiiButton.A) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    FIRE_SOLID_SPEED_DRUMS(shooter -> {
        shooter.setSpeed(shooter.speed);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed() && shooter.joystickController.get(ControllerEnums.DrumButton.B) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    FIRE_SOLID_SPEED_XBOX_CONTROLLER(shooter -> {
        shooter.setSpeed(shooter.speed);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed());
        }
    }),

    FIRE_TEST_SPEED(shooter -> {
        //shooter.setPercentSpeed(1);
        shooter.setSpeed(4200);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed());
        }
    }),

    //Used by our current vision tracking
    FIRE_HIGH_SPEED(shooter -> {
        shooter.setSpeed(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1));
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll((shooter.isAtSpeed()));
        }
    }),

    FIRE_HIGH_SPEED_SPINUP(shooter -> {
        shooter.setSpeed(3700 + (500 * shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER)));
        if (robotSettings.ENABLE_HOPPER) {
            shooter.setShooting(true);
            shooter.tryFiringBalls = true;
            hopper.setAll((shooter.isAtSpeed()));
        }
    }),

    FIRE_HIGH_SPEED_ADJUSTABLE(shooter -> {
        shooter.setSpeed(Math.ceil(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.0714 + 1)));
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll((shooter.isAtSpeed()));
        }
    }),

    FIRE_SINGLE_SHOT(shooter -> {
        if (robotSettings.ENABLE_HOPPER) {
            shooter.ticksPassed = (shooter.isAtSpeed() ? shooter.ticksPassed + 1 : 0);
            if (shooter.ticksPassed >= 50) {
                hopper.setIndexer(true);
            }
            if (!hopper.isIndexed()) {
                shooter.singleShot = false;
                hopper.setAgitator(false);
                shooter.ticksPassed = 0;
            }
        }
        shooter.setSpeed(4200);
    }),
    FIRE_MULTIPLE_SHOTS(shooter -> {
        if (shooter.ballsToShoot > shooter.ballsShot && shooter.ballsToShoot != -1) {
            shooter.setSpeed(4300); //The speed to run the shooter at during firing, typically 4200
            if (robotSettings.ENABLE_HOPPER) {
                shooter.ticksPassed = (shooter.isAtSpeed(4210) && hopper.isIndexed() && shooter.checkForDips ? shooter.ticksPassed + 1 : 0); //Shooter is at speed ticks
                if (shooter.ticksPassed >= 10) { //You're good to shoot, 0.2 seconds passed @ speed
                    hopper.setIndexer(true); //Run the indexer, fire away!
                }

                if (!hopper.isIndexed() && !shooter.loadingIndexer) {
                    shooter.emptyIndexerTicks++;
                } else {
                    shooter.emptyIndexerTicks = 0;
                }

                if (shooter.getSpeed() > 4190) {
                    shooter.checkForDips = true;
                }

                if (hopper.isIndexed()) { //Oh look there's a ball
                    shooter.loadingIndexer = false; //Stop the cooldown
                    shooter.hopperCooldownTicks = 0; //Reset the cooldown ticks
                    shooter.emptyIndexerTicks = 0; //Indexer isn't searching if it's filled
                }
                if (shooter.getSpeed() < 4180 && shooter.checkForDips) {
                    //if (shooter.emptyIndexerTicks > 50) { //It's been 0.2 second and there's no ball indexed.
                    shooter.loadingIndexer = true; //Start the cooldown to load a ball into the indexer
                    hopper.setAll(false); //Give the hopper free reign over auto indexing
                    shooter.ballsShot++; //I must've fired a ball.
                    shooter.ticksPassed = 0; //Reset the 1 second sped-up cooldown just in case, give it a chance to recover
                    shooter.emptyIndexerTicks = 0; //Reset empty hopper ticks as we're about to use this variable
                    shooter.checkForDips = false;
                }

                if (shooter.loadingIndexer) { //Indexer awaiting balls, add to cooldown
                    shooter.hopperCooldownTicks++;
                }

                if (shooter.hopperCooldownTicks > 100) { //It's been 2 seconds and no balls have been loaded
                    shooter.ballsToShoot = -1; //Stop the routine, either there's no more balls or one got jammed
                    System.out.println("!!! If you didn't fire as many balls as you wanted, then one must've jammed !!!"); //yikes.
                }
                final boolean DEBUG = true;
                if (robotSettings.DEBUG && DEBUG) {
                    UserInterface.smartDashboardPutNumber("Balls To Shoot", shooter.ballsToShoot);
                    UserInterface.smartDashboardPutNumber("Balls Shot", shooter.ballsShot);
                    UserInterface.smartDashboardPutNumber("Hopper Cooldown Ticks", shooter.hopperCooldownTicks);
                    UserInterface.smartDashboardPutNumber("Shooter Ticks Passed", shooter.ticksPassed);
                    UserInterface.smartDashboardPutNumber("Empty Hopper Ticks", shooter.emptyIndexerTicks);
                    UserInterface.smartDashboardPutBoolean("Loading Indexer", shooter.loadingIndexer);
                }
            }
        } else {
            shooter.multiShot = false;
            shooter.ballsToShoot = 0;
            shooter.ballsShot = 0;
            shooter.hopperCooldownTicks = 0;
            shooter.ticksPassed = 0;
            shooter.emptyIndexerTicks = 0;
            shooter.loadingIndexer = false;
            shooter.checkForDips = false;
            hopper.setAll(false); //We're done here. Relinquish control over the hopper.
        }
    }),

    FIRE_SINGLE_ASAP(shooter -> {
        if (robotSettings.ENABLE_HOPPER) {
            if (shooter.isAtSpeed()) {
                hopper.setIndexer(true);
            }
            if (!hopper.isIndexed()) {
                shooter.singleShot = false;
                hopper.setAgitator(false);
            }
        }
        shooter.setSpeed(4200);
    }),

    FIRE_TIMED(shooter -> {
        shooter.setSpeed(4200);
        if (Shooter.DEBUG) {
            System.out.println("Balls shot: " + shooter.ballsShot);
            System.out.println("Ticks passed: " + shooter.ticksPassed);
        }
        if (shooter.getSpeed() >= 4200) {
            shooter.timerTicks++;
            //if (++shooter.ticksPassed >= 17) {
                hopper.setAll(true);
            if (shooter.timerTicks >= shooter.goalTicks) {
                shooter.multiShot = false;
                hopper.setAll(false);
            }
        } else {
            //shooter.ticksPassed = 0;
        }
    }),

    FIRE_WITH_NO_REGARD_TO_ACCURACY(shooter -> {
        shooter.setSpeed(4400);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.getSpeed() >= 4200);
        }
    }),
    FIRE_WITH_HOPPER_CONTROLLED(shooter -> {
        shooter.setSpeed(4400);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setIndexer(shooter.getSpeed() >= 4200);
            hopper.setAgitator(!hopper.isIndexed() && shooter.getSpeed() >= 4200);
        }
    });
    public final Consumer<Shooter> function;
    boolean DEBUG = false;

    ShootingEnums(Consumer<Shooter> f) {
        function = f;
    }

    public void shoot(Shooter shooter) {

        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Shooting " + this.name());
        }
        this.function.accept(shooter);
        shooter.setShooting(true);
    }
}