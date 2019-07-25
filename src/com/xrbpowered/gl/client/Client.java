package com.xrbpowered.gl.client;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.event.KeyEvent;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.CPAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;

/**
 * Main application class representing the application window. Currently the engine supports only one window per application.
 * 
 * <p>Class constructor only initialises the application. The window is created, maintained, and destroyed within the {@link #run()} method.</p>
 *  
 */
public class Client {

	private String title;
	private long window = NULL;
	private boolean setup = false;
	
	// TODO settings
	public boolean fullscreen = false;
	public boolean vsync = true;
	public long noVsyncSleep = 12;
	public int windowedWidth = 1600;
	public int windowedHeight = 900;
	
	/**
	 * User input manager.
	 */
	public final ClientInput input = new ClientInput(this);
	
	/**
	 * {@link RenderTarget} for the window's primary OpenGL buffer.
	 */
	public final RenderTarget primaryBuffer = new RenderTarget(0, 0, 0) {
		@Override
		public int getWidth() {
			return Client.this.getFrameWidth();
		}
		@Override
		public int getHeight() {
			return Client.this.getFrameHeight();
		}
	};
	
	private int frameWidth, frameHeight;
	
	private int frames = 0;
	private float fpsTime = 0;
	private float fps = 0f;
	
	private GLFWWindowSizeCallbackI windowSizeCallback = new GLFWWindowSizeCallbackI() {
		@Override
		public void invoke(long window, int width, int height) {
			frameWidth = width;
			frameHeight = height;
			glViewport(0, 0, frameWidth, frameHeight);
			if(hasResources())
				resizeResources();
		}
	};
	
	private GLFWWindowCloseCallbackI windowCloseCallback = new GLFWWindowCloseCallbackI() {
		@Override
		public void invoke(long window) {
			if(!confirmClosing())
				glfwSetWindowShouldClose(window, false);
		}
	};
	
	/**
	 * Initialise the application and GLFW library.
	 * @param title Window title
	 */
	public Client(String title) {
		this.title = title;
		
		AssetManager.defaultAssets = new CPAssetManager("assets", AssetManager.defaultAssets);
		
		if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		GLFWErrorCallback.createPrint(System.err).set();
	}
	
