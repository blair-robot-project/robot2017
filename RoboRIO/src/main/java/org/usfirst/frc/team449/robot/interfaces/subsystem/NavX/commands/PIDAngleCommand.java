package org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.usfirst.frc.team449.robot.components.AnglePID;
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
	 * The subsystem to execute this command on.
	 */
	protected NavxSubsystem subsystem;

	/**
	 * The range in which output is turned off to prevent "dancing" around the setpoint.
	 */
	protected double deadband;

	/**
	 * Whether or not the loop is inverted.
	 */
	protected boolean inverted;

	/**
	 * Default constructor.
	 *
	 * @param PID       The map with this command's constants.
	 * @param subsystem The NavX subsystem.
	 */
	@JsonCreator
	public PIDAngleCommand(@JsonProperty(required = true) AnglePID PID,
	                       @JsonProperty(required = true) NavxSubsystem subsystem) {
		//Set P, I and D. I and D will normally be 0 if you're using cascading control, like you should be.
		super(PID.getPID().getP(), PID.getPID().getI(), PID.getPID().getD());
		this.subsystem = subsystem;

		//Navx reads from -180 to 180.
		setInputRange(-180, 180);

		//It's a circle, so it's continuous
		this.getPIDController().setContinuous(true);

		//Set the absolute tolerance to be considered on target within.
		this.getPIDController().setAbsoluteTolerance(PID.getAbsoluteTolerance());

		//This is how long we have to be within the tolerance band. Multiply by loop period for time in ms.
		this.getPIDController().setToleranceBuffer(PID.getToleranceBuffer());

		//Minimum output, the smallest output it's possible to give. One-tenth of your drive's top speed is about
		// right.
		//TODO test and implement that Talon nominalOutputVoltage and then get rid of this.
		minimumOutput = PID.getMinimumOutput();

		//This caps the output we can give. One way to set up closed-loop is to make P large and then use this to
		// prevent overshoot.
		if (PID.getMaximumOutput() != null) {
			this.getPIDController().setOutputRange(-PID.getMaximumOutput(), PID.getMaximumOutput());
		}

		//Set a deadband around the setpoint, in degrees, within which don't move, to avoid "dancing"
		deadband = PID.getDeadband();

		//Set whether or not to invert the loop.
		inverted = PID.isInverted();
	}

	/**
	 * Process the output of the PID loop to account for minimum output, deadband, and inversion.
	 *
	 * @param output The output from the WPILib angular PID loop.
	 * @return The processed output, ready to be subtracted from the left side of the drive output and added to the
	 * right side.
	 */
	protected double processPIDOutput(double output) {
		//Set the output to the minimum if it's too small.
		if (output > 0 && output < minimumOutput) {
			output = minimumOutput;
		} else if (output < 0 && output > -minimumOutput) {
			output = -minimumOutput;
		}
		//Set the output to 0 if we're within the deadband.
		if (Math.abs(this.getPIDController().getError()) < deadband) {
			output = 0;
		}
		if (inverted) {
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
