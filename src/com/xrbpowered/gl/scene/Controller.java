package com.xrbpowered.gl.scene;

import static java.awt.event.KeyEvent.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.DoubleBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

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
	
	public boolean forceForward = false;
	public boolean canStrafe = true;
	public boolean limitRotation = false;
	
	private boolean mouseLook = false;
	private boolean cursorReset = false;
	private boolean lookController = false;
	
	private Actor actor = null;
	
	public Controller(ClientInput input) {
		this.input = input;
	}
	
	public Controller setActor(Actor actor) {
		this.actor = actor;
		return this;
	}
	
	public Controller setMouseLook(boolean enable) {
		if(mouseLook==enable)
			return this;
		this.mouseLook = enable;
		input.enableMouseEvents = !enable;
		glfwSetInputMode(input.window, GLFW_CURSOR, mouseLook ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
		cursorReset = false;
		return this;
	}
	
	public Controller setLookController(boolean look) {
		this.lookController = look;
		return this;
	}
	
	private static final Vector4f v = new Vector4f(0, 0, 0, 1);
	private static final Matrix4f m = new Matrix4f();
	
	protected void applyVelocity(Vector3f position, Vector4f v) {
		actor.position.x += v.x;
		actor.position.y += v.y;
		actor.position.z += v.z;
	}
	
	public void update(float dt) {
		if(actor==null)
			return;
		float moveDelta = moveSpeed * dt;
		float rotateDelta = keyboardRotateSpeed * dt;

		v.set(0, 0, 0, 1);
		if(input.isKeyDown(keyForward) || forceForward)
			v.z += moveDelta;
		if(input.isKeyDown(keyBack) && !forceForward)
			v.z -= moveDelta;
		if(input.isKeyDown(keyStrafeLeft) && canStrafe)
			v.x += moveDelta;
		if(input.isKeyDown(keyStrafeRight) && canStrafe)
			v.x -= moveDelta;
		if(input.isKeyDown(keyFlyDown) && canStrafe)
			v.y += moveDelta;
		if(input.isKeyDown(keyFlyUp) && canStrafe)
			v.y -= moveDelta;
		
		if(lookController) {
			v.negate();
			m.identity();
			Actor.rotateYawPitchRoll(actor.rotation, m);
			m.transform(v);
		}
		applyVelocity(actor.position, v);

		v.set(0, 0, 0, 1);
		if(input.isKeyDown(keyLookUp))
			v.x += rotateDelta;
		if(input.isKeyDown(keyLookDown))
			v.x -= rotateDelta;
		if(input.isKeyDown(keyTurnLeft))
			v.y += rotateDelta;
		if(input.isKeyDown(keyTurnRight))
			v.y -= rotateDelta;

		if(mouseLook) {
			try(MemoryStack stack = stackPush()) {
				if(cursorReset) {
					DoubleBuffer pX = stack.mallocDouble(1);
					DoubleBuffer pY = stack.mallocDouble(1);
					glfwGetCursorPos(input.window, pX, pY);
					double mx = pX.get(0);
					double my = pY.get(0);
					// System.out.printf("%.1f %.1f\n", mx, my);
					v.y -= mx * mouseSensitivity;
					v.x -= my * mouseSensitivity;
				}
				glfwSetCursorPos(input.window, 0, 0);
				cursorReset = true;
			}
		}
		
		actor.rotation.x += v.x;
		/*if(limitRotation) {
			actor.rotation.x = (float) MathUtils.snap(actor.rotation.x, -Math.PI/2.0, Math.PI/2.0);
		}*/
		actor.rotation.y += v.y;
		
		actor.updateTransform();
	}
	
}
