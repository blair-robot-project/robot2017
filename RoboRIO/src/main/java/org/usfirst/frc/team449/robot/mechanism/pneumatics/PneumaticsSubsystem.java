package org.usfirst.frc.team449.robot.mechanism.pneumatics;

import edu.wpi.first.wpilibj.Compressor;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.PressureSensor;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.commands.RunCompressor;

/**
 * Created by sam on 1/29/17.
 */
public class PneumaticsSubsystem extends MappedSubsystem {
	public Compressor compressor;
	public PressureSensor pressureSensor;

	public PneumaticsSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticSystemMap
			                           .PneumaticSystem map) {
		super(map);
		pressureSensor = new PressureSensor(map.getPressureSensor());
		compressor = new Compressor(map.getNodeID());

		compressor.setClosedLoopControl(true);  //turns on compressor
	}

	@Override
	public void initDefaultCommand() {
		this.setDefaultCommand(new RunCompressor(this));
	}
}
