package com.xrbpowered.gl.client;

import static java.awt.event.KeyEvent.*;
import static org.lwjgl.glfw.GLFW.*;

import java.util.HashSet;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import com.xrbpowered.zoomui.UIElement;

public class ClientInput {

	public final Client client;
	private long window;
	
	private float mouseX, mouseY;
	private HashSet<Integer> pressedMouseButtons = new HashSet<>();
	
	private int keyMods = 0;
	private int keyCode = 0;
	private HashSet<Integer> pressedKeys = new HashSet<>();

	private GLFWCursorPosCallbackI cursorPosCallback = new GLFWCursorPosCallbackI() {
		@Override
		public void invoke(long window, double xpos, double ypos) {
			mouseX = (float) xpos;
			mouseY = (float) ypos;
			client.mouseMoved(mouseX, mouseY);
		}
	};
	
	private GLFWMouseButtonCallbackI mouseButtonCallback = new GLFWMouseButtonCallbackI() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			if(action==GLFW_PRESS) {
				pressedMouseButtons.add(button);
				client.mouseDown(mouseX, mouseY, button);
			}
			else {
				pressedMouseButtons.remove(button);
				client.mouseUp(mouseX, mouseY, button);
			}
		}
	};
	
	private GLFWScrollCallbackI mouseScrollCallback = new GLFWScrollCallbackI() {
		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			client.mouseScroll(mouseX, mouseY, (float) -yoffset);
		}
	};
	
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			keyMods = 0;
			if((mods&GLFW.GLFW_MOD_SHIFT)!=0)
				keyMods |= UIElement.modShiftMask;
			if((mods&GLFW.GLFW_MOD_CONTROL)!=0)
				keyMods |= UIElement.modCtrlMask;
			if((mods&GLFW.GLFW_MOD_ALT)!=0)
				keyMods |= UIElement.modAltMask;

			int code = scanToCode(scancode);
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

	public boolean isMouseDown() {
		return !pressedMouseButtons.isEmpty();
	}
	
	public boolean isMouseDown(Integer button) {
		return pressedMouseButtons.contains(button);
	}
	
	public int getKeyMods() {
		return keyMods;
	}
	
	public int getMouseButtons() { // TODO change to ZoomUI mouse buttons
		int buttons = 0;
		int d = 1;
		for(int i=GLFW_MOUSE_BUTTON_1; i<=GLFW_MOUSE_BUTTON_LAST; i++) {
			int state = glfwGetMouseButton(window, i);
			if(state==GLFW_PRESS)
				buttons |= d;
			d = d<<1;
		}
		return buttons;
	}
	
	public boolean isKeyDown(Integer code) {
		return pressedKeys.contains(code);
	}


	public static final int[] keyMap = {
			/*0x00*/ 0, VK_ESCAPE, VK_1, VK_2, VK_3, VK_4, VK_5, VK_6,
			/*0x08*/ VK_7, VK_8, VK_9, VK_0, VK_MINUS, VK_EQUALS, VK_BACK_SPACE, VK_TAB,
			/*0x10*/ VK_Q, VK_W, VK_E, VK_R, VK_T, VK_Y, VK_U, VK_I,
			/*0x18*/ VK_O, VK_P, VK_OPEN_BRACKET, VK_CLOSE_BRACKET, VK_ENTER, VK_CONTROL /*LEFT*/, VK_A, VK_S,
			/*0x20*/ VK_D, VK_F, VK_G, VK_H, VK_J, VK_K, VK_L, VK_SEMICOLON,
			/*0x28*/ VK_QUOTE, VK_DEAD_GRAVE, VK_SHIFT /*LEFT*/, VK_BACK_SLASH, VK_Z, VK_X, VK_C, VK_V,
			/*0x30*/ VK_B, VK_N, VK_M, VK_COMMA, VK_PERIOD, VK_SLASH, VK_SHIFT /*RIGHT*/, VK_MULTIPLY,
			/*0x38*/ VK_ALT, VK_SPACE, VK_CAPS_LOCK, VK_F1, VK_F2, VK_F3, VK_F4, VK_F5,
			/*0x40*/ VK_F6, VK_F7, VK_F8, VK_F9, VK_F10, VK_NUM_LOCK, VK_SCROLL_LOCK, VK_NUMPAD7,
			/*0x48*/ VK_NUMPAD8, VK_NUMPAD9, VK_SUBTRACT, VK_NUMPAD4, VK_NUMPAD5, VK_NUMPAD6, VK_ADD, VK_NUMPAD1,
			/*0x50*/ VK_NUMPAD2, VK_NUMPAD3, VK_NUMPAD0, VK_DECIMAL, 0, 0, 0, VK_F11,
			/*0x58*/ VK_F12, 0, 0, 0, 0, 0, 0, 0,
			/*0x60*/ 0, 0, 0, 0, VK_F13, VK_F14, VK_F15, VK_F16,
			/*0x68*/ VK_F17, VK_F18, 0, 0, 0, 0, 0, 0,
			/*0x70*/ VK_KANA, VK_F19, 0, 0, 0, 0, 0, 0,
			/*0x78*/ 0, VK_CONVERT, 0, VK_NONCONVERT, 0, 0 /*YEN*/, 0, 0,
			/*0x80*/ 0, 0, 0, 0, 0, 0, 0, 0,
			/*0x88*/ 0, 0, 0, 0, 0, 0 /*NUM_EQUALS*/, 0, 0,
			/*0x90*/ VK_CIRCUMFLEX, VK_AT, VK_COLON, VK_UNDERSCORE, VK_KANJI, VK_STOP, 0 /*AX*/, 0 /*UNLABELED*/,
			/*0x98*/ 0, 0, 0, 0, VK_ENTER /*NUM*/, VK_CONTROL /*RIGHT*/, 0, 0,
			/*0xa0*/ 0, 0, 0, 0, 0, 0, 0, 0 /*SECTION*/,
			/*0xa8*/ 0, 0, 0, 0, 0, 0, 0, 0,
			/*0xb0*/ 0, 0, 0, VK_COMMA /*NUM*/, 0, VK_DIVIDE, 0, 0 /*SYSRQ*/,
			/*0xb8*/ VK_ALT_GRAPH, 0, 0, 0, 0, 0, 0, 0,
			/*0xc0*/ 0, 0, 0, 0, 0 /*FUNCTION*/, VK_PAUSE, 0, VK_HOME,
			/*0xc8*/ VK_UP, VK_PAGE_UP, 0, VK_LEFT, 0, VK_RIGHT, 0, VK_END,
			/*0xd0*/ VK_DOWN, VK_PAGE_DOWN, VK_INSERT, VK_DELETE, 0, 0, 0, 0,
			/*0xd8*/ 0, 0, VK_CLEAR, VK_WINDOWS, 0, 0, 0, 0,
			/*0xe0*/ 0, 0, 0, 0, 0, 0, 0, 0,
			/*0xe8*/ 0, 0, 0, 0, 0, 0, 0, 0,
			/*0xf0*/ 0, 0, 0, 0, 0, 0, 0, 0,
			/*0xf8*/ 0, 0, 0, 0, 0, 0, 0, 0,
		};
	
	public static int scanToCode(int scan) {
		return (scan<128) ? keyMap[scan] : (scan-128<keyMap.length) ? keyMap[scan-128] : 0;
	}

}
