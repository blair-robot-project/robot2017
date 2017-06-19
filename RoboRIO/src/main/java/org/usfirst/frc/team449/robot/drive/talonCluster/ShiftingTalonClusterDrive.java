package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.util.CANTalonMPHandler;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side and a high and low gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftingTalonClusterDrive extends TalonClusterDrive implements ShiftingDrive {

	/**
	 * The solenoid that shifts between gears
	 */
	private DoubleSolenoid shifter;

	/**
	 * Whether not to override auto shifting
	 */
	private boolean overrideAutoshift;

	/**
	 * What gear we're in
	 */
	private gear currentGear;

	/**
	 * The gear to start teleop and autonomous in.
	 */
	private gear startingGear;

	/**
	 * Default constructor.
	 *
	 * @param leftMaster                       The master talon on the left side of the drive.
	 * @param rightMaster                      The master talon on the right side of the drive.
	 * @param MPHandler                        The motion profile handler that runs this drive's motion profiles.
	 * @param PIDScale                         The amount to scale the output to the PID loop by. Defaults to 1.
	 * @param shifter                          The piston that shifts between gears.
	 * @param startingGear                     The gear the drive starts in. Defaults to low.
	 */
	@JsonCreator
	public ShiftingTalonClusterDrive(@JsonProperty(required = true) RotPerSecCANTalonSRX leftMaster,
	                                 @JsonProperty(required = true) RotPerSecCANTalonSRX rightMaster,
	                                 @JsonProperty(required = true) CANTalonMPHandler MPHandler,
	                                 Double PIDScale,
	                                 @JsonProperty(required = true) MappedDoubleSolenoid shifter,
	                                 gear startingGear) {
		super(leftMaster, rightMaster, MPHandler, PIDScale);
		//Initialize stuff
		this.shifter = shifter;

		//Default to low
		if (startingGear == null) {
			startingGear = gear.LOW;
		}

		this.startingGear = startingGear;
		currentGear = startingGear;

		// Initialize shifting constants, assuming robot is stationary.
		overrideAutoshift = false;
	}

	/**
	 * A getter for whether we're currently overriding autoshifting.
	 *
	 * @return true if overriding, false otherwise.
	 */
	@Override
	public boolean getOverrideAutoshift() {
		return overrideAutoshift;
	}

	/**
	 * A setter for overriding the autoshifting.
	 *
	 * @param override Whether or not to override autoshifting.
	 */
	@Override
	public void setOverrideAutoshift(boolean override) {
		this.overrideAutoshift = override;
	}

	/**
	 * Sets left and right wheel PID velocity setpoint as a percent of max setpoint
	 *
	 * @param left  The left PID velocity setpoint as a percent [-1, 1]
	 * @param right The right PID velocity setpoint as a percent [-1, 1]
	 */
	@Override
	protected void setPIDThrottle(double left, double right) {
		//If we're not shifting or we're just turning in place, scale by the max speed in the current gear
		if (overrideAutoshift || left == right) {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeed()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeed()));
		}
		//If we are shifting, scale by the high gear max speed to make acceleration smoother and faster.
		else {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeedHG()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeedHG()));
		}
	}

	/**
	 * @return The gear this subsystem is currently in.
	 */
	@Override
	public gear getGear() {
		return currentGear;
	}

	/**
	 * Shift to a specific gear.
	 *
	 * @param gear Which gear to shift to.
	 */
	@Override
	public void setGear(gear gear) {
		//If we want to downshift
		if (gear == ShiftingDrive.gear.LOW) {
			//Physically shift gears
			shifter.set(DoubleSolenoid.Value.kForward);
			//Switch the PID constants
			rightMaster.switchToLowGear();
			leftMaster.switchToLowGear();
			//Record the current time
		} else {
			//Physically shift gears
			shifter.set(DoubleSolenoid.Value.kReverse);
			//Switch the PID constants
			rightMaster.switchToHighGear();
			leftMaster.switchToHighGear();
			//Record the current time.
		}
		//Set logging var
		currentGear = gear;
	}

	public gear getStartingGear() {
		return startingGear;
	}
}
