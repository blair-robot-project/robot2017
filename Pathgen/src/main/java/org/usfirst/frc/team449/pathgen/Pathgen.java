package org.usfirst.frc.team449.pathgen;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates a motion profile that hits any number of waypoints.
 */
public class Pathgen {
	public static void main(String[] args) throws IOException {
		final double CENTER_TO_FRONT = 10;
		final double CENTER_TO_BACK = 10;
		final double CENTER_TO_SIDE = 10;
		final double BACK_FROM_PEG = 3;
		//DO NOT TOUCH THE ONES BELOW
		final double CARRIAGE_LEN = 3.63;
		final double WALL_TO_CENTER_PEG = 114.3;
		final double WALL_TO_SIDE_PEG = 131.925;
		final double BACK_CORNER_TO_SIDE_PEG = 91;
		final double HALF_KEY_LENGTH = 0;
		final double KEY_CORNER_TO_SIDE_PEG = 0;

		final double PEG_BASE_TO_CENTER = CENTER_TO_FRONT + CARRIAGE_LEN + BACK_FROM_PEG;

		Waypoint[] points = new Waypoint[]{ //Units are feet and radians.
				new Waypoint(0, 0, 0),
//				new Waypoint(132.125 - 33.5 + 4 + 35 - 6, 25.125 + 1/2 * 35.5 - 36 + 6 + 3, Math.PI / 3.)
//				new Waypoint(132.125 - 33.5 + 4 + 35 - 12, 25.125 + 1/2 * 35.5 - 36 + 6 + 3+2, Math.PI / 3.)
//				new Waypoint(132.125 - 33.5 + 4 + 35 - 16, 25.125 + 1/2 * 35.5 - 36 + 6 + 3+2, Math.PI / 3.)
//				new Waypoint(121.625, -(-17.875), Math.PI / 3.)
		};

		Waypoint[] left = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((WALL_TO_SIDE_PEG-CENTER_TO_BACK - 0.5*PEG_BASE_TO_CENTER)/12.
						,-(BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3)/2)*PEG_BASE_TO_CENTER)/12.,-Math.PI/3.)
		};

		Waypoint[] right = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((WALL_TO_SIDE_PEG-CENTER_TO_BACK - 0.5*PEG_BASE_TO_CENTER)/12.
						,(BACK_CORNER_TO_SIDE_PEG - CENTER_TO_SIDE - (Math.sqrt(3)/2)*PEG_BASE_TO_CENTER)/12.,Math.PI/3.)
		};

		Waypoint[] center = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint((WALL_TO_CENTER_PEG - CENTER_TO_BACK - CENTER_TO_FRONT)/12., 0, 0)
		};

		Waypoint[] pegToKey = new Waypoint[]{
				new Waypoint(0, 0, 0),
				new Waypoint(-PEG_BASE_TO_CENTER + WALL_TO_SIDE_PEG*Math.cos(Math.toRadians(60)) + KEY_CORNER_TO_SIDE_PEG*Math.cos(Math.toRadians(30))
				+ HALF_KEY_LENGTH*Math.cos(Math.toRadians(75)) - CENTER_TO_BACK*Math.cos(Math.toRadians(15)),
						WALL_TO_SIDE_PEG*Math.sin(Math.toRadians(60)) - KEY_CORNER_TO_SIDE_PEG*Math.sin(Math.toRadians(30)) - HALF_KEY_LENGTH*Math.sin(Math.toRadians(75)) - CENTER_TO_BACK*Math.sin(Math.toRadians(15)),
						Math.PI/12)
		};

		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
				0.05, 7, 1.5, 6); //Units are seconds, feet/second, feet/(second^2), and feet/(second^3)

		Trajectory trajectory = Pathfinder.generate(center, config);

		TankModifier tm = new TankModifier(trajectory).modify(30/12.); //Units are feet

		FileWriter lfw = new FileWriter("leftMidProfile.csv", false);
		FileWriter rfw = new FileWriter("rightMidProfile.csv", false);
		FileWriter cfw = new FileWriter("combinedSpinProfile.csv", false);


		for (int i = 0; i < tm.getLeftTrajectory().length(); i++) {
			lfw.write(tm.getLeftTrajectory().get(i).position + ",\t" + tm.getLeftTrajectory().get(i).velocity + ",\t"
					+ tm.getLeftTrajectory().get(i).dt + ",");
			lfw.write("\n");
		}

		for (int i = 0; i < tm.getRightTrajectory().length(); i++) {
			rfw.write(tm.getRightTrajectory().get(i).position + ",\t" + tm.getRightTrajectory().get(i).velocity + "," +
					"\t" + tm.getRightTrajectory().get(i).dt + ",");
			rfw.write("\n");
		}
		for (int i = 0; i < trajectory.length(); i++) {
			cfw.write(trajectory.get(i).position + ",\t" + trajectory.get(i).velocity + "," +
					"\t" + trajectory.get(i).dt + ",");
			cfw.write("\n");
		}

		lfw.flush();
		lfw.close();
		rfw.flush();
		rfw.close();
		cfw.flush();
		cfw.close();
	}
}
