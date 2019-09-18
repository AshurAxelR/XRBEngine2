package com.xrbpowered.gl.ui.common;

import org.joml.Vector4f;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.ui.UINode;

public class UIPointerActor extends Actor {

	public final UINode ui;
	public float pivotx, pivoty;
	
	public CameraActor camera;
	
	public float dist;
	public float maxDist = -1f;
	
	public boolean visible = true;
	
	public UIPointerActor(UINode ui, CameraActor camera) {
		this.ui = ui;
		this.camera = camera;
		centerPivot();
	}

	public UIPointerActor setPivot(float x, float y) {
		this.pivotx = x;
		this.pivoty = y;
		return this;
	}
	
	public UIPointerActor setCamera(CameraActor camera) {
		this.camera = camera;
		return this;
	}
	
	public UIPointerActor centerPivot() {
		this.pivotx = ui.getWidth() / 2f;
		this.pivoty = ui.getWidth() / 2f;
		return this;
	}
	
	public void updateView(RenderTarget target) {
		dist = camera.getDistTo(this);
		Vector4f p = calcViewPos(camera, null);
		float x = (p.x+1f)*(float)target.getWidth()/2f - pivotx;
		float y = (1f-p.y)*(float)target.getHeight()/2f - pivoty;
		boolean visible = this.visible && (p.x>=-1f && p.x<=1f) && (p.y>=-1f && p.y<=1f) && (p.z>0 && (maxDist<0f || dist<=maxDist));
		ui.setVisible(visible);
		if(visible) {
			ui.setLocation(x, y);
			ui.getParent().repaint();
		}
	}

}
