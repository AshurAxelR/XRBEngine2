package com.xrbpowered.gl.scene;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.xrbpowered.gl.client.ClientInput;

public class WalkController extends Controller {

	public float lookLimiter = (float)Math.toRadians(80f);
	
	public WalkController(ClientInput input) {
		super(input);
		keyFlyUp = null;
		keyFlyDown = null;
		moveVectorScale.set(0.8f, 0f, 1f, 0.6f);
	}
	
	private Vector3f r = new Vector3f(0, 0, 0);
	
	@Override
	protected void lookAlign(Vector3f move, Vector3f rotation) {
		r.y = rotation.y;
		super.lookAlign(move, r);
	}
	
	@Override
	protected void applyRotation(Vector2f turn) {
		super.applyRotation(turn);
		if(actor.rotation.x<-lookLimiter)
			actor.rotation.x = -lookLimiter;
		if(actor.rotation.x>lookLimiter)
			actor.rotation.x = lookLimiter;
	}

}
