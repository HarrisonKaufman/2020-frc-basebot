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
 * Zeros the collector subsystem
 */

public class Collector_Zero implements Behavior {

    private static final Logger sLogger = LogManager.getLogger(Collector_Zero.class);
    private static final Set<String> sSubsystems = Set.of("ss_collector");

    private final InputValues fSharedInputValues;
    private final OutputValues fSharedOutputValues;
    private final Timer fDelayTimer;
    private final int fDelayTime;

    private boolean mSolenoidPosition;

    public Collector_Zero(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        fSharedInputValues = inputValues;
        fSharedOutputValues = outputValues;
        fDelayTimer = new Timer();
        fDelayTime = config.getInt("delay_time");

        mSolenoidPosition = false;
    }

    @Override
    public void initialize(String stateName, Config config) {
        sLogger.debug("Entering state {}", stateName);

        mSolenoidPosition = config.getBoolean("solenoid_position", false);

        fDelayTimer.start(fDelayTime);
        fSharedOutputValues.setNumeric("opn_collector_rollers", "percent", 0.0);
        fSharedOutputValues.setBoolean("opb_collector_extend", mSolenoidPosition);
        fSharedInputValues.setBoolean("ipb_collector_solenoid_position", mSolenoidPosition);
    }

    @Override
    public void update() {


            if (fDelayTimer.isDone()) {
                fSharedInputValues.setBoolean("ipb_collector_has_been_zeroed", true);
                sLogger.debug("Collector Zero -> Zeroed");
                fDelayTimer.reset();

            }
        }


    @Override
    public void dispose() {

    }

    @Override
    public boolean isDone() {
        return fSharedInputValues.getBoolean("ipb_collector_has_been_zeroed");
    }

    @Override
    public Set<String> getSubsystems() {
        return sSubsystems;
    }
}