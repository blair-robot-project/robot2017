package org.usfirst.frc.team449.robot.util;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.Robot;

/**
 * Created by noah on 5/20/17.
 */
public class WaitForMillis extends Command{

	private long timeout;

	private long startTime;

	public WaitForMillis(long time){
		timeout = time;
	}

	@Override
	protected void initialize(){
		startTime = Robot.currentTimeMillis();
	}

	@Override
	protected boolean isFinished() {
		return Robot.currentTimeMillis() - startTime >= timeout;
	}
}
