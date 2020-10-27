package org.team1619.modelfactory;

import org.team1619.behavior.*;
import org.uacr.models.behavior.Behavior;
import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.robot.AbstractModelFactory;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.ObjectsDirectory;
import org.uacr.shared.abstractions.OutputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.Config;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

public class ModelFactory_Behaviors extends AbstractModelFactory {

	private static final Logger sLogger = LogManager.getLogger(ModelFactory_Behaviors.class);

	private final InputValues fSharedInputValues;
	private final OutputValues fSharedOutputValues;
	private final RobotConfiguration fRobotConfiguration;

	public ModelFactory_Behaviors(InputValues inputValues, OutputValues outputValues, RobotConfiguration robotConfiguration, ObjectsDirectory objectsDirectory) {
		super(inputValues, outputValues, robotConfiguration, objectsDirectory);
		fSharedInputValues = inputValues;
		fSharedOutputValues = outputValues;
		fRobotConfiguration = robotConfiguration;
	}

	public Behavior createBehavior(String name, Config config) {
		sLogger.trace("Creating behavior '{}' of type '{}' with config '{}'", name, config.getType(), config.getData());

		switch (name) {

			case "bh_drivetrain_percent":
				return new Drivetrain_Percent(fSharedInputValues, fSharedOutputValues, config, fRobotConfiguration);
			// State not found

			case "bh_collector_zero":
				return new Collector_Zero(fSharedInputValues, fSharedOutputValues, config, fRobotConfiguration);
			case "bh_collector_states":
				return new Collector_States(fSharedInputValues, fSharedOutputValues, config, fRobotConfiguration);

			case "bh_hopper_zero":
				return new Hopper_Zero(fSharedInputValues, fSharedOutputValues, config, fRobotConfiguration);
			case "bh_hopper_states":
				return new Hopper_States(fSharedInputValues, fSharedOutputValues, config, fRobotConfiguration);
			default:
				throw new ConfigurationException("Behavior " + name + " does not exist.");
		}
	}

}