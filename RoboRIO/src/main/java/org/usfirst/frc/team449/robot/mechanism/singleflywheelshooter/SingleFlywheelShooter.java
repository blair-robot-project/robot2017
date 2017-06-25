package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.VictorSP;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;

/**
 * Class for the flywheel
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SingleFlywheelShooter extends YamlSubsystem implements Loggable, ShooterSubsystem {
	/**
	 * The flywheel's Talon
	 */
	@NotNull
	private final RotPerSecCANTalonSRX shooterTalon;

	/**
	 * The feeder's Victor
	 */
	@NotNull
	private final VictorSP feederVictor;

	/**
	 * How fast to run the feeder, from [-1, 1]
	 */
	private final double feederThrottle;

	/**
	 * Throttle at which to run the shooter, from [-1, 1]
	 */
	private final double shooterThrottle;

	/**
	 * How long it takes the shooter to get up to speed, in milliseconds.
	 */
	private final long spinUpTime;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	@NotNull
	private ShooterState state;

	/**
	 * Default constructor
	 *
	 * @param shooterTalon    The TalonSRX controlling the flywheel.
	 * @param shooterThrottle The throttle, from [-1, 1], at which to run the shooter.
	 * @param feederVictor    The VictorSP controlling the feeder.
	 * @param feederThrottle  The throttle, from [-1, 1], at which to run the feeder.
	 * @param spinUpTimeSecs  The amount of time, in seconds, it takes for the shooter to get up to speed. Defaults to
	 *                        0.
	 */
	@JsonCreator
	public SingleFlywheelShooter(@NotNull @JsonProperty(required = true) RotPerSecCANTalonSRX shooterTalon,
	                             @JsonProperty(required = true) double shooterThrottle,
	                             @NotNull @JsonProperty(required = true) MappedVictor feederVictor,
	                             @JsonProperty(required = true) double feederThrottle,
	                             double spinUpTimeSecs) {
		super();
		this.shooterTalon = shooterTalon;
		this.shooterThrottle = shooterThrottle;
		this.feederVictor = feederVictor;
		this.feederThrottle = feederThrottle;
		state = ShooterState.OFF;
		spinUpTime = (long) (spinUpTimeSecs * 1000.);
		Logger.addEvent("Shooter F: " + shooterTalon.getCanTalon().getF(), this.getClass());
	}

	/**
	 * Set the flywheel's percent voltage
	 *
	 * @param sp percent voltage setpoint [-1, 1]
	 */
	private void setFlywheelVBusSpeed(double sp) {
		shooterTalon.setPercentVbus(sp);
	}

	/**
	 * Set the flywheel's percent PID velocity setpoint
	 *
	 * @param sp percent PID velocity setpoint [-1, 1]
	 */
	private void setFlywheelPIDSpeed(double sp) {
		if (shooterTalon.getMaxSpeed() == null) {
			setFlywheelVBusSpeed(sp);
			System.out.println("You're trying to set PID throttle, but the shooter talon doesn't have PID constants defined. Using voltage control instead.");
		} else {
			shooterTalon.setSpeed(shooterTalon.getMaxSpeed() * sp);
		}
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 *
	 * @param sp The velocity to go at [-1, 1]
	 */
	private void setFlywheelDefaultSpeed(double sp) {
		setFlywheelPIDSpeed(sp);
	}

	/**
	 * Set the speed of the feeder motor.
	 *
	 * @param sp The velocity to go at from [-1, 1]
	 */
	private void setFeederSpeed(double sp) {
		feederVictor.set(sp);
	}

	/**
	 * Do nothing
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 *
	 * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
	 */
	@NotNull
	@Override
	public String[] getHeader() {
		return new String[]{"speed",
				"setpoint",
				"error",
				"voltage",
				"current"};
	}

	/**
	 * Get the data this subsystem logs every loop.
	 *
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	@NotNull
	@Override
	public Object[] getData() {
		return new Object[]{shooterTalon.getSpeed(),
				shooterTalon.getSetpoint(),
				shooterTalon.getError(),
				shooterTalon.getCanTalon().getOutputVoltage(),
				shooterTalon.getCanTalon().getOutputCurrent()};
	}

	/**
	 * Get the name of this object.
	 *
	 * @return A string that will identify this object in the log file.
	 */
	@NotNull
	@Override
	public String getName() {
		return "shooter";
	}

	/**
	 * Turn the shooter on to a map-specified speed.
	 */
	@Override
	public void turnShooterOn() {
		shooterTalon.getCanTalon().enable();
		setFlywheelDefaultSpeed(shooterThrottle);
	}

	/**
	 * Turn the shooter off.
	 */
	@Override
	public void turnShooterOff() {
		setFlywheelVBusSpeed(0);
		shooterTalon.getCanTalon().disable();
	}

	/**
	 * Start feeding balls into the shooter.
	 */
	@Override
	public void turnFeederOn() {
		setFeederSpeed(feederThrottle);
	}

	/**
	 * Stop feeding balls into the shooter.
	 */
	@Override
	public void turnFeederOff() {
		setFeederSpeed(0);
	}

	/**
	 * Gets the shooter's state, for use in "toggle" commands.
	 *
	 * @return Off, spinning up, or shooting.
	 */
	@NotNull
	@Override
	public ShooterState getShooterState() {
		return state;
	}

	/**
	 * Sets the state of the shooter. Only called from within commands.
	 *
	 * @param state Off, spinning up, or shooting
	 */
	@Override
	public void setShooterState(@NotNull ShooterState state) {
		this.state = state;
	}

	/**
	 * How long it takes for the shooter to get up to launch speed. Should be measured experimentally.
	 *
	 * @return Time from giving the shooter a voltage to being ready to fire, in milliseconds.
	 */
	@Override
	public long getSpinUpTimeMillis() {
		return spinUpTime;
	}
}
