package org.usfirst.frc.team449.pathgen;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a motion profile that hits any number of waypoints.
 */
public class Pathgen {
	public static void main(String[] args) throws IOException {
		final double CENTER_TO_FRONT = 27./2.;
		final double CENTER_TO_BACK = 27./2. + 3.25;
		final double CENTER_TO_SIDE = 29./2. + 3.25;
		final double BACK_FROM_PEG = 0;
		//DO NOT TOUCH THE ONES BELOW
		final double CARRIAGE_LEN = 3.63;
		final double WALL_TO_CENTER_PEG = 114.;
		final double WALL_TO_SIDE_PEG = 131.925;
		final double BACK_CORNER_TO_SIDE_PEG = 94.139;
		final double HALF_KEY_LENGTH = 158.9/2.;
		final double KEY_CORNER_TO_SIDE_PEG = 18.89;
		final double AIRSHIP_PARALLEL_OFFSET = 6.-2.5;

		final double PEG_BASE_TO_CENTER = CENTER_TO_FRONT + CARRIAGE_LEN + BACK_FROM_PEG;

		Waypoint[] points = new Waypoint[]{ //Units are feet and radians.
				new Waypoint(0, 0, 0),
				new Waypoint(100./12., 0, 0)
		};

		Waypoint[] left = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((WALL_TO_SIDE_PEG-CENTER_TO_BACK - 0.5*PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET*Math.cos(5.*Math.PI/6.))/12.
						,-(BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3.)/2.)*PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET*Math.sin(5.*Math.PI/6.))/12.,-Math.PI/3.)
		};

		Waypoint[] right = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((WALL_TO_SIDE_PEG-CENTER_TO_BACK - 0.5*PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET*Math.cos(5.*Math.PI/6.))/12.
						,(BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3.)/2.)*PEG_BASE_TO_CENTER + AIRSHIP_PARALLEL_OFFSET*Math.sin(5.*Math.PI/6.))/12.,Math.PI/3.)
		};

		Waypoint[] center = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((WALL_TO_CENTER_PEG - CENTER_TO_BACK - PEG_BASE_TO_CENTER)/12., 0, 0)
		};

		Waypoint[] redPegToKey = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((PEG_BASE_TO_CENTER*Math.cos(Math.toRadians(180)) + WALL_TO_SIDE_PEG*Math.cos(Math.toRadians(-60)) + KEY_CORNER_TO_SIDE_PEG*Math.cos(Math.toRadians(30))
				+ HALF_KEY_LENGTH*Math.cos(Math.toRadians(75)) + CENTER_TO_BACK*Math.cos(Math.toRadians(165)))/12.,
						(WALL_TO_SIDE_PEG*Math.sin(Math.toRadians(-60)) + KEY_CORNER_TO_SIDE_PEG*Math.sin(Math.toRadians(30))
								+ HALF_KEY_LENGTH*Math.sin(Math.toRadians(75)) + CENTER_TO_BACK*Math.sin(Math.toRadians(165)))/12.,
						-Math.toRadians(16))
		};

		Waypoint[] bluePegToKey = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((PEG_BASE_TO_CENTER*Math.cos(Math.toRadians(180)) + WALL_TO_SIDE_PEG*Math.cos(Math.toRadians(60)) + KEY_CORNER_TO_SIDE_PEG*Math.cos(Math.toRadians(-30))
						+ HALF_KEY_LENGTH*Math.cos(Math.toRadians(-75)) + CENTER_TO_BACK*Math.cos(Math.toRadians(-165)))/12.,
						(WALL_TO_SIDE_PEG*Math.sin(Math.toRadians(60)) + KEY_CORNER_TO_SIDE_PEG*Math.sin(Math.toRadians(-30))
								+ HALF_KEY_LENGTH*Math.sin(Math.toRadians(-75)) + CENTER_TO_BACK*Math.sin(Math.toRadians(-165)))/12.,
						Math.toRadians(16))
		};

		Waypoint[] backupRed = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint(3, 1, Math.PI/3)
		};

		Waypoint[] backupBlue = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint(3, -1, -Math.PI/3)
		};

		Waypoint[] forward = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint(15,0,0)
		};

		Map<String, Waypoint[]> profiles = new HashMap<>();
		profiles.put("Left", left);
		profiles.put("Right", right);
		profiles.put("Mid", center);
		profiles.put("RedShoot", redPegToKey);
		profiles.put("BlueShoot", bluePegToKey);
		profiles.put("RedBackup", backupRed);
		profiles.put("BlueBackup", backupBlue);

		final String ROBOT_NAME = "calcifer";

		//Calculated by driving each wheel n inches in opposite directions, then taking the angle moved, θ, and finding
		// the circumference of a circle moved by the robot via C = 360 * n / θ
		//You then find the diameter via C / π.
		double balbasaurWheelbase = 33.3/12.;
		//200 in: 29.96
		//50 in: 34.2

		//433.415
		double calciferWheelbase = 26./12.;

		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
				0.05, 5., 3, 6); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

		for (String profile : profiles.keySet()) {
			Trajectory trajectory = Pathfinder.generate(profiles.get(profile), config);

			TankModifier tm = new TankModifier(trajectory).modify(calciferWheelbase); //Units are feet

			FileWriter lfw = new FileWriter(ROBOT_NAME+"Left"+profile+"Profile.csv", false);
			FileWriter rfw = new FileWriter(ROBOT_NAME+"Right"+profile+"Profile.csv", false);


			lfw.write(tm.getLeftTrajectory().length()+"\n");
			for (int i = 0; i < tm.getLeftTrajectory().length(); i++) {
				lfw.write(tm.getLeftTrajectory().get(i).position + ",\t" + tm.getLeftTrajectory().get(i).velocity + ",\t"
						+ tm.getLeftTrajectory().get(i).dt + ",");
				lfw.write("\n");
			}

			rfw.write(tm.getRightTrajectory().length()+"\n");
			for (int i = 0; i < tm.getRightTrajectory().length(); i++) {
				rfw.write(tm.getRightTrajectory().get(i).position + ",\t" + tm.getRightTrajectory().get(i).velocity + "," +
						"\t" + tm.getRightTrajectory().get(i).dt + ",");
				rfw.write("\n");
			}

			lfw.flush();
			lfw.close();
			rfw.flush();
			rfw.close();
		}
	}
}
