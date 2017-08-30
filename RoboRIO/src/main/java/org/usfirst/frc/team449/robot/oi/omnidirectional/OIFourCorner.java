package org.usfirst.frc.team449.robot.oi.omnidirectional;

/**
 * An OI to control a drivetrain where each corner has an independently-controlled wheel (e.g. swerve or meccanum).
 */
public interface OIFourCorner {

    /**
     * The output to be given to the front-left wheel.
     * @return the front-left wheel output in the range [-1, 1]
     */
    double getFrontLeftOutput();

    double getFrontRightOutput();

    double getBackLeftOutput();

    double getBackRightOutput();

    /**
     * Whether the driver is trying to drive straight
     * @return true if the driver is trying to go straight; false otherwise
     */
    boolean commandingStraight();

}
