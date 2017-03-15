package org.usfirst.frc.team449.robot.drive.talonCluster.util;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Notifier;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;

import java.util.List;

/**
 * Created by BlairRobot on 2017-03-15.
 */
public class MPLoader {
	
	public static void loadTopLevel(MotionProfileData data, RotPerSecCANTalonSRX talon, double wheelDiameter){
		// Fill the Talon's buffer with points
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
		for (int i = 0; i < data.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = -inchesToNative(data.data[i][0], talon.encoderCPR, wheelDiameter);
			point.velocity = -talon.RPStoNative(data.data[i][1]);
			point.timeDurMs = (int) (data.data[i][2] * 1000.);
			point.profileSlotSelect = 1;    // gain selection
			point.velocityOnly = false;  // true => no position servo just velocity feedforward
			point.zeroPos = i == 0; // If its the first point, zeroPos  =  true
			point.isLastPoint = (i + 1) == data.data.length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			if(!talon.canTalon.pushMotionProfileTrajectory(point)) {
				System.out.println("Buffer full!");
				break;
			}
		}
	}

	public static void startLoadBottomLevel(List<RotPerSecCANTalonSRX> talons, double updateRate){
		MPUpdaterProcess updater = new MPUpdaterProcess();
		for (RotPerSecCANTalonSRX talon : talons){
			updater.addTalon(talon.canTalon);
		}
		Notifier updaterNotifier = new Notifier(updater);
		updaterNotifier.startPeriodic(updateRate);
	}

	public static double nativeToInches(double nativeUnits, int encoderCPR, double wheelDiameter){
		double rotations = nativeUnits / (encoderCPR*4);
		return rotations * (wheelDiameter*Math.PI);
	}

	public static double inchesToNative(double inches, int encoderCPR, double wheelDiameter){
		double rotations = inches / (wheelDiameter*Math.PI);
		return rotations * (encoderCPR*4);
	}
}
