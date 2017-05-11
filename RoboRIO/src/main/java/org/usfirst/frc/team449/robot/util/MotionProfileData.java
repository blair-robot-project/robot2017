package org.usfirst.frc.team449.robot.util;

import maps.org.usfirst.frc.team449.robot.components.MotionProfileMap;
import org.usfirst.frc.team449.robot.Robot;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Data structure containing the array of points for the MP and a method to fill the MP from a csv file
 */
public class MotionProfileData{
	public double data[][];

	private int dPtr = -1;

	private boolean inverted;

	public MotionProfileData(MotionProfileMap.MotionProfile map){
		inverted = map.getInverted();
		readFile(Robot.RESOURCES_PATH+map.getFilename());
	}

	public MotionProfileData(String filename, boolean inverted) {
		this.inverted = inverted;
		readFile(filename);
	}

	private void readFile(String filename) {
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			stream.forEach(this::processLine);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processLine(String line) {
		String[] tokens = line.split(",\t");

		if (dPtr == -1) {
			data = new double[Integer.parseInt(tokens[0])][3];
		} else {
			// Strip the end of line comma
			tokens[2] = tokens[2].replace(",", "");

			double[] tmp = Arrays.stream(tokens).mapToDouble(Double::parseDouble).toArray();
			if (inverted) {
				tmp[0] = -tmp[0];
				tmp[1] = -tmp[1];
			}
			data[dPtr] = tmp;
		}
		dPtr++;
	}
}
