package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;

/**
 * A command that uses a NavX to turn to a certain angle.
 */
public abstract class PIDAngleCommand extends PIDCommand {

	protected double minimumOutput;
	protected boolean minimumOutputEnabled;
	protected NavxSubsystem subsystem;
	protected double tolerance;
	protected double deadband;

	//TODO add a timeout.
	public PIDAngleCommand(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, NavxSubsystem subsystem) {
		//Set P, I and D. I and D will normally be 0 if you're using cascading control, like you should be.
		super(map.getPID().getP(), map.getPID().getI(), map.getPID().getD());
		//Navx reads from -180 to 180.
		setInputRange(-180, 180);
		//It's a circle, so it's continuous
		this.getPIDController().setContinuous(true);
		//Tolerance should be a variable so we can scale the deadband later.
		tolerance = map.getAbsoluteTolerance();
		this.getPIDController().setAbsoluteTolerance(tolerance);
		//This is how long we have to be within the tolerance band. Multiply by loop period for time in ms.
		this.getPIDController().setToleranceBuffer(map.getToleranceBuffer());
		//this.getPIDController().setOutputRange(-0.7, 0.7);
		//Minimum ouutput, the smallest output it's possible to give. One-tenth of your drive's top speed is about
		// right.
		this.minimumOutput = map.getMinimumOutput();
		this.minimumOutputEnabled = map.getMinimumOutputEnabled();
		//This caps the output we can give. One way to set this up is to make P large and then use this to prevent
		// overshoot.
		if (map.getMaximumOutputEnabled()) {
			this.getPIDController().setOutputRange(-map.getMaximumOutput(), map.getMaximumOutput());
		}
		if (map.getDeadbandEnabled()) {
			this.deadband = map.getDeadband();
		} else {
			this.deadband = 0;
		}
		this.subsystem = subsystem;
	}

	/**
	 * Returns the input for the pid loop.
	 * <p>
	 * <p>
	 * It returns the input for the pid loop, so if this command was based off of
	 * a gyro, then it should return the angle of the gyro
	 * </p>
	 * <p>
	 * <p>
	 * All subclasses of {@link PIDCommand} must override this method.
	 * </p>
	 * <p>
	 * <p>
	 * This method will be called in a different thread then the {@link Scheduler}
	 * thread.
	 * </p>
	 *
	 * @return the value the pid loop should use as input
	 */
	@Override
	protected double returnPIDInput() {
		return subsystem.getGyroOutput();
	}
}
