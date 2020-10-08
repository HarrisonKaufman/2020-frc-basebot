package org.team1619.state.modelogic;

import org.uacr.models.state.State;
import org.uacr.robot.AbstractModeLogic;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

/**
 * Handles the isReady and isDone logic for teleop mode on competition bot
 */

public class TeleopModeLogic extends AbstractModeLogic {

	private static final Logger sLogger = LogManager.getLogger(TeleopModeLogic.class);

	boolean mIsStowed = false;
	boolean mRollersOn= false;

	private boolean mFloorCollect = false;
	private boolean mPrime = false;
	private boolean mShoot = false;
	private boolean mProtect = false;







	public TeleopModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);
	}

	@Override
	public void initialize() {
		sLogger.info("***** TELEOP *****");

		mFloorCollect = false;
		mPrime = false;
		mShoot = false;
		mProtect = false;


		}




	@Override
	public void update() {


		 if(fSharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper")) {

			mRollersOn = false;
			mIsStowed = true;


		}

		if(fSharedInputValues.getBooleanRisingEdge("ipb_operator_left_trigger") ) {

			mRollersOn = !mRollersOn;
			mIsStowed = false;

		}

			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_left_trigger")) {
				mFloorCollect = !mFloorCollect;
				mPrime = false;
				mShoot = false;
				mProtect = false;
			}
			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper")) {
				mFloorCollect = false;
				mPrime = false;
				mShoot = false;
				mProtect = true;
			}
			if (fSharedInputValues.getBooleanFallingEdge("ipb_operator_right_trigger")) {
				mFloorCollect = false;
				mPrime = false;
				mShoot = true;
				mProtect = false;
			}

			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_right_bumper")) {
				mFloorCollect = false;
				mPrime = true;
				mShoot = false;
				mProtect = false;
			}


		}





	@Override
	public void dispose() {

	}

	@Override
	public boolean isReady(String name) {
		switch (name) {

			//collector

			case "st_drivetrain_percent":
				return true;

			case "st_collector_zero":
				return !fSharedInputValues.getBoolean("ipb_collector_has_been_zeroed");

			case "st_hopper_zero":
				return !fSharedInputValues.getBoolean("ipb_hopper_has_been_zeroed");

			case"st_elevator_zero":
				return !fSharedInputValues.getBoolean("ipb_elevator_has_been_zeroed");

			case "pl_floor_intake":
				return mFloorCollect;

			case "pl_prime":
				return mPrime;

			case "pl_shoot":
				return mShoot;

			case "pl_protect":
				return mProtect;






			default:
				return false;
		}
	}

	@Override
	public boolean isDone(String name, State state) {
		switch (name) {

			case "pl_floor_intake":
				return !mFloorCollect;

			case "pl_prime":
				return !mPrime;

			case "pl_shoot":
				return !mShoot;

			case "pl_protect":
				return !mProtect;


			default:
				return state.isDone();
		}
	}
}
