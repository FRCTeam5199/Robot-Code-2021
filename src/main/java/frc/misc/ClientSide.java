package frc.misc;

/**
 * Marks that the robot should only use this. There is no functionality here because it is very difficult to implement
 * and would be of trivial benefit. This acts as a marker annotation that tells the programmer when the
 * function/field/class should be called/accessed/used. In this case the {@link frc.robot.ClientServerPipeline
 * DiscordBot} <b>listenter</b> (usually the robot) should be the accessor of the marked thing.
 */
public @interface ClientSide {
}
