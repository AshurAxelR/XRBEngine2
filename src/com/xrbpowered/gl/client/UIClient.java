package com.xrbpowered.gl.client;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.ui.ClientRootContainer;
import com.xrbpowered.gl.ui.ClientWindow;
import com.xrbpowered.gl.ui.pane.PaneShader;
import com.xrbpowered.zoomui.UIWindowFactory;
	
public class UIClient extends Client {

	// settings
	// private float uiScale = UIWindowFactory.getSystemScale();

	private ClientWindow uiWindow;
	public Color clearColor = Color.BLACK;
	
	public UIClient(String title, float scale) {
		super(title);
		uiWindow = new ClientWindow(this);
		getContainer().setBaseScale(scale);
	}

	public UIClient(String title) {
		this(title, UIWindowFactory.getSystemScale());
	}

	public ClientWindow getUIWindow() {
		return uiWindow;
	}
	
	public ClientRootContainer getContainer() {
		return (ClientRootContainer) uiWindow.getContainer();
	}
	
	public void createResources() {
		PaneShader.createInstance();
		getContainer().setupResources();
	}

	@Override
	public void resizeResources() {
		uiWindow.notifyResized();
		getContainer().resizeResources();
	}

	public void releaseResources() {
		getContainer().releaseResources();
		PaneShader.releaseInstance();
	}

	@Override
	public void render(float dt) {
		RenderTarget.setClearColor(clearColor);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		getContainer().updateTime(dt);
		getContainer().render(primaryBuffer);
	}
	
	@Override
	public void keyPressed(char c, int code) {
		getContainer().onKeyPressed(c, code, input.getInputInfo());
	}
	
	@Override
	public void mouseMoved(float x, float y) {
		if(input.isMouseDown())
			getContainer().onMouseDragged(x, y, input.getMouseInfo());
		else
			getContainer().onMouseMoved(x, y, input.getMouseInfo());
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		getContainer().notifyMouseDown(x, y, input.getMouseInfo(button));
	}
	
	@Override
	public void mouseUp(float x, float y, int button) {
		getContainer().notifyMouseUp(x, y, input.getMouseInfo(button), null);
	}
	
	@Override
	public void mouseScroll(float x, float y, float delta) {
		getContainer().notifyMouseScroll(x, y, delta, input.getMouseInfo());
	}


}
