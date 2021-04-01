package frc.vision.camera;

/**
 * A nice place for all the modes of the camera's LEDs ({@link VisionLEDMode#OFF}, {@link VisionLEDMode#ON}, {@link
 * VisionLEDMode#BLINK}).
 */
public enum VisionLEDMode {
    /**
     * Turns off the LEDs
     */
    OFF(),
    /**
     * Blinks the LEDs (dunno why you'd ever do this, but okay..)
     */
    BLINK(),
    /**
     * Turns on the LEDs
     */
    ON()
}