	/**
	 * Main application cycle. The method creates the window ({@link #createWindow()},
	 * processes messages and rendering ({@link ClientInput#pollEvents()}, {@link #render(float)}),
	 * destroys the window ({@link #destroyWindow()}), and exits the application ({@link System#exit(int)}).
	 */
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
			if(!vsync && noVsyncSleep>0) {
				try {
					Thread.sleep(noVsyncSleep);
				}
				catch(InterruptedException e) {
				}
			}
			t0 = t;
		}
		
		destroyWindow();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		System.exit(0);
	}
	
	/**
	 * Check if the window and OpenGL context have been created. Any OpenGL calls are allowed only after this is <code>true</code>. 
	 * @return <code>true</code> if OpenGL context exists.
	 */
	public boolean hasContext() {
		return window!=NULL;
	}
	
	/**
	 * Check if resources have been created. This is <code>true</code> after {@link #createResources()} and before {@link #releaseResources()}. 
	 * @return <code>true</code> if {@link #createResources()} has finished.
	 */
	public boolean hasResources() {
		return window!=NULL && setup;
	}
	
	/**
	 * Create and show new application window. Any existing window is destroyed first.
	 * Once the window is created, {@link #createResources()} is called.
	 */
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
			frameWidth = mode.width();
			frameHeight = mode.height();
			window = glfwCreateWindow(frameWidth, frameHeight, title, NULL, NULL);
		}
		else
			window = glfwCreateWindow(windowedWidth, windowedHeight, title, NULL, NULL);
		
		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(vsync ? 1 : 0);

		GL.createCapabilities(true);

		glfwSetWindowSizeCallback(window, windowSizeCallback);
		glfwSetWindowCloseCallback(window, windowCloseCallback);
		input.registerCallbacks(window);
		
		centerWindow();
		glfwShowWindow(window);
		
		createResources();
		setup = true;
	}
	
	/**
	 * Destroys current window and OpenGL context. 
	 */
	public void destroyWindow() {
		if(!hasContext())
			return;
		releaseResources();
		setup = false;
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		window = NULL;
	}
	
	/**
	 * Center window in the primary monitor. Newly created windows are centered by default.  
	 */
	public void centerWindow() {
		try(MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(window, (mode.width() - pWidth.get(0)) / 2, (mode.height() - pHeight.get(0)) / 2);
		}
	}
	
	/**
	 * Override this method to initialise OpenGL resources (textures, models, etc.).
	 * This method may be called multiple times during application life cycle,
	 * for example, when a window is re-created to switch between fullscreen and windowed modes.
	 * In this case, {@link #releaseResources()} is always called before {@link #createResources()}.
	 * Not cleaning up resources in {@link #releaseResources()} may cause memory leaks.
	 */
	public void createResources() {
	}
	
	/**
	 * This method is called when the window is resized.
	 */
	public void resizeResources() {
	}
	
	/**
	 * Override this method to clean-up OpenGL resources.
	 * Not cleaning up resources in {@link #releaseResources()} may cause memory leaks.
	 */
	public void releaseResources() {
	}
	
	/**
	 * Override to render the window contents.
	 * @param dt elapsed time in seconds since the last frame.
	 */
	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	/**
	 * Get primary OpenGL buffer width.
	 * @return width in pixels.
	 */
	public int getFrameWidth() {
		return frameWidth;
	}
	
	/**
	 * Get primary OpenGL buffer height.
	 * @return height in pixels.
	 */
	public int getFrameHeight() {
		return frameHeight;
	}
	
	/**
	 * Command application window to be closed in the next event cycle. 
	 */
	public void requestExit() {
		glfwSetWindowShouldClose(window, true);
	}
	

	/**
	 * The method is called when the user attempts to close the window.
	 * Override this method to return <code>false</code> to prevent closing at this time.
	 * This method is not called from {@link #requestExit()}. 
	 * @return <code>true</code> to close the window.
	 */
	public boolean confirmClosing() {
		return true;
	}
	
	/**
	 * The method is invoked by {@link ClientInput} when the user types a key.
	 * @param c typed character.
	 * @param code key code compatible with {@link KeyEvent}.
	 */
	public void keyPressed(char c, int code) {
	}
	
	/**
	 * The method is invoked by {@link ClientInput} when the user moves a mouse.
	 * @param x cursor <i>x</i> position in window coordinates.
	 * @param y cursor <i>y</i> position in window coordinates.
	 */
	public void mouseMoved(float x, float y) {
	}
	
	/**
	 * The method is invoked by {@link ClientInput} when the user presses a mouse button.
	 * @param x cursor <i>x</i> position in window coordinates.
	 * @param y cursor <i>y</i> position in window coordinates.
	 * @param button pressed mouse button index.
	 */
	public void mouseDown(float x, float y, int button) {
	}
	
	/**
	 * The method is invoked by {@link ClientInput} when a mouse button is released.
	 * @param x cursor <i>x</i> position in window coordinates.
	 * @param y cursor <i>y</i> position in window coordinates.
	 * @param button released mouse button index.
	 */
	public void mouseUp(float x, float y, int button) {
	}
	
	/**
	 * The method is invoked by {@link ClientInput} when mouse wheel is scrolled.
	 * @param x cursor <i>x</i> position in window coordinates.
	 * @param y cursor <i>y</i> position in window coordinates.
	 * @param delta scroll value
	 */
	public void mouseScroll(float x, float y, float delta) {
	}
	
	/**
	 * Get FPS (frames per second) counter.
	 * @return current FPS value.
	 */
	public float getFps() {
		return fps;
	}
	
}
