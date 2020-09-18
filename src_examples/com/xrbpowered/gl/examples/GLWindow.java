package com.xrbpowered.gl.examples;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class GLWindow {

	// The window handle
	private long window;
	private boolean fullscreen = false;
	public boolean toggleFullscreen = false;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		if(fullscreen)
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
		else
			glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);
		//glfwWindowHint(GLFW_RED_BITS, mode.redBits());
		//glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
		//glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
		//glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
		// Create the window
		if(fullscreen)
			window = glfwCreateWindow(mode.width(), mode.height(), "Hello World!", NULL, NULL);
		else
			window = glfwCreateWindow(1600, 900, "Hello World!", NULL, NULL);
		if(window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			else if(key == GLFW_KEY_ENTER && action == GLFW_PRESS) {
				//toggleFullscreen = true;
				fullscreen = !fullscreen;
				toggleFullscreen = false;
				glfwFreeCallbacks(window);
				glfwDestroyWindow(window);
				init();
			}
		});

		// Get the thread stack and push a new frame
		try(MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.3f, 0.5f, 0.7f, 0.0f);
		
		System.out.println("Device: " + GL11.glGetString(GL11.GL_RENDERER));
		System.out.println("Device vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
	}

	private void loop() {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while(!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			/*if(toggleFullscreen) {
				fullscreen = !fullscreen;
				toggleFullscreen = false;
				glfwFreeCallbacks(window);
				glfwDestroyWindow(window);
				init();
			}*/
		}
	}

	public static void main(String[] args) {
		new GLWindow().run();
	}

}
