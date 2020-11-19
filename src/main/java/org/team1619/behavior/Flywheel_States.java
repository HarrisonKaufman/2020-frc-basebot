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
 * States for the flywheel behavior
 */

public class Flywheel_States implements Behavior {

    private static final Logger sLogger = LogManager.getLogger(Flywheel_States.class);
    private static final Set<String> sSubsystems = Set.of("ss_flywheel");

    private final InputValues fSharedInputValues;
    private final OutputValues fSharedOutputValues;

    private String mIncrementButton;
    private String mDecrementButton;

    private double mSpeed;
    private double mVelocity;
    private double mVelocityAdjust;
    private double mUpdatedVelocity;
    private String mVelocityProfile;
    private boolean mPrime;
    private double mUpToVelocity;
    private boolean mCoasting;
    private boolean mAllowAdjust;
    private double mIncrementAmount;

    public Flywheel_States(InputValues inputValues, OutputValues outputValues, Config config, RobotConfiguration robotConfiguration) {
        fSharedInputValues = inputValues;
        fSharedOutputValues = outputValues;

        mIncrementButton = robotConfiguration.getString("global_flywheel", "increment_button");
        mDecrementButton = robotConfiguration.getString("global_flywheel", "decrement_button");

        mSpeed = 0.0;
        mVelocity = 0;
        mVelocityProfile = "none";
        mVelocityAdjust = 0;
        mUpdatedVelocity = 0;
        mPrime = false;
        mUpToVelocity = 0;
        mCoasting = false;
        mAllowAdjust = false;
        mIncrementAmount = 0.0;

    }

    @Override
    public void initialize(String stateName, Config config) {
        sLogger.debug("Entering state {}", stateName);

        mSpeed = config.getDouble("speed", 0.0);
        mVelocity = config.getInt("velocity", 0);
        mVelocityProfile = config.getString("velocity_profile", "none");
        mPrime = config.getBoolean("prime", false);
        mUpToVelocity = config.getInt("up_to_velocity", 0);
        mCoasting = config.getBoolean("coast", false);
        mAllowAdjust = config.getBoolean("allow_adjust", false);
        mIncrementAmount = config.getDouble("increment_amount",0 );
    }

    @Override
    public void update() {
        String mOutputType = "";

        if (mPrime){
            if (fSharedInputValues.getNumeric("ipn_flywheel_primary_velocity") > mUpToVelocity){
                mOutputType = "velocity";
                mUpdatedVelocity = mVelocity;
            }
            else{
                mOutputType = "percent";
                mUpdatedVelocity = mSpeed;
            }
        }
        if (mCoasting){
            mOutputType = "percent";
            mUpdatedVelocity = 0;
        }
        if (!mPrime && !mCoasting){
            mOutputType = "velocity";
            mUpdatedVelocity = mVelocity;
        }

        if ((mAllowAdjust) && (mOutputType.equals("velocity"))){
            boolean decrementButton = fSharedInputValues.getBooleanRisingEdge(mIncrementButton);
            boolean incrementButton = fSharedInputValues.getBooleanRisingEdge(mDecrementButton);

            if (incrementButton){
                mVelocityAdjust += mIncrementAmount;
            }
            else if (decrementButton){
                mVelocityAdjust -= mIncrementAmount;
            }

            mUpdatedVelocity = mVelocity + mVelocityAdjust;

            if (mUpdatedVelocity <= -1000){
                mUpdatedVelocity = -1000;
            }
            else if (mUpdatedVelocity >= 8000){
                mUpdatedVelocity = 8000;
            }
        }

        if (mVelocityProfile.equals("none")){
            mOutputType = "percent";
        }

        fSharedOutputValues.setNumeric("opn_flywheel", mOutputType, mUpdatedVelocity, mVelocityProfile);
    }

    @Override
    public void dispose() {
        fSharedOutputValues.setNumeric("opn_flywheel", "percent", 0.0);
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