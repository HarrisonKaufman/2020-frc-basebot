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



public class Elevator_Zero implements Behavior {

    private static final Logger sLogger = LogManager.getLogger(Elevator_Zero.class);
    private static final Set<String> sSubsystems = Set.of("ss_elevator");

    private final InputValues fSharedInputValues;
    private final OutputValues fSharedOutputValues;
    private final Timer fTimeoutTimer;

    private int mTimeoutTime;
    public Elevator_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        fSharedInputValues = inputValues;
        fSharedOutputValues = outputValues;
        fTimeoutTimer = new Timer();

        mTimeoutTime = 0;
    }

    @Override
    public void initialize(String stateName, Config config) {
        sLogger.debug("Entering state {}", stateName);

        mTimeoutTime = config.getInt("zeroing_timeout_time");

        fTimeoutTimer.reset();
        fTimeoutTimer.start(mTimeoutTime);
    }

    @Override
    public void update() {
        if (!fSharedInputValues.getBoolean("ipb_elevator_has_been_zeroed")) {
            fSharedOutputValues.setOutputFlag("opn_elevator", "zero");

            if (Math.abs(fSharedInputValues.getNumeric("ipn_elevator_position")) == 0.0) {
                sLogger.debug("Elevator Zero -> Zeroed");
                fSharedInputValues.setBoolean("ipb_elevator_has_been_zeroed", true);
            }
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDone() {
        if (fTimeoutTimer.isDone() && !fSharedInputValues.getBoolean("ipb_elevator_has_been_zeroed")) {
            fTimeoutTimer.reset();
            sLogger.error("Elevator Zero -> Timed Out");
        }
        return fSharedInputValues.getBoolean("ipb_elevator_has_been_zeroed") || fTimeoutTimer.isDone();
    }

    @Override
    public Set<String> getSubsystems() {
        return sSubsystems;
    }
}