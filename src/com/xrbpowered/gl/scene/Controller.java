package com.xrbpowered.gl.scene;

import static java.awt.event.KeyEvent.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.xrbpowered.gl.client.ClientInput;

public class Controller {

	public final ClientInput input;
	
	public Integer keyForward = VK_W;
	public Integer keyBack = VK_S;
	public Integer keyStrafeLeft = VK_A;
	public Integer keyStrafeRight = VK_D;
	public Integer keyFlyDown = VK_SHIFT;
	public Integer keyFlyUp = VK_SPACE;
	public Integer keyLookUp = VK_UP;
	public Integer keyLookDown = VK_DOWN;
	public Integer keyTurnLeft = VK_LEFT;
	public Integer keyTurnRight = VK_RIGHT;
	
	public float moveSpeed = 10.0f;
	public float keyboardRotateSpeed = (float)(Math.PI/2f);
	public float mouseSensitivity = 0.002f;
	
	private boolean mouseLook = false;
	
	protected Actor actor = null;
	protected Vector3f velocity = new Vector3f();
	
	protected Vector4f moveVectorScale = new Vector4f(1f, 1f, 1f, 1f); /* strafe, fly, fwd, back */
	public boolean lookAlign = true;
	
	public Controller(ClientInput input) {
		this.input = input;
	}
	
	public Controller setActor(Actor actor) {
		this.actor = actor;
		return this;
	}
	
	public Controller setMouseLook(boolean enable) {
		this.mouseLook = enable;
		input.setDeltaInput(enable);
		return this;
	}
	
	public boolean isMouseLook() {
		return mouseLook;
	}
	
	protected void updateMove(Vector3f move) {
		if(input.isKeyDown(keyForward))
			move.z += 1;
		if(input.isKeyDown(keyBack))
			move.z -= 1;
		if(input.isKeyDown(keyStrafeLeft))
			move.x += 1;
		if(input.isKeyDown(keyStrafeRight))
			move.x -= 1;
		if(input.isKeyDown(keyFlyDown))
			move.y += 1;
		if(input.isKeyDown(keyFlyUp))
			move.y -= 1;
		
		if(move.length()>0f)
			move.normalize();
		
		move.x *= moveVectorScale.x;
		move.y *= moveVectorScale.y;
		move.z *= move.z>0f ? moveVectorScale.z : moveVectorScale.w;
	}
	
	private final Vector4f v4 = new Vector4f();
	private final Matrix4f m = new Matrix4f();
	
	protected void lookAlign(Vector3f move, Vector3f rotation) {
		move.negate();
		m.identity();
		// Actor.rotateYawPitchRoll(rotation, m);
		m.rotateZYX(rotation);
		v4.set(move, 0);
		m.transform(v4);
		move.set(v4.x, v4.y, v4.z);
	}
	
	protected void updateVelocity(Vector3f move, float dt) {
		velocity.set(move.mul(moveSpeed * dt));
	}
	
	protected void applyVelocity(float dt) {
		actor.position.x += velocity.x;
		actor.position.y += velocity.y;
		actor.position.z += velocity.z;
	}
	
	protected void addMouseLook(Vector2f look) {
		if(mouseLook)
			input.addDeltaInput(look, -mouseSensitivity);
	}
	
	protected void updateTurn(Vector2f turn, float dt) {
		float rotateDelta = keyboardRotateSpeed * dt;
		if(input.isKeyDown(keyLookUp))
			turn.x += rotateDelta;
		if(input.isKeyDown(keyLookDown))
			turn.x -= rotateDelta;
		if(input.isKeyDown(keyTurnLeft))
			turn.y += rotateDelta;
		if(input.isKeyDown(keyTurnRight))
			turn.y -= rotateDelta;
		addMouseLook(turn);
	}
	
	protected void applyRotation(Vector2f turn) {
		actor.rotation.x += turn.x;
		actor.rotation.y += turn.y;
	}
	
	private final Vector3f move = new Vector3f();
	private final Vector2f turn = new Vector2f();
	
	public void update(float dt) {
		if(actor==null)
			return;

		move.zero();
		updateMove(move);
		if(lookAlign)
			lookAlign(move, actor.rotation);
		updateVelocity(move, dt);
		applyVelocity(dt);

		turn.zero();
		updateTurn(turn, dt);
		/*if(limitRotation) {
			actor.rotation.x = (float) MathUtils.snap(actor.rotation.x, -Math.PI/2.0, Math.PI/2.0);
		}*/
		applyRotation(turn);
		
		actor.updateTransform();
	}
	
}
