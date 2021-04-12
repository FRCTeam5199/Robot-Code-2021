package frc.drive.auton;

/**
 * I am literally just vibing. Make enums for auton paths implement this and then make this return the path where the
 * generated path can be found. That way, all autons can be different yet the same without having to do much of
 * anything
 */
public interface IAutonEnumPath {
    /**
     * This is a method EVERY auton enum field must be able to supply.
     *
     * @return The location that the auton path associated with this auton path is stored in the deplot dir
     */
    String getDeployLocation();
}
