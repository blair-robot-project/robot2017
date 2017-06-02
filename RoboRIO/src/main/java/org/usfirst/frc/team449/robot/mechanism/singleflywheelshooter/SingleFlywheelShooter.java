package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Class for the flywheel
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class SingleFlywheelShooter extends MappedSubsystem implements Loggable, ShooterSubsystem {
	/**
	 * The flywheel's Talon
	 */
	private RotPerSecCANTalonSRX shooterTalon;

	/**
	 * The feeder's Victor
	 */
	private VictorSP feederVictor;

	/**
	 * How fast to run the feeder, from [-1, 1]
	 */
	private double feederThrottle;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	private ShooterState state;

	/**
	 * Throttle at which to run the shooter, from [-1, 1]
	 */
	private double shooterThrottle;

	/**
	 * How long it takes the shooter to get up to speed, in milliseconds.
	 */
	private long spinUpTime;

	/**
	 * Construct a SingleFlywheelShooter
	 *
	 * @param map config map
	 */
	public SingleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter
			                             .SingleFlywheelShooterMap.SingleFlywheelShooter map) {
		super(map.getMechanism());
		this.map = map;
		shooterTalon = new RotPerSecCANTalonSRX(map.getShooter());

		shooterThrottle = map.getShooterThrottle();
		state = ShooterState.OFF;
		spinUpTime = (long) (map.getSpinUpTimeSecs() * 1000.);
		feederVictor = new MappedVictor(map.getFeeder());
		feederThrottle = map.getFeederThrottle();
		Logger.addEvent("Shooter F: " + shooterTalon.canTalon.getF(), this.getClass());
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
		shooterTalon.setSpeed(shooterTalon.getMaxSpeed() * sp);
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
	@Override
	public String[] getHeader() {
		return new String[]{"speed,",
				"setpoint,",
				"error,",
				"voltage,",
				"current"};
	}

	/**
	 * Get the data this subsystem logs every loop.
	 *
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	@Override
	public Object[] getData() {
		return new Object[]{shooterTalon.getSpeed(),
				shooterTalon.getSetpoint(),
				shooterTalon.getError(),
				shooterTalon.canTalon.getOutputVoltage(),
				shooterTalon.canTalon.getOutputCurrent()};
	}

	/**
	 * Get the name of this object.
	 *
	 * @return A string that will identify this object in the log file.
	 */
	@Override
	public String getName() {
		return "shooter";
	}

	/**
	 * Turn the shooter on to a map-specified speed.
	 */
	@Override
	public void turnShooterOn() {
		shooterTalon.canTalon.enable();
		setFlywheelDefaultSpeed(shooterThrottle);
	}

	/**
	 * Turn the shooter off.
	 */
	@Override
	public void turnShooterOff() {
		setFlywheelVBusSpeed(0);
		shooterTalon.canTalon.disable();
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
	public void setShooterState(ShooterState state) {
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
