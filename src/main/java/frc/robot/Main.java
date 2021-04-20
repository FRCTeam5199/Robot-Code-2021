/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

import java.io.File;
import java.util.Random;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what you are doing, do
 * not modify this file except to change the parameter class to the startRobot call.
 */
public final class Main {
    /**
     * No son, I refuse to make a new, unseeded random everytime we want a new song. Besides, we have a random at home
     * already so you don't need another one
     */
    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static ClientServerPipeline pipeline;
    public static boolean IS_DS;

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>If you change your main robot class, change the parameter type.
     *
     * @param args does nothing
     */
    public static void main(String... args) {
        IS_DS = (new File("/home/lvuser", "deploy")).isDirectory();
        if (IS_DS)
            RobotBase.startRobot(Robot::new);
        else {
            IS_DS = true;
            pipeline = ClientServerPipeline.getServer();
            pipeline.run();
        }
    }

    private Main() {
    }
}
