package org.usfirst.frc.team449.robot.util;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.Robot;

/**
 * Created by noah on 5/20/17.
 */
public class WaitForMillis extends Command{

	private long finishTime;

	public WaitForMillis(long time){
		finishTime = Robot.currentTimeMillis() + time;
	}

	@Override
	protected boolean isFinished() {
		return Robot.currentTimeMillis() >= finishTime;
	}
}
