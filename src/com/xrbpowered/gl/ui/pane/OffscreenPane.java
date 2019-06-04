package com.xrbpowered.gl.ui.pane;

import com.xrbpowered.gl.res.buffer.OffscreenBuffer;

public class OffscreenPane extends AbstractPane {

	protected OffscreenBuffer buffer = null;

	public OffscreenPane() {
	}
	
	public OffscreenPane(OffscreenBuffer buffer) {
		setBuffer(buffer);
	}
	
	public OffscreenBuffer getBuffer() {
		return buffer;
	}
	
	public void setBuffer(OffscreenBuffer buffer) {
		if(this.buffer!=null)
			this.buffer.destroy();
		this.buffer = buffer;
	}
	
	@Override
	protected void bindTexture(int index) {
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
	
	public void destroy() {
		if(buffer!=null)
			buffer.destroy();
	}

}
