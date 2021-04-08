package frc.misc;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.Robot;

import java.util.Random;
import java.util.function.Consumer;

import static frc.robot.Robot.robotSettings;

public class LEDs {
    private static final Random random = new Random(System.currentTimeMillis());
    private final boolean drawBackGround = true;
    private AddressableLED ledConfig;
    private AddressableLEDBuffer ledBuffer;
    private int chaseLastLEDSet = 0;

    public void init() {
        ledBuffer = new AddressableLEDBuffer(robotSettings.LED_STRAND_LENGTH);
        ledConfig = new AddressableLED(robotSettings.LED_STRAND_PORT_ID);
        ledConfig.setLength(ledBuffer.getLength());
        ledConfig.setData(ledBuffer);
        ledConfig.start();
    }

    public void update() {
        //LEDEnums.CHASE_RGB.runLightFunction(new int[][]{{22, 71, 142}, {0, 0, 0}}); //Robot Dolphins color ig
        LEDEnums.EYES_DESERVE_TO_BLEED.runLightFunction(null);
        ledConfig.setData(ledBuffer);
    }

    public enum LEDEnums {
        //Really basic, set the background
        SOLID_COLOR_RGB((RGB) -> {
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                Robot.leds.ledBuffer.setRGB(leaderIndex, RGB[0][0], RGB[0][1], RGB[0][2]);
            }
        }),
        //Nice chasing thing, idk haven't tested yet
        CHASE_RGB((RGB) -> {
            if (++Robot.leds.chaseLastLEDSet == Robot.leds.ledBuffer.getLength()) {
                Robot.leds.chaseLastLEDSet = 0;
            }
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                if (leaderIndex == Robot.leds.chaseLastLEDSet) {
                    Robot.leds.ledBuffer.setRGB(leaderIndex, RGB[0][0], RGB[0][1], RGB[0][2]);
                } else {
                    Robot.leds.ledBuffer.setRGB(leaderIndex, RGB[1][0], RGB[1][1], RGB[1][2]);
                }
            }
            Robot.leds.chaseLastLEDSet++;
        }),
        //Oh boy, time to bleed.
        EYES_DESERVE_TO_BLEED((ignored) -> {
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                Robot.leds.ledBuffer.setRGB(leaderIndex, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            }
        }),

        BRANDON_FUN_TIME_LAND((ignored) -> {
            int[] followColorR = {255, 255, 255, 0, 0, 128};
            int[] followColorG = {0, 165, 255, 128, 0, 0};
            int[] followColorB = {0, 0, 0, 128, 255, 128};
            if (++Robot.leds.chaseLastLEDSet == Robot.leds.ledBuffer.getLength()) {
                Robot.leds.chaseLastLEDSet = 0;
            }
            int colorStartdex = Robot.leds.chaseLastLEDSet % followColorR.length;
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                Robot.leds.ledBuffer.setRGB(leaderIndex, followColorR[colorStartdex], followColorG[colorStartdex], followColorB[colorStartdex]);
                colorStartdex = (colorStartdex + 1) % followColorR.length;
            }
            Robot.leds.ledBuffer.setRGB(Robot.leds.chaseLastLEDSet, 0, 0, 0);
        });

        private final Consumer<int[][]> ledFunction;

        LEDEnums(Consumer<int[][]> function) {
            ledFunction = function;
        }

        public void runLightFunction() {
            runLightFunction(new int[0][0]);
        }

        public void runLightFunction(int[][] val) {
            ledFunction.accept(val);
        }
    }
}