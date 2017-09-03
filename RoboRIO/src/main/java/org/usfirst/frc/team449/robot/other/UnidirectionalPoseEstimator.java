package org.usfirst.frc.team449.robot.other;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;

import java.util.ArrayList;
import java.util.List;

/**
 * A Runnable for pose estimation that can take absolute positions.
 *
 * @param <T>
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalPoseEstimator <T extends SubsystemNavX & DriveUnidirectional> implements Runnable {

	/**
	 * The wheel-to-wheel diameter of the robot.
	 */
	private final double robotDiameter;

	/**
	 * The subsystem to get gyro and encoder data from.
	 */
	@NotNull
	private final T subsystem;

	/**
	 * The maximum amount, in degrees, a new absolute position's angle can be off from the gyro reading and still be
	 * accepted as valid.
	 */
	private final double absolutePosAngleTolerance;

	/**
	 * A list of all the gyro angles recorded, in order from oldest to newest and in degrees.
	 */
	@NotNull
	private List<Double> angles;

	/**
	 * A list of all [x,y] transformation vectors calculated, in order from oldest to newest and in inches.
	 */
	@NotNull
	private List<double[]> vectors;

	/**
	 * A list of all the times at which the gyro angles and vectors were recorded, in order from oldest to newest and in
	 * milliseconds.
	 */
	@NotNull
	private List<Long> times;

	/**
	 * The current x,y position of the robot, in inches.
	 */
	private double[] currentPos;

	/**
	 * The time, in milliseconds since the robot code started, that the last absolute position was recorded at.
	 */
	private long absolutePosTime;

	/**
	 * The encoder reading of the left encoder the last time the loop ran, in inches.
	 */
	private double lastLeftPos;

	/**
	 * The encoder reading of the right encoder the last time the loop ran, in inches.
	 */
	private double lastRightPos;

	/**
	 * The angle of the gyro the last time the loop ran, in degrees. This is normally the same as the last element of
	 * angles, but it's possible that a new absolute position could erase all elements of angles.
	 */
	private double lastTheta;

	/**
	 * The last time the loop ran, in milliseconds. This is normally the same as the last element of times, but it's
	 * possible that a new absolute position could erase all elements of times.
	 */
	private long lastTime;

	/**
	 * Whether the run loop is running. To be used for thread safety, currently unimplemented.
	 */
	//TODO implement thread safety.
	private boolean running;

	/**
	 * Whether a new absolute position is being added. To be used for thread safety, currently unimplemented.
	 */
	private boolean modifyingList;

	/**
	 * Default constructor.
	 *
	 * @param robotDiameter             The wheel-to-wheel diameter of the robot.
	 * @param subsystem                 The subsystem to get gyro and encoder data from.
	 * @param absolutePosAngleTolerance The maximum amount, in degrees, a new absolute position's angle can be off from
	 *                                  the gyro reading and still be accepted as valid.
	 * @param startX                    The starting X of the robot, in inches. Defaults to 0.
	 * @param startY                    The starting Y of the robot, in inches. Defaults to 0.
	 * @param startTheta                The starting angle of the robot, in degrees. Defaults to 0.
	 */
	@JsonCreator
	public UnidirectionalPoseEstimator(@JsonProperty(required = true) double robotDiameter,
	                                   @JsonProperty(required = true) @NotNull T subsystem,
	                                   @JsonProperty(required = true) double absolutePosAngleTolerance,
	                                   double startX,
	                                   double startY,
	                                   double startTheta) {
		this.robotDiameter = robotDiameter;
		this.subsystem = subsystem;
		this.absolutePosAngleTolerance = absolutePosAngleTolerance;
		lastTheta = startTheta;

		//Construct lists
		angles = new ArrayList<>();
		vectors = new ArrayList<>();
		times = new ArrayList<>();

		//Set up start pos
		currentPos = new double[2];
		currentPos[0] = startX;
		currentPos[1] = startY;

		lastLeftPos = subsystem.getLeftPos();
		lastRightPos = subsystem.getRightPos();
		absolutePosTime = 0;
		lastTime = 0;
	}

	/**
	 * Use the current gyro and encoder data to calculate how the robot has moved since the last time run was called.
	 */
	@Override
	public void run() {
		running = true;

		//Record everything at the start, as it may change between executing lines of code and that would be bad.
		double left = subsystem.getLeftPos();
		double right = subsystem.getRightPos();
		double theta = subsystem.getGyroHeading();
		long time = Robot.getTimeSinceInit();

		//Calculate differences versus the last measurement
		double deltaLeft = left - lastLeftPos;
		double deltaRight = right - lastRightPos;
		double deltaTheta = theta - lastTheta;

		//For this next part, we assume that the gyro is 100% accurate at measuring the change in angle over the given
		//time period and that the encoders will possibly overmeasure (due to wheel slip) but never undermeasure.
		//Given those constraints, we have an overdetermined system because deltaTheta should be equal to
		//(deltaLeft-deltaRight)/robotDiameter. We can use this to determine which wheel slipped more, and replace its
		//reading with a value calculated from the other encoder and the gyro.
		if (deltaTheta < (deltaLeft - deltaRight) / robotDiameter) {
			if (deltaLeft > 0) {
				deltaLeft = deltaRight + robotDiameter * deltaTheta;
			} else {
				deltaRight = deltaLeft - robotDiameter * deltaTheta;
			}
		} else if (deltaTheta > (deltaLeft - deltaRight) / robotDiameter) {
			if (deltaLeft < 0) {
				deltaLeft = deltaRight + robotDiameter * deltaTheta;
			} else {
				deltaRight = deltaLeft - robotDiameter * deltaTheta;
			}
		}

		//The vector for how much the robot moves, element 0 is x and element 1 is y.
		double[] vector = new double[2];

		//If we're going in a straight line
		if (deltaTheta == 0) {
			//we could use deltaRight here, doesn't matter. Going straight means no change in angle and left and right are the same.
			vector[0] = deltaLeft * Math.cos(theta);
			vector[1] = deltaLeft * Math.sin(theta);
		} else {
			//This next part is too complicated to explain in comments. Read this wiki page instead:
			// http://team449.shoutwiki.com/wiki/Pose_Estimation
			double r = robotDiameter / 2. * (deltaLeft + deltaRight) / (deltaLeft - deltaRight);
			double vectorAngle = (Math.PI - deltaTheta) / 2. - (Math.PI / 2 - lastTheta);
			double vectorMagnitude = (r * Math.sin(deltaTheta)) / Math.sin((Math.PI - deltaTheta) / 2.);
			vector[0] = vectorMagnitude * Math.cos(vectorAngle);
			vector[1] = vectorMagnitude * Math.sin(vectorAngle);
		}

		//If we received an absolute position between the last run and this one, scale the vector so it only includes
		//the change since the absolute position was given
		if (absolutePosTime > lastTime) {
			currentPos[0] += vector[0] * (time - absolutePosTime) / (time - lastTime);
			currentPos[1] += vector[1] * (time - absolutePosTime) / (time - lastTime);
		} else {
			currentPos[0] += vector[0];
			currentPos[1] += vector[1];
		}

		//record measurements in lists
		vectors.add(vector);
		angles.add(theta);
		times.add(time);

		//record current stuff as "last"
		lastTheta = theta;
		lastRightPos = right;
		lastLeftPos = left;
		lastTime = time;

		running = false;
	}

	/**
	 * Add an absolute position at the given time stamp.
	 *
	 * @param x    The absolute x, in inches
	 * @param y    The absolute y, in inches
	 * @param time The time, in milleseconds after the robot code started, that the absolute position was recorded.
	 * @return true if the absolute position was the most recent received and was used, false otherwise.
	 */
	public boolean addAbsolutePos(double x, double y, long time) {
		//Ignore it if it's older than the existing absolute position
		if (time < absolutePosTime) {
			return false;
		}

		modifyingList = true;

		//Add the given position
		addPos(x, y, time, getFirstKeepableIndex(time));

		modifyingList = false;
		return true;
	}

	/**
	 * Add an absolute position at the given time stamp, using an angle measured to verify that the absolute position is
	 * correct.
	 *
	 * @param x     The absolute x, in inches
	 * @param y     The absolute y, in inches
	 * @param time  The time, in milleseconds after the robot code started, that the absolute position was recorded
	 * @param angle The absolute angle, in degrees.
	 * @return true if the absolute position was the most recent received and the angle was correct enough to be used,
	 * false otherwise.
	 */
	public boolean addAbsolutePos(double x, double y, long time, double angle) {
		//Ignore it if it's older than the existing absolute position
		if (time < absolutePosTime) {
			return false;
		}

		modifyingList = true;

		//Get the first keepable index
		int firstKeepableIndex = getFirstKeepableIndex(time);

		//Find the angle of the gyro at the time the absolute position was recorded
		double angleAtTime;
		if (firstKeepableIndex == 0) {
			//If the absolute position is from before the first angle was recorded, just use the first angle.
			angleAtTime = angles.get(0);
		} else {
			//Calculate the angle at the time by assuming constant angular velocity over the interval.
			angleAtTime = (angles.get(firstKeepableIndex - 1) * (time - times.get(firstKeepableIndex - 1)) +
					angles.get(firstKeepableIndex) * (times.get(firstKeepableIndex) - time))
					/ (times.get(firstKeepableIndex) - times.get(firstKeepableIndex - 1));
		}

		//If the angle from the gyro and from the absolute position are too different, don't use the absolute position.
		if (Math.abs(angleAtTime - angle) > absolutePosAngleTolerance) {
			modifyingList = false;
			return false;
		} else {
			//Otherwise, use it.
			addPos(x, y, time, firstKeepableIndex);
			modifyingList = false;
			return true;
		}
	}

	/**
	 * Get the current absolute position of the robot
	 *
	 * @return The current x,y position in inches.
	 */
	public double[] getPos() {
		return currentPos;
	}

	/**
	 * An internal helper method that adds an absolute position given a first keepable index.
	 *
	 * @param x                  The absolute x, in inches
	 * @param y                  The absolute y, in inches
	 * @param time               The time, in milleseconds after the robot code started, that the absolute position was
	 *                           recorded
	 * @param firstKeepableIndex The first index of the times, vectors, and angles arrays recorded after the given
	 *                           time.
	 */
	private void addPos(double x, double y, long time, int firstKeepableIndex) {
		this.absolutePosTime = time;
		this.currentPos = new double[2];
		this.currentPos[0] = x;
		this.currentPos[1] = y;

		//The "first keepable" vector starts before the absolute position is measured and ends after it, so we have to
		//add the part that comes after to the position.
		if (firstKeepableIndex > 0) { //This if is just to prevent IndexOutOfBounds on times.get(firstKeepableIndex - 1)
			this.currentPos[0] += vectors.get(firstKeepableIndex)[0] * (times.get(firstKeepableIndex) - time) /
					(times.get(firstKeepableIndex) - times.get(firstKeepableIndex - 1));
			this.currentPos[1] += vectors.get(firstKeepableIndex)[1] * (times.get(firstKeepableIndex) - time) /
					(times.get(firstKeepableIndex) - times.get(firstKeepableIndex - 1));
		}

		//Trim vectors to only be the relevant vectors.
		vectors = vectors.subList(firstKeepableIndex, vectors.size());

		//Add all the vectors that come after the absolutePos to the position. Loop starts at index 1 because index 0,
		//the firstKeepableIndex, we already accounted for above.
		for (int i = 1; i < vectors.size(); i++) {
			currentPos[0] += vectors.get(i)[0];
			currentPos[1] += vectors.get(i)[1];
		}

		//Trim other lists
		times = times.subList(firstKeepableIndex, times.size());
		angles = angles.subList(firstKeepableIndex, angles.size());
	}

	/**
	 * Finds the first index of the times array that was recorded after the given time.
	 *
	 * @param time A time in milliseconds since the robot code started
	 * @return the lowest index of times whose value is greater than time.
	 */
	private int getFirstKeepableIndex(long time) {
		int firstKeepableIndex = times.size();
		for (int i = 0; i < times.size(); i++) {
			if (times.get(i) > time) {
				firstKeepableIndex = i;
				break;
			}
		}
		return firstKeepableIndex;
	}

}
