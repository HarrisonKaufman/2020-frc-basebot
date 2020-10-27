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

	private boolean floorCollect = false;
	private boolean prime = false;
	private boolean shoot = false;
	private boolean protect = false;







	public TeleopModeLogic(InputValues inputValues, RobotConfiguration robotConfiguration) {
		super(inputValues, robotConfiguration);
	}

	@Override
	public void initialize() {
		sLogger.info("***** TELEOP *****");

		floorCollect = false;
		prime = false;
		shoot = false;
		protect = false;


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
				floorCollect = !floorCollect;
				prime = false;
				shoot = false;
				protect = false;
			}
			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_left_bumper")) {
				floorCollect = false;
				prime = false;
				shoot = false;
				protect = true;
			}
			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_right_trigger")) {
				floorCollect = false;
				prime = false;
				shoot = true;
				protect = false;
			}
			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_right_trigger")){
				floorCollect = false;
				prime = false;
				shoot = false;
				protect = false;
			}
			if (fSharedInputValues.getBooleanRisingEdge("ipb_operator_right_bumper")) {
				floorCollect = false;
				prime = true;
				shoot = false;
				protect = false;
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

			case "st_collector_retract":
				return mIsStowed && !mRollersOn;

			case "st_collector_floor_intake":
				return !mIsStowed && mRollersOn;

			case "st_collector_extend":
				return !mIsStowed && !mRollersOn;

			case "st_hopper_zero":
				return !fSharedInputValues.getBoolean("ipb_hopper_has_been_zeroed");

			case "pl_floor_intake":
				return floorCollect && !prime && !shoot && !protect;

			case "pl_prime":
				return !floorCollect && prime && !shoot && !protect;

			case "pl_shoot":
				return !floorCollect && !prime && shoot && !protect;

			case "pl_protect":
				return !floorCollect && !prime && !shoot && protect;






			default:
				return false;
		}
	}

	@Override
	public boolean isDone(String name, State state) {
		switch (name) {

			case "pl_floor_intake":
				return !floorCollect;

			case "pl_prime":
				return !prime;

			case "pl_shoot":
				return !shoot;

			case "pl_protect":
				return !protect;


			default:
				return state.isDone();
		}
	}
}
