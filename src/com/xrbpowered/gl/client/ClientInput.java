package com.xrbpowered.gl.client;

import static java.awt.event.KeyEvent.*;
import static org.lwjgl.glfw.GLFW.*;

import java.util.HashSet;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.MouseInfo;

public class ClientInput {

	public final Client client;
	private long window;
	
	private float mouseX, mouseY;
	private HashSet<Integer> pressedMouseButtons = new HashSet<>();
	private int mouseButtons = MouseInfo.NONE;
	private boolean deltaInput = false;
	private boolean cursorReset = false;

	private int keyMods = 0;
	private int keyCode = 0;
	private HashSet<Integer> pressedKeys = new HashSet<>();

	private GLFWCursorPosCallbackI cursorPosCallback = new GLFWCursorPosCallbackI() {
		@Override
		public void invoke(long window, double xpos, double ypos) {
			mouseX = (float) xpos;
			mouseY = (float) ypos;
			if(!deltaInput)
				client.mouseMoved(mouseX, mouseY);
		}
	};
	
	private GLFWMouseButtonCallbackI mouseButtonCallback = new GLFWMouseButtonCallbackI() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			if(action==GLFW_PRESS) {
				pressedMouseButtons.add(button);
				mouseButtons |= getMouseButton(button);
				if(deltaInput)
					client.mouseDown(-1, -1, button);
				else
					client.mouseDown(mouseX, mouseY, button);
			}
			else {
				pressedMouseButtons.remove(button);
				mouseButtons &= ~getMouseButton(button);
				if(deltaInput)
					client.mouseUp(-1, -1, button);
				else
					client.mouseUp(mouseX, mouseY, button);
			}
		}
	};
	
	private GLFWScrollCallbackI mouseScrollCallback = new GLFWScrollCallbackI() {
		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			if(deltaInput)
				client.mouseScroll(-1, -1, (float) -yoffset);
			else
				client.mouseScroll(mouseX, mouseY, (float) -yoffset);
		}
	};
	
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			keyMods = 0;
			if((mods&GLFW.GLFW_MOD_SHIFT)!=0)
				keyMods |= InputInfo.SHIFT;
			if((mods&GLFW.GLFW_MOD_CONTROL)!=0)
				keyMods |= InputInfo.CTRL;
			if((mods&GLFW.GLFW_MOD_ALT)!=0)
				keyMods |= InputInfo.ALT;

			int code = keyToCode(key);
			if(code!=0) {
				if(action==GLFW_PRESS)
					pressedKeys.add(code);
				else if(action==GLFW_RELEASE)
					pressedKeys.remove(code);
			}
			if(action==GLFW_PRESS || action==GLFW_REPEAT) {
				pushKeyCode(code);
			}
		}
	};
	
	private GLFWCharCallback charCallback = new GLFWCharCallback() {
		@Override
		public void invoke(long window, int codepoint) {
			pushChar((char) codepoint);
		}
	};
	
	public ClientInput(Client client) {
		this.client = client;
	}
	
	public void registerCallbacks(long window) {
		this.window = window;
		glfwSetCursorPosCallback(window, cursorPosCallback);
		glfwSetMouseButtonCallback(window, mouseButtonCallback);
		glfwSetScrollCallback(window, mouseScrollCallback);
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCharCallback(window, charCallback);
		
		pressedMouseButtons.clear();
		keyMods = 0;
		keyCode = 0;
		pressedKeys.clear();
	}
	
	public long getWindow() {
		return window;
	}
	
	public void pollEvents() {
		glfwPollEvents();
		pushKeyCode(0);
	}
	
	private void pushKeyCode(int code) {
		if(keyCode>0)
			client.keyPressed('\0', keyCode);
		keyCode = code;
	}
	
	private void pushChar(char c) {
		if(keyCode>0)
			client.keyPressed(c, keyCode);
		keyCode = 0;
	}
	
	public void setDeltaInput(boolean enable) {
		if(deltaInput!=enable) {
			this.deltaInput = enable;
			glfwSetInputMode(window, GLFW_CURSOR, enable ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
			cursorReset = false;
		}
	}
	
	public Vector2f addDeltaInput(Vector2f look, float scale) {
		if(deltaInput) {
			if(cursorReset) {
				look.y += mouseX * scale;
				look.x += mouseY * scale;
			}
			resetMousePos();
		}
		return look;
	}
	
	public void setMousePos(float x, float y) {
		glfwSetCursorPos(window, x, y);
		mouseX = x;
		mouseY = y;
	}
	
	public void resetMousePos() {
		setMousePos(0, 0);
		cursorReset = true;
	}

	public boolean isMouseDown() {
		return !pressedMouseButtons.isEmpty();
	}
	
	public boolean isMouseDown(Integer button) {
		return button!=null && pressedMouseButtons.contains(button);
	}
	
	public int getKeyMods() {
		return keyMods;
	}
	
	public float getMouseX() {
		return mouseX;
	}

	public float getMouseY() {
		return mouseY;
	}
	
	public boolean isKeyDown(Integer code) {
		return code!=null && pressedKeys.contains(code);
	}

	public boolean isAnyKeyDown() {
		return !pressedKeys.isEmpty();
	}
	
	public InputInfo getInputInfo() {
		return new InputInfo(keyMods);
	}
	
	public static int getMouseButton(int button) {
		if(button>4)
			return MouseInfo.UNKNOWN;
		else if(button>=0)
			return 1 << button;
		else
			return MouseInfo.NONE;
	}
	
	public MouseInfo getMouseInfo(int button) {
		return new MouseInfo(getMouseButton(button), mouseButtons, keyMods);
	}

	public MouseInfo getMouseInfo() {
		return new MouseInfo(-1, mouseButtons, keyMods);
	}
	
	public static final int[] fkeyMap = {
		/*256*/ VK_ESCAPE, VK_ENTER, VK_TAB, VK_BACK_SPACE, VK_INSERT, VK_DELETE, VK_RIGHT, VK_LEFT,
		/*264*/ VK_DOWN, VK_UP, VK_PAGE_UP, VK_PAGE_DOWN, VK_HOME, VK_END, 0, 0,
		/*272*/ 0, 0, 0, 0, 0, 0, 0, 0,
		/*280*/ VK_CAPS_LOCK, VK_SCROLL_LOCK, VK_NUM_LOCK, VK_PRINTSCREEN, VK_PAUSE, 0, 0, 0,
		/*288*/ 0, 0, VK_F1, VK_F2, VK_F3, VK_F4, VK_F5, VK_F6,
		/*296*/ VK_F7, VK_F8, VK_F9, VK_F10, VK_F11, VK_F12, VK_F13, VK_F14,
		/*304*/ VK_F15, VK_F16, VK_F17, VK_F18, VK_F19, VK_F20, VK_F21, VK_F22,
		/*312*/ VK_F23, VK_F24, 0, 0, 0, 0, 0, 0,
		/*320*/ VK_NUMPAD0, VK_NUMPAD1, VK_NUMPAD2, VK_NUMPAD3, VK_NUMPAD4, VK_NUMPAD5, VK_NUMPAD6, VK_NUMPAD7,
		/*328*/ VK_NUMPAD8, VK_NUMPAD9, VK_DECIMAL, VK_DIVIDE, VK_MULTIPLY, VK_SUBTRACT, VK_ADD, VK_ENTER /*NUM*/,
		/*336*/ VK_EQUALS /*NUM*/, 0, 0, 0, VK_SHIFT /*LEFT*/, VK_CONTROL /*LEFT*/, VK_ALT, VK_WINDOWS /*LEFT*/,
		/*344*/ VK_SHIFT /*RIGHT*/, VK_CONTROL /*RIGHT*/, VK_ALT_GRAPH, VK_WINDOWS /*RIGHT*/, VK_CONTEXT_MENU
	};
	
	public static int keyToCode(int key) {
		if(key>=256) {
			return (key-256<fkeyMap.length) ? fkeyMap[key-256] : 0;
		}
		else if(key>=GLFW_KEY_SPACE && key<=GLFW_KEY_GRAVE_ACCENT) {
			switch(key) {
				case GLFW_KEY_APOSTROPHE:
					return VK_QUOTE;
				case GLFW_KEY_GRAVE_ACCENT:
					return VK_BACK_QUOTE;
				default:
					return key;
			}
		}
		else
			return 0;
	}

}
