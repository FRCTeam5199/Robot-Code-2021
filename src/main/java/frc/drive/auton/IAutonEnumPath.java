package frc.drive.auton;

/**
 * I am literally just vibing. Make enums for auton paths implement this and then make this return the path where the
 * generated path can be found. That way, all autons can be different yet the same without having to do much of
 * anything
 */
public interface IAutonEnumPath {
    String getDeployLocation();
}
