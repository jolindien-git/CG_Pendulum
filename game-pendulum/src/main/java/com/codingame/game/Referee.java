package com.codingame.game;

import java.util.List;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
import com.codingame.gameengine.core.SoloGameManager;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    private PendulumEnv env;

    @Override
    public void init() {
		gameManager.setFrameDuration(Constants.FRAME_DURATION);
		gameManager.setMaxTurns(Constants.MAX_TURN);
		gameManager.setTurnMaxTime(Constants.TURN_MAX_TIME);
		
		String input = gameManager.getTestCaseInput().get(0);
		String[] inputs = input.split(";");
		float theta = Float.parseFloat(inputs[0]);
		float theta_dot = Float.parseFloat(inputs[1]);
		env = new PendulumEnv(graphicEntityModule, theta, theta_dot);
		
    }

    @Override
    public void gameTurn(int turn) {    	
    	gameManager.getPlayer().sendInputLine(env.get_obs());
    	
    	gameManager.getPlayer().execute();

        try {
        	List<String> outputs = gameManager.getPlayer().getOutputs();
			Float torque = Float.parseFloat(outputs.get(0));
			if (torque < PendulumEnv.TORQUE_MIN || torque > PendulumEnv.TORQUE_MAX) {
				gameManager.addToGameSummary("Invalid torque. Please keep between " + PendulumEnv.TORQUE_MIN
										+ " and " + PendulumEnv.TORQUE_MAX);
                throw new Exception("Invalid thrust");
			}
			env.step(torque);
			if (env.is_done())
				gameManager.winGame();
        } 
        catch (TimeoutException e) {
            gameManager.loseGame("Timeout!");
			gameManager.addTooltip(gameManager.getPlayer(), gameManager.getPlayer().getNicknameToken() + " timeout!");
        }
        catch (Exception e) {
        	gameManager.loseGame(e.getMessage());
        	gameManager.addTooltip(gameManager.getPlayer(), gameManager.getPlayer().getNicknameToken() + e.getMessage());
        }
    }		
}
