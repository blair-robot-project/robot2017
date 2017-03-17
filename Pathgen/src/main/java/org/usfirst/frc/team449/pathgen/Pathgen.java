package org.usfirst.frc.team449.pathgen;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ryant on 2017-01-15.
 */
public class Pathgen {
	public static void main(String[] args) throws IOException {
		Waypoint[] points = new Waypoint[]{
				new Waypoint(0, 0, 0),
//				new Waypoint(132.125 - 33.5 + 4 + 35 - 6, 25.125 + 1/2 * 35.5 - 36 + 6 + 3, Math.PI / 3.)
//				new Waypoint(132.125 - 33.5 + 4 + 35 - 12, 25.125 + 1/2 * 35.5 - 36 + 6 + 3+2, Math.PI / 3.)
//				new Waypoint(132.125 - 33.5 + 4 + 35 - 16, 25.125 + 1/2 * 35.5 - 36 + 6 + 3+2, Math.PI / 3.)
				new Waypoint(121.625, -(-17.875), Math.PI / 3.)
//				new Waypoint(70, 0, 0)

		};

		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config
				.SAMPLES_HIGH, 0.05, 3.5, 0.5, 0.1);

		Trajectory trajectory = Pathfinder.generate(points, config);

		TankModifier tm = new TankModifier(trajectory).modify(28.5);

		FileWriter lfw = new FileWriter("leftProfile.csv", false);
		FileWriter rfw = new FileWriter("rightProfile.csv", false);
		FileWriter cfw = new FileWriter("combinedProfile.csv", false);


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
