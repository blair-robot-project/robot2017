package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.MappedAHRS;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalon;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.DriveShifting;
import org.usfirst.frc.team449.robot.util.CANTalonMPComponent;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side and a high and low gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveTalonClusterShifting extends DriveTalonCluster implements DriveShifting {

	/**
	 * The solenoid that shifts between gears
	 */
	@NotNull
	private final DoubleSolenoid shifter;

	/**
	 * The gear to start teleop and autonomous in.
	 */
	@NotNull
	private final gear startingGear;

	/**
	 * Whether not to override auto shifting
	 */
	private boolean overrideAutoshift;

	/**
	 * What gear we're in
	 */
	@NotNull
	private gear currentGear;

	/**
	 * Default constructor.
	 *
	 * @param leftMaster   The master talon on the left side of the drive.
	 * @param rightMaster  The master talon on the right side of the drive.
	 * @param navX         The NavX on this drive.
	 * @param MPHandler    The motion profile handler that runs this drive's motion profiles.
	 * @param PIDScale     The amount to scale the output to the PID loop by. Defaults to 1.
	 * @param shifter      The piston that shifts between gears.
	 * @param startingGear The gear the drive starts in. Defaults to low.
	 */
	@JsonCreator
	public DriveTalonClusterShifting(@NotNull @JsonProperty(required = true) RotPerSecCANTalon leftMaster,
	                                 @NotNull @JsonProperty(required = true) RotPerSecCANTalon rightMaster,
	                                 @NotNull @JsonProperty(required = true) MappedAHRS navX,
	                                 @NotNull @JsonProperty(required = true) CANTalonMPComponent MPHandler,
	                                 @Nullable Double PIDScale,
	                                 @NotNull @JsonProperty(required = true) MappedDoubleSolenoid shifter,
	                                 @Nullable gear startingGear) {
		super(leftMaster, rightMaster, navX, MPHandler, PIDScale);
		//Initialize stuff
		this.shifter = shifter;

		//Default to low
		this.startingGear = startingGear != null ? startingGear : gear.LOW;
		currentGear = this.startingGear;

		// Initialize shifting constants, assuming robot is stationary.
		overrideAutoshift = false;
	}

	/**
	 * @return true if currently overriding autoshifting, false otherwise.
	 */
	@Override
	public boolean getOverrideAutoshift() {
		return overrideAutoshift;
	}

	/**
	 * @param override Whether or not to override autoshifting.
	 */
	@Override
	public void setOverrideAutoshift(boolean override) {
		this.overrideAutoshift = override;
	}

	/**
	 * Sets left and right wheel PID velocity setpoint as a percent of max setpoint. Defaults to voltage setpoint if the
	 * talons don't have PID constants.
	 *
	 * @param left  The left PID velocity setpoint as a percent [-1, 1]
	 * @param right The right PID velocity setpoint as a percent [-1, 1]
	 */
	@Override
	protected void setPIDThrottle(double left, double right) {
		//If we're not shifting or we're just turning in place, scale by the max speed in the current gear
		if (overrideAutoshift || left == right) {
			super.setPIDThrottle(left, right);
		}
		//If we are shifting, scale by the high gear max speed to make acceleration smoother and faster.
		else {
			if (leftMaster.getMaxSpeedHG() == null || rightMaster.getMaxSpeedHG() == null) {
				setVBusThrottle(left, right);
				System.out.println("You're trying to set PID throttle, but the drive talons don't have PID constants defined. Using voltage control instead.");
			} else {
				leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeedHG()));
				rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeedHG()));
			}
		}
	}

	/**
	 * @return The gear this subsystem is currently in.
	 */
	@Override
	@NotNull
	public gear getGear() {
		return currentGear;
	}

	/**
	 * Shift to a specific gear.
	 *
	 * @param gear Which gear to shift to.
	 */
	@Override
	public void setGear(@NotNull gear gear) {
		//If we want to downshift
		if (gear == DriveShifting.gear.LOW) {
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

	/**
	 * @return The gear this subsystem starts auto and teleop in.
	 */
	@NotNull
	public gear getStartingGear() {
		return startingGear;
	}
}
