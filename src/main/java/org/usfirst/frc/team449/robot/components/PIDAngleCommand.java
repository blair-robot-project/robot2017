package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.command.PIDCommand;

/**
 * Created by blairrobot on 1/14/17.
 */
public abstract class PIDAngleCommand extends PIDCommand{

	public PIDAngleCommand(double p, double i, double d, double absoluteTolerance){
		super(p, i, d);
		setInputRange(-180, 180);
		this.getPIDController().setContinuous(true);
		this.getPIDController().setAbsoluteTolerance(absoluteTolerance);
	}
}
