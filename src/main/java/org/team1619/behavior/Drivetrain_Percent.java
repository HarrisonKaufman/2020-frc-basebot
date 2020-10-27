package org.team1619.behavior;

import org.uacr.models.behavior.Behavior;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Set;

/**
 * Example behavior to copy for other behaviors
 */

public class Drivetrain_Percent implements Behavior {

	private static final Logger sLogger = LogManager.getLogger(Drivetrain_Percent.class);
	private static final Set<String> sSubsystems = Set.of("ss_drivetrain");

	private final InputValues fSharedInputValues;
	private final OutputValues fSharedOutputValues;
	private String fStateName;
	private String fXAxis;
	private String fYAxis;
	private String fGearShiftButton;

	public Drivetrain_Percent(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
		fSharedInputValues = inputValues;
		fSharedOutputValues = outputValues;
		fStateName = "Unknown";
		fXAxis = robotConfiguration.getString("global_drivetrain", "x");
		fYAxis = robotConfiguration.getString("global_drivetrain", "y");
		fGearShiftButton = robotConfiguration.getString("global_drivetrain", "gear_shift_button");
	}

	@Override
	public void initialize(String stateName, Config config) {
		sLogger.debug("Entering state {}", stateName);
		fStateName = stateName;
	}

	@Override
	public void update() {
		double xAxis = fSharedInputValues.getNumeric(fXAxis);
		double yAxis = fSharedInputValues.getNumeric(fYAxis);
		boolean gearShiftButtonValue = fSharedInputValues.getBoolean(fGearShiftButton);


		// Set the motor speed to the joystick values
		double leftMotorSpeed = yAxis + xAxis;
		double rightMotorSpeed = yAxis - xAxis;

		fSharedOutputValues.setNumeric("opn_drivetrain_left", "percent", leftMotorSpeed);
		fSharedOutputValues.setNumeric("opn_drivetrain_right", "percent", rightMotorSpeed);


		fSharedOutputValues.setBoolean("opb_drivetrain_gear_shifter", gearShiftButtonValue);

	}


	@Override
	public void dispose() {
		sLogger.trace("Leaving state {}", fStateName);
		fSharedOutputValues.setNumeric("opn_drivetrain_left", "percent", 0.0);
		fSharedOutputValues.setNumeric("opn_drivetrain_right", "percent", 0.0);
		fSharedOutputValues.setBoolean("opb_drivetrain_gear_shifter", false);
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public Set<String> getSubsystems() {
		return sSubsystems;
	}
}

