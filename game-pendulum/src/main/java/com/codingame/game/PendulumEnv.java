package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;

public class PendulumEnv {
	public static final float TORQUE_MAX = 2;
	public static final float TORQUE_MIN = -2;
	public static final float SPEED_MAX = 8;
	public static final float OBJECTIVE_THETA_DEGREES = 1;
	public static final float OBJECTIVE_N_TURNS = 3;  // objective: keep abs(theta) < 1° during 3 turns 
	public static final float G = 10;
	public static final float DT = .05f;
	public static final float L = 1, M = 1;	
	
	private float theta, theta_dot;  // state
	private int n_turns_stable = 0;
	private float last_torque = 0;  // for rendering
	
	private GraphicEntityModule graph;
	private Text text;
	private RoundedRectangle rod;
	private Group pendulum;
	private Sprite img_torque;
	
	
	public PendulumEnv(GraphicEntityModule graph, float theta, float theta_dot){
		this.graph = graph;
		this.theta = theta;
		this.theta_dot = theta_dot;
		render_init();
		render();
	}
	
	private float _clip(float x, float low, float high) {
		return Math.min(Math.max(x, low),high);
	}
	
	private float _get_theta_degrees() {
		float theta_degrees = (float)Math.toDegrees(theta);
		return (theta_degrees + 180 + 360) % 360 - 180;  // \in [-180; 180]		
	}
	
	public void step(float torque) {
		torque = _clip(torque, TORQUE_MIN, TORQUE_MAX);
		last_torque = torque;
		float newthdot = (float) (theta_dot + (-3 * G / (2 * L) * Math.sin(theta + Math.PI) + 3. / (M * L * L) * torque) * DT);
		theta += newthdot * DT;
		theta_dot = _clip(newthdot, -SPEED_MAX, SPEED_MAX);
		
		if (Math.abs(_get_theta_degrees()) < OBJECTIVE_THETA_DEGREES)
			n_turns_stable++;
		else
			n_turns_stable = 0;
		render();
	}
	
	public boolean is_done() {
		return n_turns_stable >= OBJECTIVE_N_TURNS;		
	}
	
	public String get_obs(){		
		return (float)Math.cos(theta) + " " + (float)Math.sin(theta) + " " + (float)theta_dot;
	}
	
	private void render_init() {		
		// background
		int background_size = Constants.VIEWER_HEIGHT;		
		graph.createRectangle()
			.setWidth(background_size)
			.setHeight(background_size)
			.setX((int)(0.5 * Constants.VIEWER_WIDTH - 0.5 * background_size))
			.setFillColor(0xffffff);
		
		// pendulum		
		int origin_x = (int) Constants.VIEWER_WIDTH / 2;
		int origin_y = (int) Constants.VIEWER_HEIGHT / 2;
		double scale =  background_size / 2;		
		rod = graph.createRoundedRectangle()
			.setWidth((int)(scale))
			.setHeight((int)(.2*scale))
			.setRadius((int)(.1*scale))			
			.setFillColor(0xCC4C4C)			
			.setZIndex(1);
		rod.setX(-rod.getRadius());
		rod.setY(-rod.getRadius());
		graph.createCircle()  // axle				
			.setRadius((int)(.05*scale))
			.setX(origin_x)
			.setY(origin_y)			
			.setFillColor(0)
			.setZIndex(2);		
		pendulum = graph.createGroup(rod)
			.setX(origin_x)
			.setY(origin_y);
		
		// torque
		img_torque = graph.createSprite()
			.setImage("clockwise.png")	        
	        .setZIndex(3)
	        .setBaseWidth((int)(0.5 * scale))
	        .setBaseHeight((int)(0.5 * scale))
	        .setX(origin_x)
			.setY(origin_y);
					
		// debug
		text = graph.createText()
                .setX(10)
                .setY(10)
                .setAnchor(0)
                .setFontSize(40)
                .setFillColor(0xffffff);		
	}
	
	public void render() {
		pendulum
			.setRotation(- Math.PI / 2 - theta);
		
		double img_scaleX = -last_torque / TORQUE_MAX;
		double img_scaleY = Math.abs(last_torque) / TORQUE_MAX;
		double img_width = img_torque.getBaseWidth() * img_scaleX;
		double img_height = img_torque.getBaseHeight() * img_scaleY;
		img_torque
			.setScaleX(img_scaleX)
			.setScaleY(img_scaleY)
			.setX(pendulum.getX() - (int)(img_width / 2))
			.setY(pendulum.getY() - (int)(img_height / 2));
				
		text
			.setText("theta = " + _get_theta_degrees() + "°\n"
					+ "OK since : " + n_turns_stable + " turns\n");
		
	}
	
	
			
	
	
}
