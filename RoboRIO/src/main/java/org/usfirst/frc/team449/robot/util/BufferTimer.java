package org.usfirst.frc.team449.robot.util;

import org.usfirst.frc.team449.robot.Robot;

/**
 * Created by noah on 5/13/17.
 */
public class BufferTimer {

	private long bufferTime;

	private long timeConditionBecameTrue;

	private boolean flag;

	public BufferTimer(double bufferTimeSeconds){
		bufferTime = (long) (bufferTimeSeconds*1000.);
	}

	public BufferTimer(long bufferTimeMilliseconds){
		bufferTime = bufferTimeMilliseconds;
	}

	public boolean get(boolean currentState){
		if (currentState && !flag){
			flag = true;
			timeConditionBecameTrue = Robot.currentTimeMillis();
		} else if (!currentState && flag){
			flag = false;
			timeConditionBecameTrue = 0;
		}
		return currentState && Robot.currentTimeMillis() - timeConditionBecameTrue > bufferTime;
	}
}
