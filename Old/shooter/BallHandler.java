package frc.shooter;

import edu.wpi.first.wpilibj.Timer;
import frc.controllers.*;
import frc.shooter.*;
import frc.leds.ShooterLEDs;

public class BallHandler{
    //private ShooterLEDs leds;
    public Shooter shooter;
    public Hopper hopper;
    public Intake intake;
    private JoystickController joy;
    private ButtonPanel panel;
    public boolean shooting;
    public boolean indexing;
    private boolean forceDisable;
    private Timer shootTimer;
    private Timer indexTimer;

    //private XBoxController xbox;

    public void init(){
        joy = new JoystickController(1);
        panel = new ButtonPanel(2);
        //xbox = new XBoxController(0);
        shooter = new Shooter();
        hopper = new Hopper();
        intake = new Intake();
        shootTimer = new Timer();
        indexTimer = new Timer();
        indexTimer.stop();
        indexTimer.reset();
        shootTimer.stop();
        shootTimer.reset();
        shooter.init();
        hopper.init();
        intake.init();
        indexing = false;
        forceDisable = false;
        //leds = new ShooterLEDs();
        //leds.init();
    }
    /**
     * Update all the mechanisms being handled by the BallHandler.
     */
    public void update(){
        // if(xbox.getButtonDown(1)){
        //     leds.startShootCycle();
        // }
        if(panel.getButtonDown(13)){
            shooter.toggle(true);
        }

        if(panel.getButtonDown(9)){
            intake.setDeploy(true);
        }
        if(panel.getButtonDown(8)){
            intake.setDeploy(false);
        }
        if(joy.getHat()==180){
            intake.setIntake(1);
            //deploy intake
            //intake.setDeploy(true);
        }
        else if(joy.getHat()==0){
            intake.setIntake(-1);
            //deploy intake
            //intake.setDeploy(true);
        }
        else{
            intake.setIntake(0);
            //deployn't intake
            //intake.setDeploy(false);
        }
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        // boolean runDisable = hopper.disableOverride.getBoolean(false);
        //fireTimed();
        fireIndexerDependent();
        //fireMixed();
        // if(joy.getButton(1)){
        //     fireHighAccuracy();
        //     shooting = true;
        // }
        // else{
        //     shooter.toggle(false);
        //     hopper.setAgitator(false);
        //     hopper.setIndexer(false);
        //     shooting = false;
        // }
        if(joy.getButton(11)){
            shooter.toggle(joy.getButton(8));
            hopper.setAgitator(joy.getButton(10));
            hopper.setIndexer(joy.getButton(12));
        }
        if(panel.getButton(11)){
            hopper.setReverse(true);
        }
        else{
            hopper.setReverse(false);
        }

        if(panel.getButton(10)){
            hopper.setForced(true);
        }
        else{
            hopper.setForced(false);
        }


        if(panel.getButtonDown(6)){
            forceDisable = true;
        }
        if(panel.getButtonDown(7)){
            forceDisable = false;
        }
        
        // if(forceDisable){
        //     hopper.setAgitator(false);
        //     hopper.setIndexer(false);
        // }

        indexing = joy.getButton(1);
        //(shooter.spunUp()||spinOverride);//&&(shooter.validTarget()||true)&&!runDisable;
        // if(joy.getButton(3)){
        //     shooter.toggle(true);
        // }
        updateMechanisms();
    }

    public void updateMechanisms(){
        shooter.update();
        intake.update();
        hopper.update();
        //leds.update();
    }

    public void closeLoggers(){
        shooter.closeLogger();
    }

    public void fireHighSpeed(){
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        // boolean runDisable = hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        hopper.setAgitator((shooter.spunUp()||shooter.recovering()||false)&&(shooter.validTarget()||false)&&!false);
        hopper.setIndexer((shooter.spunUp()||shooter.recovering()||false)&&(shooter.validTarget()||false)&&!false);
    }

    public void fireHighAccuracy(){
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        boolean runDisable = false;//hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        hopper.setAgitator((shooter.atSpeed()||false));//&&(shooter.validTarget()||visOverride)&&!runDisable);
        hopper.setIndexer((shooter.atSpeed()||false));//&&(shooter.validTarget()||visOverride)&&!runDisable);
    }

    private void fireMixed(){
        if(joy.getButton(1)){
            shooting = true;
            if(shooter.atSpeed()&&hopper.indexed){
                if(!timerStarted){
                    shootTimer.start();
                    timerStarted = true;
                }
                if(shootTimer.hasPeriodPassed(0.1)){
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            }
            else{
                hopper.setIndexer(false);
                hopper.setAgitator(false);
                shootTimer.stop();
                shootTimer.reset();
                timerStarted = false;
            }
        }
        else{
            shooting = false;
            hopper.setIndexer(false);
            hopper.setAgitator(false);
            shootTimer.stop();
            shootTimer.reset();
            timerStarted = false;
        }
    }

    private void fireIndexerDependent(){
        if(joy.getButton(1)){
            hopper.setIndexer(shooter.atSpeed&&hopper.indexed);
        }
    }


    private boolean timerStarted = false;
    public void fireTimed(){
        if(joy.getButton(1)){
            shooting = true;
            if(shooter.atSpeed()){
                if(!timerStarted){
                    shootTimer.start();
                    timerStarted = true;
                }
                if(shootTimer.hasPeriodPassed(0.5)){
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            }
            else{
                hopper.setIndexer(false);
                hopper.setAgitator(false);
                shootTimer.stop();
                shootTimer.reset();
                timerStarted = false;
            }
        }
        else{
            shooting = false;
            hopper.setIndexer(false);
            hopper.setAgitator(false);
            shootTimer.stop();
            shootTimer.reset();
            timerStarted = false;
        }
    }

    public void feedIn(){

    }

    public void stopFiring(){
        shooter.toggle(false);
        hopper.setAgitator(false);
        hopper.setIndexer(false);
        shooting = false;
    }

    private Timer shooterTimer;
    public void setupShooterTimer(){
        shooterTimer = new Timer();
        timerFlag = false;
        shooterTimer.stop();
        shooterTimer.reset();
        stopFiring();
    }

    public boolean allBallsFired = false;
    private boolean timerFlag = false;
    public void fireThreeBalls(){
        fireHighAccuracy();
        shooting = true;
        allBallsFired = false;
        //return true if speed has been at target speed for a certain amount of time
        // if(shooter.atSpeed&&shooterTimer.get()>2){
        //     shooterTimer.stop();   //stop the timerasw
        //     //shooterTimer.reset();  //set the timer to zero
        //     stopFiring();          //stop firing
        //     allBallsFired = true;
        // }

        //if the shooter is at speed, reset and start the timer

        if(shooter.atSpeed()){
            if(!timerFlag){
                shooterTimer.reset();
                shooterTimer.start();
                timerFlag = true;
                System.out.println("Starting Timer");
            }
        }
        if(!shooter.atSpeed()){
            timerFlag = false;
            shooterTimer.stop();
            System.out.println("Stopping Timer");
            //shooterTimer.reset();
        }
        if((shooter.atSpeed())&&shooterTimer.get()>0.4){
            stopFiring();
            shooterTimer.stop();
            shooterTimer.reset();
            allBallsFired = true;
            System.out.println("STOPPING THINGS!!!!!!");
        }

        System.out.println(shooterTimer.get()+" "+(shooter.actualRPM>shooter.speed-50));
        updateMechanisms();
    }

    public boolean setIntakeState(boolean intakeState){
        int intakeRun = 0;
        if(intakeState){intakeRun = 1;}
        intake.setIntake(intakeRun);
        updateMechanisms();
        return true;
    }
}