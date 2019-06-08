package com.xrbpowered.gl.examples.tutorial;

import static org.lwjgl.opengl.GL11.*;

import com.xrbpowered.gl.client.Client;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.pane.Pane;
import com.xrbpowered.gl.ui.pane.PaneShader;
import com.xrbpowered.gl.ui.pane.TexturePane;

public class GLQuadPane extends Client {

	private Pane quad;
	
	public GLQuadPane() {
		super("Quad using Pane");
	}
	
	@Override
	public void createResources() {
		glClearColor(0.5f, 0.5f, 0.5f, 1f);
		PaneShader.createInstance();
		quad = new TexturePane(new Texture("example_assets/checker.png")).setAnchor(10, 10).setSize(512, 512);
	}
	
	@Override
	public void releaseResources() {
		quad.release();
		PaneShader.releaseInstance();
	}
	
	@Override
	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT);
		quad.draw(primaryBuffer);
	}

	public static void main(String[] args) {
		new GLQuadPane().run();
	}

}
