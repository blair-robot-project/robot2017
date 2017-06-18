package org.usfirst.frc.team449.robot.mechanism.pneumatics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Compressor;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.components.PressureSensor;
import org.usfirst.frc.team449.robot.util.Loggable;

/**
 * A subsystem representing the pneumatics control system (e.g. the compressor and maybe a pressure sensor)
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class PneumaticsSubsystem extends YamlSubsystem implements Loggable {
	/**
	 * The compressor that provides pressure to the robot's pneumatics.
	 */
	private Compressor compressor;

	/**
	 * The pressure sensor that reads the pneumatic pressure.
	 */
	private PressureSensor pressureSensor;

	/**
	 * Default constructor
	 *
	 * @param nodeID         The node ID of the compressor.
	 * @param pressureSensor The pressure sensor attached to this pneumatics system. Can be null.
	 */
	@JsonCreator
	public PneumaticsSubsystem(@JsonProperty(required = true) int nodeID,
	                           PressureSensor pressureSensor) {
		super();
		compressor = new Compressor(nodeID);
		this.pressureSensor = pressureSensor;
	}

	/**
	 * Do nothing.
	 */
	@Override
	public void initDefaultCommand() {
		//Do Nothing
	}

	/**
	 * Start up the compressor in closed loop control mode.
	 */
	public void startCompressor() {
		compressor.setClosedLoopControl(true);
		compressor.start();
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 *
	 * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
	 */
	@Override
	public String[] getHeader() {
		return new String[]{"pressure"};
	}

	/**
	 * Get the data this subsystem logs every loop.
	 *
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	@Override
	public Object[] getData() {
		if (pressureSensor == null) {
			return new Object[]{"N/A"};
		} else {
			return new Object[]{pressureSensor.getPressure()};
		}
	}

	/**
	 * Get the name of this object.
	 *
	 * @return A string that will identify this object in the log file.
	 */
	@Override
	public String getName() {
		return "pneumatics";
	}
}
