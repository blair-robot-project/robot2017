package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by noah on 6/25/17.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Polynomial {

	@NotNull
	private final Map<Double, Double> powerToCoefficientMap;

	private double sign;

	private double abs;

	private double toRet;

	@JsonCreator
	public Polynomial(@Nullable Map<Double, Double> powerToCoefficientMap){
		if (powerToCoefficientMap == null || powerToCoefficientMap.size() == 0){
			this.powerToCoefficientMap = new HashMap<>(1);
			this.powerToCoefficientMap.put(1., 1.);
		} else {
			this.powerToCoefficientMap = powerToCoefficientMap;
		}
	}

	public double get(double x){
		sign = Math.signum(x);
		abs = Math.abs(x);
		toRet = 0;
		for (double power : powerToCoefficientMap.keySet()){
			toRet += Math.pow(abs, power)*powerToCoefficientMap.get(power);
		}
		return toRet*sign;
	}

	@NotNull
	public Map<Double, Double> getPowerToCoefficientMap() {
		return powerToCoefficientMap;
	}
}
