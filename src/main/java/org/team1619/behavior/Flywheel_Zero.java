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
 * Zeroes Flywheel in Sequence Mode
 */

public class Flywheel_Zero implements Behavior {

    private static final Logger sLogger = LogManager.getLogger(Flywheel_Zero.class);
    private static final Set<String> sSubsystems = Set.of("ss_flywheel");

    private final InputValues fSharedInputValues;
    private final OutputValues fSharedOutputValues;
    private final Timer fTimeoutTimer;

    private int mTimeoutTime;

    public Flywheel_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        fSharedInputValues = inputValues;
        fSharedOutputValues = outputValues;
        fTimeoutTimer = new Timer();

        mTimeoutTime = 0;
    }

    @Override
    public void initialize(String stateName, Config config) {
        sLogger.debug("Entering state {}", stateName);

        mTimeoutTime = config.getInt("timeout_time");
        fTimeoutTimer.reset();
        fTimeoutTimer.start(mTimeoutTime);
        fSharedInputValues.setBoolean("ipb_flywheel_primed", false);
        fSharedOutputValues.setNumeric("opn_flywheel", "percent", 0.0);
    }

    @Override
    public void update() {
        if (!fSharedInputValues.getBoolean("ipb_flywheel_has_been_zeroed")) {
            fSharedOutputValues.setOutputFlag("opn_flywheel", "zero");
            fSharedInputValues.setBoolean("ipb_flywheel_has_been_zeroed", true);
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isDone() {
        return (fSharedInputValues.getBoolean("ipb_flywheel_has_been_zeroed") || fTimeoutTimer.isDone());
    }

    @Override
    public Set<String> getSubsystems() {
        return sSubsystems;
    }
}