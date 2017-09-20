package org.usfirst.frc.team449.robot.subsystem.interfaces.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;

/**
 * A simple shooter subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShooterSimple extends YamlSubsystem implements SubsystemShooter {

	/**
	 * The motor that controls the shooter.
	 */
	@NotNull
	private SimpleMotor shooterMotor;

	/**
	 * The motor that controls the feeder.
	 */
	@NotNull
	private SimpleMotor feederMotor;

	/**
	 * The velocity for the shooter to run at, on [-1, 1].
	 */
	private double shooterVelocity;

	/**
	 * The velocity for the feeder to run at, on [-1, 1].
	 */
	private double feederVelocity;

	/**
	 * Time from giving the shooter voltage to being ready to fire, in milliseconds.
	 */
	private long spinUpTimeMillis;

	/**
	 * The current state of the shooter.
	 */
	@NotNull
	private ShooterState state;

	/**
	 * Default constructor
	 *
	 * @param shooterMotor     The motor that controls the shooter.
	 * @param feederMotor      The motor that controls the feeder.
	 * @param shooterVelocity  The velocity for the shooter to run at, on [-1, 1].
	 * @param feederVelocity   The velocity for the feeder to run at, on [-1, 1]. Defaults to 1.
	 * @param spinUpTimeMillis Time from giving the shooter voltage to being ready to fire, in milliseconds. Defaults to
	 *                         0.
	 */
	@JsonCreator
	public ShooterSimple(@NotNull @JsonProperty(required = true) SimpleMotor shooterMotor,
	                     @NotNull @JsonProperty(required = true) SimpleMotor feederMotor,
	                     @JsonProperty(required = true) double shooterVelocity,
	                     @Nullable Double feederVelocity,
	                     long spinUpTimeMillis) {
		this.shooterMotor = shooterMotor;
		this.feederMotor = feederMotor;
		this.shooterVelocity = shooterVelocity;
		this.feederVelocity = feederVelocity != null ? feederVelocity : 1;
		this.spinUpTimeMillis = spinUpTimeMillis;
		this.state = ShooterState.OFF;
	}

	/**
	 * Initialize the default command for a subsystem. By default subsystems have no default command, but if they do,
	 * the default command is set with this method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	/**
	 * Turn the shooter on to a map-specified speed.
	 */
	@Override
	public void turnShooterOn() {
		shooterMotor.enable();
		shooterMotor.setVelocity(shooterVelocity);
	}

	/**
	 * Turn the shooter off.
	 */
	@Override
	public void turnShooterOff() {
		shooterMotor.setVelocity(0);
		shooterMotor.disable();
	}

	/**
	 * Start feeding balls into the shooter.
	 */
	@Override
	public void turnFeederOn() {
		feederMotor.enable();
		feederMotor.setVelocity(feederVelocity);
	}

	/**
	 * Stop feeding balls into the shooter.
	 */
	@Override
	public void turnFeederOff() {
		feederMotor.setVelocity(0);
		feederMotor.disable();
	}

	/**
	 * @return The current state of the shooter.
	 */
	@NotNull
	@Override
	public ShooterState getShooterState() {
		return state;
	}

	/**
	 * @param state The state to switch the shooter to.
	 */
	@Override
	public void setShooterState(@NotNull ShooterState state) {
		this.state = state;
	}

	/**
	 * @return Time from giving the shooter voltage to being ready to fire, in milliseconds.
	 */
	@Override
	public long getSpinUpTimeMillis() {
		return spinUpTimeMillis;
	}
}
