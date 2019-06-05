package com.xrbpowered.gl.client;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.CPAssetManager;
import com.xrbpowered.gl.res.buffer.PrimaryBuffer;

public class Client {

	private String title;
	private long window = NULL;
	
	// TODO settings
	private boolean fullscreen = false;
	private int windowedWidth = 1600;
	private int windowedHeight = 900;
	
	public final ClientInput input = new ClientInput(this);
	
	public final PrimaryBuffer primaryBuffer = new PrimaryBuffer(this);
	
	private static int frameWidth, frameHeight;
	
	private int frames = 0;
	private float fpsTime = 0;
	private float fps = 0f;
	
	private GLFWWindowSizeCallbackI windowSizeCallback = new GLFWWindowSizeCallbackI() {
		@Override
		public void invoke(long window, int width, int height) {
			frameWidth = width;
			frameHeight = height;
			glViewport(0, 0, frameWidth, frameHeight);
			resizeResources();
		}
	};
	
	public Client(String title) {
		this.title = title;
		
		AssetManager.defaultAssets = new CPAssetManager("assets", AssetManager.defaultAssets);
		
		if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		GLFWErrorCallback.createPrint(System.err).set();
	}
	
	public void run() {
		createWindow();
		
		float t0 = (float) glfwGetTime();
		fpsTime = t0;
		while(!glfwWindowShouldClose(window)) {
			input.pollEvents();
			// TODO process window operations (e.g. switch fullscreen)
			
			float t = (float) glfwGetTime();
			float dt = t-t0;
			
			if(t-fpsTime>0.5) {
				fps = frames / (t-fpsTime);
				frames = 0;
				fpsTime = t;
			}
			frames++;
			
			render(dt);

			glfwSwapBuffers(window);
			t0 = t;
		}
		
		destroyWindow();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public boolean hasContext() {
		return window!=NULL;
	}
	
	public void createWindow() {
		if(hasContext())
			destroyWindow();
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		if(fullscreen)
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
		else
			glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		if(fullscreen) {
			GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			window = glfwCreateWindow(mode.width(), mode.height(), title, NULL, NULL);
		}
		else
			window = glfwCreateWindow(windowedWidth, windowedHeight, title, NULL, NULL);
		
		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		centerWindow();
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1); // v-sync

		GL.createCapabilities();

		glfwSetWindowSizeCallback(window, windowSizeCallback);
		input.registerCallbacks(window);
		glfwShowWindow(window);
		
		setupResources();
	}
	
	public void destroyWindow() {
		if(window==NULL)
			return;
		releaseResources();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		window = NULL;
	}
	
	public void centerWindow() {
		try(MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(window, (mode.width() - pWidth.get(0)) / 2, (mode.height() - pHeight.get(0)) / 2);
		}
	}
	
	public void setupResources() {
	}
	
	public void resizeResources() {
	}
	
	public void releaseResources() {
	}
	
	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static int getWidth() {
		return frameWidth;
	}
	
	public static int getHeight() {
		return frameHeight;
	}
	
	public void keyPressed(char c, int code) {
	}
	
	public void mouseMoved(float x, float y) {
	}
	
	public void mouseDown(float x, float y, int button) {
	}
	
	public void mouseUp(float x, float y, int button) {
	}
	
	public void mouseScroll(float x, float y, float delta) {
	}
	
	public float getFps() {
		return fps;
	}
	
}
