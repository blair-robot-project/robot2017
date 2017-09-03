package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PoseEstimator implements Runnable{

	private double lastTheta;

	

	@Override
	public void run() {

	}
}
