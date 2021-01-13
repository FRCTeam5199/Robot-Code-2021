/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.Rev2mDistanceSensor.RangeProfile;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.drive.*;
import frc.spinner.*;
import frc.shooter.*;
import frc.power.*;
import frc.climber.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  Driver driver;
  Spinner spinner;
  Shooter shooter;
  PDP pdp;
  Climber climber;
  Turret turret;
  Hopper hopper;
  Intake intake;
  BallHandler baller;
  int autoStage;
  private static final String defaultAuto = "Default";
  private static final String auto1 = "Auto 1";
  private double[][] selectedAuto;
  private final SendableChooser<double[][]> chooser = new SendableChooser<>();
  

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    chooser.setDefaultOption("Default Auto", Autos.defaultAuto);
    //chooser.addOption("Auto 1", Autos.auto1);      
    // chooser.addOption("Aim Only", Autos.aimOnlyAuto);
    // chooser.addOption("Aim+Shoot", Autos.shootOnlyAuto);
    // chooser.addOption("Drive+Aim+Shoot Rightmost", Autos.runAimShootAutoRightmost);
    // chooser.addOption("Spinup Only", Autos.spinupOnlyAuto);
    // chooser.addOption("Drive+Aim+Shoot Rightmost goto Trench", Autos.runAimShootTrenchAutoRightmost);
    //chooser.addOption("Intake and Move", Autos.intakeOnlyAuto);
    // chooser.addOption("Intake, Move, Target", Autos.intakeTargetOnlyAuto);
    chooser.addOption("(Right) Intake 2 and Shoot", Autos.intakeSpinupTargetShootAuto);
    //chooser.addOption("Drive Forward Then Back", Autos.driveForwardThenReverse);
    chooser.addOption("(Right) Rendezvous + Trench", Autos.driveToRendezvousThenBackThenToTrench);
    chooser.addOption("(Left) Steal Two, Shoot", Autos.stealTwoAuto);
    chooser.addOption("(NEW) Turn North and Shoot", Autos.runForwardAimShootAuto);
    chooser.addOption("Do Nothing", Autos.nothingAuto);
    SmartDashboard.putData("Auto choices", chooser);

    driver = new Driver();
    driver.init();

    spinner = new Spinner();
    spinner.init();

    // shooter = new Shooter();
    // shooter.init();

    pdp = new PDP();
    pdp.init();

    climber = new Climber();
    climber.init();

    turret = new Turret();
    turret.init();

    //intake = new Intake();
    //intake.init();
    //intake.initPneumatic(); //jank

    // hopper = new Hopper();
    // hopper.init();

    baller = new BallHandler();
    baller.init();
    baller.intake.setDeploy(false);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    driver.setCurrentLimits(80);
    baller.shooter.interpolationEnabled = false;
    baller.intake.setDeploy(false);
    //driver.resetAuton();
    //driver.initPathfinderAuto();
    //shooter.initLogger();
    //pdp.initLogger();
    //hopper.setupSensor();
    selectedAuto = chooser.getSelected();
    setStuffUp();
    autoStage = 0;
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("Auton Stage", autoStage);
    updateAuto(selectedAuto);
  }

  private boolean setupDone = false;
  private void setStuffUp(){
    if(!setupDone){
      driver.setupAuto();
      turret.resetEncoderAndGyro();
      turret.resetPigeon();
      turret.setBrake(true);
      turret.track = false;
      baller.hopper.indexSensor.setAutomaticMode(true);
      baller.hopper.indexSensor.setRangeProfile(RangeProfile.kHighSpeed);
      baller.hopper.indexSensor.setEnabled(true);
      turret.chasingTarget = false;
      setupDone = true;
    }
  }

  @Override
  public void teleopInit() {
    driver.setCurrentLimits(50);
    baller.intake.setDeploy(true);
    baller.hopper.indexSensor.setAutomaticMode(true);
    baller.hopper.indexSensor.setRangeProfile(RangeProfile.kHighSpeed);
    baller.hopper.indexSensor.setEnabled(true);
    baller.shooter.interpolationEnabled = true;
    //setStuffUp();
    //testInit();
    // shooter.initLogger();
    // pdp.initLogger();
    //driver.stopMotors();
    //shooter.initLogger();
    //pdp.initLogger();
    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    baller.shooter.interpolationEnabled = true;
    driver.updateTeleop(); //USE FOR PRACTICE
    baller.update(); //USE
    climber.update();
    //turret.setDriveOmega(driver.omega());
    turret.track = !baller.indexing;
    turret.update();
    SmartDashboard.putNumber("drive omega", driver.omega());
  }

  @Override
  public void testInit() { 
    //turret.resetEncoder();
    //hopper.setupSensor();
    // shooter.initLogger();
    // pdp.initLogger();
    driver.setupAuto();
    driver.initPoseLogger();
    turret.resetEncoderAndGyro();
    turret.resetPigeon();
    turret.setBrake(true);
    turret.track = false;
    baller.hopper.indexSensor.setAutomaticMode(true);
    baller.hopper.indexSensor.setRangeProfile(RangeProfile.kHighSpeed);
    baller.hopper.indexSensor.setEnabled(true);
  }
  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    //driver.updateTest(); //USE FOR POINT GATHERING
    //driver.updateTeleop(); //USE FOR PRACTICE
    baller.update(); //USE
    climber.update();
    //turret.setDriveOmega(driver.omega());
    turret.track = !baller.shooting;
    turret.update();
    SmartDashboard.putNumber("drive omega", driver.omega());
  }

  @Override
  public void disabledInit() {
    autoStage = 0;
    driver.setup = false;
    setupDone = false;
    turret.setBrake(false);
    driver.unlockWheels();
    baller.hopper.indexSensor.setEnabled(false);
    //baller.closeLoggers();
    pdp.closeLogger();
    driver.closeLogger();
    baller.stopFiring();
    climber.buddyLock.set(false);
    driver.setLowGear(false);
    baller.intake.setDeploy(false);
    baller.intake.closeUnusedSolenoids();
  }

  public void updateAuto(double[][] auto){
    driver.updateGeneric();
    if(auto[autoStage][3]==-1){
      if(driver.attackPoint(auto[autoStage][0], auto[autoStage][1], auto[autoStage][2])){autoStage++;}
    }
    else if(auto[autoStage][3]==-3){
      if(driver.attackPointReverse(auto[autoStage][0], auto[autoStage][1], auto[autoStage][2])){autoStage++;}
    }
    else if(auto[autoStage][3]==-2){
      //do nothing and don't advance the auton stage, as -2 signifies the end of the auton.
    }
    else{
      if(performSpecialAction(auto[autoStage][3])){autoStage++;}
    }
    baller.updateMechanisms();
  }

  public boolean performSpecialAction(double actionToPerform){
    boolean complete = false;
    int action = (int)actionToPerform; //done because im lazy
    switch(action){
      case(0): 
        complete = specialAction0();
        break;
      case(1): //action 1 is to aim the turret to 135
        complete = specialActionAimTurret(110);
        break;
      case(2): //action 2 is to shoot all the balls in the hopper
        complete = specialActionFireAll();
        break;
      case(3): //action 3 is to setup shooter to shoot
        complete = specialActionSetupShooter();
        break;
      case(4): //action 4 is to disable turret motion
        complete = specialActionDisableTurret();
        break;
      case(5): //action 5 is to spinup shooter
        complete = specialActionSpinUpShooter();
        break;
      case(6): //action 6 is to turn on the intake
        complete = specialActionEnableIntake();
        break;
      case(7): //action 7 is to turn off the intake
        complete = specialActionDisableIntake();
        break;
      case(8): //action 8 is to deploy the intake(with the pneumatics)
        complete = specialActionDeployIntake();
        break;
      case(9): //action 9 is to retract the intake
        complete = specialActionRetractIntake();
        break;
      case(10): //action 10 is to snap the turret back to home
        complete = specialActionAimTurret(260);
        break;
      case(11): //rotate to x
        complete = specialActionAimTurret(235);
        break;
    }
    return complete;
  }

  private boolean specialAction0(){
    SmartDashboard.putString("Auto Mode", "Going Fishing");
    System.out.println("SPECIAL ACTION 0");
    return true;
  }

  int cycles = 0;
  private boolean specialActionAimTurret(double angle){
    cycles++;
    //turret.chasingTarget = true;
    SmartDashboard.putString("Auto Mode", "Aiming Turret to "+angle+" degrees for "+cycles+" cycles, chasingTarget = "+turret.chasingTarget);
    turret.setTargetAngle(angle);
    turret.chasingTarget = true;
    turret.track = true;
    turret.update();
    return turret.atTarget;
  }

  private boolean specialActionDisableTurret(){
    SmartDashboard.putString("Auto Mode", "Disabling Turret");
    turret.chasingTarget = false;
    turret.track = false;
    turret.update();
    return true;
  }

  private boolean specialActionSpinUpShooter(){
    baller.shooter.toggle(true);
    baller.updateMechanisms();
    return true;
  }

  private boolean specialActionSetupShooter(){
    baller.setupShooterTimer();
    cycles = 0;
    return true;
  }
  private boolean specialActionFireAll(){
    cycles++;
    baller.fireThreeBalls();
    SmartDashboard.putString("Auto Mode", "Shooting Balls for "+cycles+" cycles, "+baller.allBallsFired);
    //baller.update();
    return baller.allBallsFired;
  }

  private boolean specialActionEnableIntake(){
    baller.intake.setIntake(1);
    return true;
  }

  private boolean specialActionDisableIntake(){
    baller.intake.setIntake(0);
    return true;
  }

  private boolean specialActionDeployIntake(){
    baller.intake.setDeploy(true);
    return true;
  }

  private boolean specialActionRetractIntake(){
    baller.intake.setDeploy(false);
    return true;
  }
}
