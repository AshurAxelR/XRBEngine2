package com.xrbpowered.gl.ui;

import java.awt.Cursor;

import com.xrbpowered.gl.client.Client;
import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.zoomui.BaseContainer;
import com.xrbpowered.zoomui.UIModalWindow;
import com.xrbpowered.zoomui.UIModalWindow.ResultHandler;
import com.xrbpowered.zoomui.UIWindow;
import com.xrbpowered.zoomui.UIWindowFactory;

public class ClientWindow extends UIWindow {

	private static UIWindowFactory factory = new UIWindowFactory() {
		@Override
		public UIWindow create(String title, int w, int h, boolean canResize) {
			throw new UnsupportedOperationException();
		}
		@Override
		public <A> UIModalWindow<A> createModal(String title, int w, int h, boolean canResize, ResultHandler<A> onResult) {
			throw new UnsupportedOperationException();
		}
		@Override
		public UIWindow createUndecorated(int w, int h) {
			throw new UnsupportedOperationException();
		}
	};
	
	public final UIClient client; 
	
	public ClientWindow(UIClient client) {
		super(factory);
		this.client = client;
	}
	
	@Override
	protected BaseContainer createContainer() {
		return new ClientBaseContainer(this);
	}

	@Override
	public int getClientWidth() {
		return Client.getWidth();
	}

	@Override
	public int getClientHeight() {
		return Client.getHeight();
	}

	@Override
	public void setClientSize(int width, int height) {
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public void moveTo(int x, int y) {
	}

	@Override
	public void center() {
		client.centerWindow();
	}

	@Override
	public void show() {
	}

	@Override
	public void repaint() {
		((ClientBaseContainer) container).repaint();
	}

	@Override
	public int baseToScreenX(float x) {
		return (int)x;
	}

	@Override
	public int baseToScreenY(float y) {
		return (int)y;
	}

	@Override
	public float screenToBaseX(int x) {
		return x;
	}

	@Override
	public float screenToBaseY(int y) {
		return y;
	}

	@Override
	public void setCursor(Cursor cursor) {
		// change cursor
	}

}
