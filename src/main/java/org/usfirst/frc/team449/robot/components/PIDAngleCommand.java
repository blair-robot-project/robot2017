package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.command.PIDCommand;
import maps.org.usfirst.frc.team449.robot.components.AnglePIDMap;

/**
 * Created by blairrobot on 1/14/17.
 */
public abstract class PIDAngleCommand extends PIDCommand{

	protected double minimumOutput;
	protected boolean minimumOutputEnabled;

	public PIDAngleCommand(AnglePIDMap.AnglePID map){
		super(map.getPID().getP(), map.getPID().getI(), map.getPID().getD());
		setInputRange(-180, 180);
		this.getPIDController().setContinuous(true);
		this.getPIDController().setAbsoluteTolerance(map.getAbsoluteTolerance());
		this.minimumOutput = map.getMinimumOutput();
		this.minimumOutputEnabled = map.getMinimumOutputEnabled();
	}
}
