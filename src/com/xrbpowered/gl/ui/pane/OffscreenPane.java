package com.xrbpowered.gl.ui.pane;

import com.xrbpowered.gl.res.buffer.OffscreenBuffer;

public class OffscreenPane extends Pane {

	protected OffscreenBuffer buffer = null;

	public OffscreenPane() {
		ydown = false;
	}
	
	public OffscreenPane(OffscreenBuffer buffer) {
		setBuffer(buffer);
	}
	
	public OffscreenBuffer getBuffer() {
		return buffer;
	}
	
	public void setBuffer(OffscreenBuffer buffer) {
		if(this.buffer!=null)
			this.buffer.release();
		this.buffer = buffer;
	}
	
	@Override
	public void bindTexture(int index) {
		buffer.bindColorBuffer(index);
	}
	
	public OffscreenPane resizeToBuffer(float scale) {
		if(buffer==null) {
			width = 0;
			height = 0;
		}
		else {
			width = (int)(buffer.getWidth() * scale);
			height = (int)(buffer.getHeight() * scale);
		}
		return this;
	}
	
	public OffscreenPane resizeToBuffer() {
		return resizeToBuffer(1f);
	}
	
	public boolean isVisible() {
		return super.isVisible() && buffer!=null;
	}
	
	public void release() {
		if(buffer!=null)
			buffer.release();
	}

}
