package org.usfirst.frc.team449.robot.mechanism.pneumatics.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;

/**
 * Created by sam on 1/29/17.
 */
public class RunCompressor extends ReferencingCommand {
	PneumaticsSubsystem pneumaticsSubsystem;

	public RunCompressor(PneumaticsSubsystem ps) {
		super(ps);
		pneumaticsSubsystem = ps;
	}

	@Override
	public void initialize() {
		pneumaticsSubsystem.compressor.setClosedLoopControl(true);
	}

	@Override
	public void execute() {
		SmartDashboard.putNumber("Pressure (psi)", pneumaticsSubsystem.pressureSensor.getPressure());
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
