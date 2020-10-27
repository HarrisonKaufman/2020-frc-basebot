package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.Timer;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Set;

/**
 * This behavior zeroes the Hopper talon and kicker solenoid
 */

public class Hopper_Zero implements Behavior {

	private static final Logger sLogger = LogManager.getLogger(Hopper_Zero.class);
	private static final Set<String> sSubsystems = Set.of("ss_hopper");

	private final InputValues fSharedInputValues;
	private final OutputValues fSharedOutputValues;

	private double fHopperSpeed;
	private boolean fKickerPosition;
	private int fTimeoutTime;
	private Timer fTimeoutTimer;

	public Hopper_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		fSharedInputValues = inputValues;
		fSharedOutputValues = outputValues;

		fHopperSpeed = 0.0;
		fKickerPosition = false;
		fTimeoutTimer = new Timer();
		fTimeoutTime = 0;
	}

	@Override
	public void initialize(String stateName, Config config) {
		sLogger.debug("Entering state {}", stateName);
		fHopperSpeed = config.getDouble("hopper_speed");
		fKickerPosition = config.getBoolean("kicker_position");
		fTimeoutTime = config.getInt("timeout_time");
		fTimeoutTimer.start(fTimeoutTime);
		fSharedOutputValues.setBoolean("opb_hopper_kicker", fKickerPosition);

	}

	@Override
	public void update() {

		if (fSharedInputValues.getBoolean("ipb_hopper_home_switch")) {
			fSharedOutputValues.setNumeric("opn_hopper", "percent", 0.0);
			fSharedInputValues.setBoolean("ipb_hopper_has_been_zeroed", true);
			sLogger.debug("Hopper zero -> Zeroed");

		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDone() {

	 if (fTimeoutTimer.isDone() && fSharedInputValues.getBoolean("ipb_hopper_has_been_zeroed")) {
		fSharedOutputValues.setNumeric("opn_hopper", "percent", 0.0);
		fSharedInputValues.setBoolean("ipb_hopper_zero_has_timed_out", true);
		sLogger.debug("Hopper Zero -> Timed out");
	}
		return fSharedInputValues.getBoolean("ipb_hopper_has_been_zeroed");
	}

	@Override
	public Set<String> getSubsystems() {
		return sSubsystems;
	}
}