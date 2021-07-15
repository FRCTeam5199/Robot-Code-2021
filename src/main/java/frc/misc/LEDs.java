package frc.misc;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.Main;
import frc.robot.Robot;

import java.util.function.Consumer;

import static frc.robot.Robot.robotSettings;

/**
 * Light Emitting Diodes, or LED's for short, emit light and we can do some cool stuff with em
 */
public class LEDs implements ISubsystem {
    private final boolean drawBackGround = true;
    private AddressableLED ledConfig;
    private AddressableLEDBuffer ledBuffer;
    private int chaseLastLEDSet = 0;

    public LEDs() {
        addToMetaList();
        init();
    }

    @Override
    public void init() {
        ledBuffer = new AddressableLEDBuffer(robotSettings.LED_STRAND_LENGTH);
        ledConfig = new AddressableLED(robotSettings.LED_STRAND_PORT_ID);
        ledConfig.setLength(ledBuffer.getLength());
        ledConfig.setData(ledBuffer);
        ledConfig.start();
    }

    @Override
    public SubsystemStatus getSubsystemStatus() {
        return ledConfig != null ? SubsystemStatus.NOMINAL : SubsystemStatus.FAILED;
    }

    @Override
    public void updateTest() {
        updateGeneric();
    }

    @Override
    public void updateTeleop() {
        updateGeneric();
    }

    @Override
    public void updateAuton() {
        updateGeneric();
    }

    @Override
    public void updateGeneric() {
        LEDEnums.RAINBOW_CHASE.runLightFunction();
        ledConfig.setData(ledBuffer);
    }

    @Override
    public void initTest() {

    }

    @Override
    public void initTeleop() {

    }

    @Override
    public void initAuton() {

    }

    @Override
    public void initDisabled() {
        LEDEnums.SOLID_COLOR_RGB.runLightFunction(new int[][]{{0, 0, 0}});
        ledConfig.setData(ledBuffer);
    }

    @Override
    public void initGeneric() {

    }

    @Override
    public String getSubsystemName() {
        return "LEDs";
    }

    public enum LEDEnums {
        //Really basic, set the background
        SOLID_COLOR_RGB((RGB) -> {
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                Robot.leds.ledBuffer.setRGB(leaderIndex, RGB[0][0], RGB[0][1], RGB[0][2]);
            }
        }),
        //Run a single pixel across the bot
        CHASE_RGB((RGB) -> {
            if (++Robot.leds.chaseLastLEDSet >= Robot.leds.ledBuffer.getLength()) {
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
        CHASE_RGB_3PIXEL((RGB) -> {
            if (++Robot.leds.chaseLastLEDSet >= Robot.leds.ledBuffer.getLength()) {
                Robot.leds.chaseLastLEDSet = 0;
            }
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                if (leaderIndex + 2 < Robot.leds.chaseLastLEDSet) {
                    Robot.leds.ledBuffer.setRGB(leaderIndex, RGB[0][0], RGB[0][1], RGB[0][2]);
                    Robot.leds.ledBuffer.setRGB(leaderIndex + 1, RGB[0][0], RGB[0][1], RGB[0][2]);
                    Robot.leds.ledBuffer.setRGB(leaderIndex + 2, RGB[0][0], RGB[0][1], RGB[0][2]);
                } else {
                    Robot.leds.ledBuffer.setRGB(leaderIndex, RGB[1][0], RGB[1][1], RGB[1][2]);
                    Robot.leds.ledBuffer.setRGB(leaderIndex + 1, RGB[1][0], RGB[1][1], RGB[1][2]);
                    Robot.leds.ledBuffer.setRGB(leaderIndex + 2, RGB[1][0], RGB[1][1], RGB[1][2]);
                }
            }
            Robot.leds.chaseLastLEDSet++;
        }),
        //Oh boy, time to bleed.
        EYES_DESERVE_TO_BLEED((ignored) -> {
            for (int leaderIndex = 0; leaderIndex < Robot.leds.ledBuffer.getLength(); leaderIndex++) {
                Robot.leds.ledBuffer.setRGB(leaderIndex, Main.RANDOM.nextInt(255), Main.RANDOM.nextInt(255), Main.RANDOM.nextInt(255));
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
        }),
        RAINBOW_CHASE((ignored) -> {
            Robot.leds.chaseLastLEDSet = (Robot.leds.chaseLastLEDSet + 1) % Robot.leds.ledBuffer.getLength();
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength()) % Robot.leds.ledBuffer.getLength(), 255, 0, 0); //RED
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 1) % Robot.leds.ledBuffer.getLength(), 255, 0, 0); //RED
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 2) % Robot.leds.ledBuffer.getLength(), 255, 0, 0); //RED
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 3) % Robot.leds.ledBuffer.getLength(), 255, 85, 0); //ORANGE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 4) % Robot.leds.ledBuffer.getLength(), 255, 85, 0); //ORANGE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 5) % Robot.leds.ledBuffer.getLength(), 255, 85, 0); //ORANGE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 6) % Robot.leds.ledBuffer.getLength(), 128, 128, 0); //YELLOW
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 7) % Robot.leds.ledBuffer.getLength(), 128, 128, 0); //YELLOW
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 8) % Robot.leds.ledBuffer.getLength(), 128, 128, 0); //YELLOW
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 9) % Robot.leds.ledBuffer.getLength(), 0, 255, 0); //GREEN
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 10) % Robot.leds.ledBuffer.getLength(), 0, 255, 0); //GREEN
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 11) % Robot.leds.ledBuffer.getLength(), 0, 255, 0); //GREEN
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 12) % Robot.leds.ledBuffer.getLength(), 0, 128, 128); //LIGHT BLUE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 13) % Robot.leds.ledBuffer.getLength(), 0, 128, 128); //LIGHT BLUE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 14) % Robot.leds.ledBuffer.getLength(), 0, 128, 128); //LIGHT BLUE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 15) % Robot.leds.ledBuffer.getLength(), 0, 0, 255); //BLUE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 16) % Robot.leds.ledBuffer.getLength(), 0, 0, 255); //BLUE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 17) % Robot.leds.ledBuffer.getLength(), 0, 0, 255); //BLUE
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 18) % Robot.leds.ledBuffer.getLength(), 213, 128, 255); //LIGHT PURPLE (INDIGO)
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 19) % Robot.leds.ledBuffer.getLength(), 213, 128, 255); //LIGHT PURPLE (INDIGO)
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 20) % Robot.leds.ledBuffer.getLength(), 213, 128, 255); //LIGHT PURPLE (INDIGO)
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 21) % Robot.leds.ledBuffer.getLength(), 119, 0, 179); //PURPLE (VIOLET)
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 22) % Robot.leds.ledBuffer.getLength(), 119, 0, 179); //PURPLE (VIOLET)
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 23) % Robot.leds.ledBuffer.getLength(), 119, 0, 179); //PURPLE (VIOLET)
            Robot.leds.ledBuffer.setRGB((Robot.leds.chaseLastLEDSet + Robot.leds.ledBuffer.getLength() - 24) % Robot.leds.ledBuffer.getLength(), 0, 0, 0); //Nothing (back)
        });

        /**
         * This bad boy is basically a fancy way of doing inheritence on the cheap. It is less messy than using an
         * interface and works in mostly the same way
         */
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