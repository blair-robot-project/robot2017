package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.components.AnglePIDMap;

/**
 * Created by blairrobot on 1/14/17.
 */
public abstract class PIDAngleCommand extends PIDCommand{

	protected double minimumOutput;
	protected boolean minimumOutputEnabled;
	protected NavxSubsystem subsystem;

	public PIDAngleCommand(AnglePIDMap.AnglePID map, NavxSubsystem subsystem){
		super(map.getPID().getP(), map.getPID().getI(), map.getPID().getD());
		setInputRange(-180, 180);
		this.getPIDController().setContinuous(true);
		this.getPIDController().setAbsoluteTolerance(map.getAbsoluteTolerance());
		this.getPIDController().setToleranceBuffer(10);
		this.minimumOutput = map.getMinimumOutput();
		this.minimumOutputEnabled = map.getMinimumOutputEnabled();
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
