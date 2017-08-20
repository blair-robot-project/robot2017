package org.usfirst.frc.team449.robot.oi.unidirectional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class OIOutreach implements OIUnidirectional{

	/**
	 * The OI with higher priority that overrides if it has any input.
	 */
	@NotNull
	private final OIUnidirectional overridingOI;

	/**
	 * The OI with lower priority that gets overriden.
	 */
	@NotNull
	private final OIUnidirectional overridenOI;

	@JsonCreator
	public OIOutreach(@NotNull @JsonProperty(required = true) OIUnidirectional overridingOI,
	                  @NotNull @JsonProperty(required = true) OIUnidirectional overridenOI) {
		this.overridingOI = overridingOI;
		this.overridenOI = overridenOI;
	}

	/**
	 * The output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	@Override
	public double getLeftOutput() {
		if (overridingOI.getLeftOutput() != 0 || overridingOI.getRightOutput() != 0){
			return overridingOI.getLeftOutput();
		} else {
			return overridenOI.getLeftOutput();
		}
	}

	/**
	 * The output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	@Override
	public double getRightOutput() {
		if (overridingOI.getLeftOutput() != 0 || overridingOI.getRightOutput() != 0){
			return overridingOI.getRightOutput();
		} else {
			return overridenOI.getRightOutput();
		}
	}

	/**
	 * Whether the driver is trying to drive straight.
	 *
	 * @return True if the driver is trying to drive straight, false otherwise.
	 */
	@Override
	public boolean commandingStraight() {
		return false;
	}
}
