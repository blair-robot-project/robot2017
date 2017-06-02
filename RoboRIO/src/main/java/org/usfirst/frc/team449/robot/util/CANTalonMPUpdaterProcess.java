package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread that can be spawned by motion profile executing commands that processes the motion profile buffer.
 * This is so that you can update the buffer faster than the main robot loop could.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class CANTalonMPUpdaterProcess implements Runnable {
	/**
	 * The queue of talons to update
	 */
	private Queue<CANTalon> talonQueue;

	/**
	 * Construct a CANTalonMPUpdaterProcess.
	 * Talons must be added after construction.
	 */
	public CANTalonMPUpdaterProcess() {
		talonQueue = new LinkedList<>();
	}

	/**
	 * @param talon Talon to add to the queue of Talons to update
	 */
	public void addTalon(CANTalon talon) {
		talonQueue.add(talon);
	}

	/**
	 * Process the MP buffer for each Talon in the queue
	 */
	@Override
	public void run() {
		for (CANTalon talon : talonQueue) {
			talon.processMotionProfileBuffer();
		}
	}
}
