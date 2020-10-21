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


public class Elevator_States implements Behavior {

    private static final Logger sLogger = LogManager.getLogger(Elevator_States.class);
    private static final Set<String> sSubsystems = Set.of("ss_elevator");

    private final InputValues fSharedInputValues;
    private final OutputValues fSharedOutputValues;
    private final Timer fIsDoneTimer;
    private final double fElevatorIncrement;
    private final String fIncrementUpButton;
    private final String fIncrementDownButton;

    private boolean mBeamSensor;
    private double mElevatorSpeed;
    private double mCurrentSpeed;
    private double mInitialSpeed;
    private String mStateName;

    public Elevator_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        fSharedInputValues = inputValues;
        fSharedOutputValues = outputValues;
        fIsDoneTimer = new Timer();

        mBeamSensor = false;
        mElevatorSpeed = 0.0;
        mStateName = "";

        fIncrementUpButton = robotConfiguration.getString("global_elevator", "increment_up_button");
        fIncrementDownButton = robotConfiguration.getString("global_elevator", "increment_down_button");
        fElevatorIncrement = robotConfiguration.getDouble("global_elevator", "increment");
    }

    @Override
    public void initialize(String stateName, Config config) {
        sLogger.debug("Entering state {}", stateName);

        mBeamSensor = config.getBoolean("beam_sensor", false);
        mElevatorSpeed = config.getDouble("speed");
        mStateName = stateName;

        fIsDoneTimer.start(1000);

       mCurrentSpeed = fSharedInputValues.getNumeric("opn_elevator");
    }

    @Override
    public void update() {

        boolean incrementUpButton = fSharedInputValues.getBooleanRisingEdge(fIncrementUpButton);
        boolean incrementDownButton = fSharedInputValues.getBooleanRisingEdge(fIncrementDownButton);

        if (incrementUpButton) {
            mCurrentSpeed += fElevatorIncrement;
        }
        if (incrementDownButton) {
            mCurrentSpeed -= fElevatorIncrement;
        }
        if (mCurrentSpeed >= 1) {
            mCurrentSpeed = 1;
        }
        if (mCurrentSpeed <= -1) {
            mCurrentSpeed = -1;
        }
        fSharedInputValues.setNumeric("opn_elevator", mCurrentSpeed);
    }

    @Override
    public void dispose() {
        sLogger.trace("Leaving state {}", mStateName);

        fSharedOutputValues.setNumeric("opn_elevator", "percent", 0.0);
    }

    @Override
    public boolean isDone() {
        if (mBeamSensor) {
            return fSharedInputValues.getBoolean("ipb_elevator_beam_sensor");
        } else {
            return true;
        }
    }

    @Override
    public Set<String> getSubsystems() {
        return sSubsystems;
    }
}