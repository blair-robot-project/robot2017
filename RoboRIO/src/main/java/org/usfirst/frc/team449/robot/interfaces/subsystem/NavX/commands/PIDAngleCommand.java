package org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands;

import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;

/**
 * A command that uses a NavX to turn to a certain angle.
 */
public abstract class PIDAngleCommand extends PIDCommand {

	/**
	 * The minimum the robot should be able to output, to overcome friction.
	 */
	protected double minimumOutput;
	/**
	 * Whether or not to use minimumOutput.
	 */
	protected boolean minimumOutputEnabled;

	/**
	 * The subsystem to execute this command on.
	 */
	protected NavxSubsystem subsystem;

	/**
	 * The range in which output is turned off to prevent "dancing" around the setpoint.
	 */
	protected double deadband;

	protected boolean inverted;

	/**
	 * Default constructor.
	 *
	 * @param map       The map with this command's constants.
	 * @param subsystem The NavX subsystem.
	 */
	public PIDAngleCommand(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, NavxSubsystem subsystem) {
		//Set P, I and D. I and D will normally be 0 if you're using cascading control, like you should be.
		super(map.getPID().getP(), map.getPID().getI(), map.getPID().getD());

		//Navx reads from -180 to 180.
		setInputRange(-180, 180);

		//It's a circle, so it's continuous
		this.getPIDController().setContinuous(true);

		this.getPIDController().setAbsoluteTolerance(map.getAbsoluteTolerance());

		//This is how long we have to be within the tolerance band. Multiply by loop period for time in ms.
		this.getPIDController().setToleranceBuffer(map.getToleranceBuffer());

		//Minimum output, the smallest output it's possible to give. One-tenth of your drive's top speed is about
		// right.
		minimumOutput = map.getMinimumOutput();
		minimumOutputEnabled = map.getMinimumOutputEnabled();

		//This caps the output we can give. One way to set up closed-loop is to make P large and then use this to
		// prevent overshoot.
		if (map.getMaximumOutputEnabled()) {
			this.getPIDController().setOutputRange(-map.getMaximumOutput(), map.getMaximumOutput());
		}

		//Set a deadband around the setpoint, in degrees, within which don't move, to avoid "dancing"
		if (map.getDeadbandEnabled()) {
			deadband = map.getDeadband();
		} else {
			deadband = 0;
		}

		inverted = map.getInverted();

		this.subsystem = subsystem;
	}

	protected double processPIDOutput(double output){
		//If we're using minimumOutput..
		if (minimumOutputEnabled) {
			//Set the output to the minimum if it's too small.
			if (output > 0 && output < minimumOutput) {
				output = minimumOutput;
			} else if (output < 0 && output > -minimumOutput) {
				output = -minimumOutput;
			}
		}
		//Set the output to 0 if we're within the deadband.
		if (Math.abs(this.getPIDController().getError()) < deadband) {
			output = 0;
		}
		if (inverted){
			output *= -1;
		}

		return output;
	}

	/*
	 NOTE: usePIDOutput() is an abstract method in PIDCommand. Any subclass of PIDAngleCommand must implement it.
	 It is called from the PIDController in PIDCommand, which will give it the output (i.e. u(t)) of the PID loop.
	 It's up to the programmer to decide how to use this. For any subclass of PIDAngleCommand, you can generally just
	 use it as a throttle value, or add it the throttle. Remember that one side is positive and one side is negative!
	 */

	/**
	 * Returns the input for the pid loop.
	 * <p>
	 * It returns the input for the pid loop, so if this command was based off of
	 * a gyro, then it should return the angle of the gyro
	 * </p>
	 * <p>
	 * All subclasses of {@link PIDCommand} must override this method.
	 * </p>
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
