package com.xrbpowered.gl.res.buffer;

import com.xrbpowered.gl.client.Client;

public class PrimaryBuffer extends RenderTarget {

	protected final Client client;
	
	public PrimaryBuffer(Client client) {
		super(0, 0, 0);
		this.client = client;
	}
	
	@Override
	public int getWidth() {
		return Client.getWidth();
	}
	
	@Override
	public int getHeight() {
		return Client.getHeight();
	}

}
