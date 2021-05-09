package com.xrbpowered.gl.client;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.ui.ClientBaseContainer;
import com.xrbpowered.gl.ui.ClientWindow;
import com.xrbpowered.gl.ui.pane.PaneShader;
import com.xrbpowered.zoomui.UIElement;
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
	
	public ClientBaseContainer getContainer() {
		return (ClientBaseContainer) uiWindow.getContainer();
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
		getContainer().onKeyPressed(c, code, input.getKeyMods());
	}
	
	@Override
	public void mouseMoved(float x, float y) {
		if(input.isMouseDown())
			getContainer().onMouseDragged(x, y);
		else
			getContainer().onMouseMoved(x, y, input.getKeyMods());
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		getContainer().notifyMouseDown(x, y, getMouseButton(button), input.getKeyMods());
	}
	
	@Override
	public void mouseUp(float x, float y, int button) {
		getContainer().notifyMouseUp(x, y, getMouseButton(button), input.getKeyMods(), null);
	}
	
	@Override
	public void mouseScroll(float x, float y, float delta) {
		getContainer().notifyMouseScroll(x, y, delta, input.getKeyMods());
	}

	public static UIElement.Button getMouseButton(int button) {
		if(button>=0 && button<3)
			return UIElement.Button.values()[button];
		else
			return UIElement.Button.unknown;
	}

}
