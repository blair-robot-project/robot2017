package org.usfirst.frc.team449.robot.drive.talonCluster.util;

import com.ctre.CANTalon;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread that can be spawned by motion profile executing commands that processes the motion profile buffer.
 * This is so that you can update the buffer faster than the RoboRIO scheduler does.
 */
public class MPUpdaterProcess implements Runnable {
	private Queue<CANTalon> talonQueue;

	public MPUpdaterProcess() {
		talonQueue = new LinkedList<>();
	}

	public void addTalon(CANTalon talon) {
		talonQueue.add(talon);
	}

	// Process the MP buffer for each Talon in the queue
	@Override
	public void run() {
		for (CANTalon talon : talonQueue){
			talon.processMotionProfileBuffer();
		}
	}
}
