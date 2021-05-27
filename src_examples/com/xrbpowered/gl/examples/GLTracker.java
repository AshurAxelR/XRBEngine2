package com.xrbpowered.gl.examples;

import java.awt.Color;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.ActorPicker;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class GLTracker extends UIClient {

	private StandardShader shader;
	private StaticMesh mesh;
	private StaticMeshActor meshActor;
	private ActorPicker picker;

	private CameraActor camera = null;
	private Controller controller;
	private Controller activeController = null;

	private static final Color transparentColor = new Color(0x00ffffff, true);
	
	private PointerPane mousePointerPane;
	private boolean meshActorHover = false;
	
	private static abstract class PointerPane extends UIPane {
		protected float pointerX, pointerY;
		protected boolean showPointer = true;
		public PointerPane(UIContainer parent) {
			super(parent, false);
			setSize(100, 40);
		}
		@Override
		protected void paintSelf(GraphAssist g) {
			g.graph.setBackground(transparentColor);
			g.graph.clearRect(0, 0, (int)getWidth(), (int)getHeight());
			g.setColor(Color.BLACK);
			int midy = (int)getHeight()/2;
			g.line(midy, 0, midy, getHeight());
			g.line(0, midy, 120, midy);
			g.graph.drawOval(midy-10, midy-10, 21, 21);
			g.drawString(String.format("X: %.2f", pointerX), getWidth()-5, midy-4, GraphAssist.RIGHT, GraphAssist.BOTTOM);
			g.drawString(String.format("Y: %.2f", pointerY), getWidth()-5, midy+4, GraphAssist.RIGHT, GraphAssist.TOP);
		}
		public abstract void updatePointer();
		@Override
		public void render(RenderTarget target) {
			updatePointer();
			if(isVisible() && showPointer) {
				int d = (int)(getHeight()/getPixelScale())/2;
				pane.setAnchor((int)pointerX-d, (int)pointerY-d);
				requestRepaint = true;
				super.render(target);
			}
		}
	}
	
	public GLTracker() {
		super("GLTracker");
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);

		new UIOffscreen(getContainer()) {
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			public void setupResources() {
				clearColor = new Color(0xeeeeee);
				camera = new CameraActor.Perspective().setRange(0.1f, 40f).setAspectRatio(getWidth(), getHeight());
				camera.position = new Vector3f(0, 0, 10);
				camera.updateTransform();
				controller = new Controller(input).setActor(camera);
				
				shader = StandardShader.getInstance();
				shader.setFog(((CameraActor.Perspective) camera).getFar()/2, ((CameraActor.Perspective) camera).getFar(),
						new Vector4f(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0.0f));
				shader.ambientColor.set(0.5f, 0.5f, 0.5f, 1f);
				shader.lightColor.set(0.5f, 0.5f, 0.5f, 1f);
				shader.lightDir.set(-0.5f, -0.5f, -1f).normalize();

				mesh = FastMeshBuilder.sphere(1, 16, StandardShader.standardVertexInfo, null);
				meshActor = StaticMeshActor.make(mesh, shader, new Texture(new Color(0xffcc99)), new Texture(new Color(0x999999)), new Texture(new Color(0x8080ff)));
				
				picker = new ActorPicker(camera);
				
				super.setupResources();
			}
			
			@Override
			public boolean onMouseDown(float x, float y, Button button, int mods) {
				if(button==UIElement.Button.left) {
					activeController = controller;
					getBase().resetFocus();
					activeController.setMouseLook(true);
					mousePointerPane.showPointer = false;
				}
				return true;
			}
			
			@Override
			public void updateTime(float dt) {
				meshActor.updateTransform();
				if(activeController!=null) {
					if(input.isMouseDown(0))
						activeController.update(dt);
					else {
						activeController.setMouseLook(false);
						activeController = null;
					}
				}
				else {
					mousePointerPane.showPointer = true;
				}
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				
				picker.startPicking((int)input.getMouseX(), getFrameHeight()-(int)input.getMouseY(), target);
				picker.drawActor(meshActor, 1);
				meshActorHover = picker.finishPicking(target)==1;
				
				shader.setCamera(camera);
				meshActor.draw();
			}
		};
		
		UINode uiRoot = new UINode(getContainer());
		
		mousePointerPane = new PointerPane(uiRoot) {
			@Override
			public void updatePointer() {
				pointerX = input.getMouseX();
				pointerY = input.getMouseY();
			}
		};
		
		new PointerPane(uiRoot) {
			private Vector4f vtrackScreen = new Vector4f();
			@Override
			public void updatePointer() {
				vtrackScreen.set(meshActor.position.x, meshActor.position.y, meshActor.position.z, 1);
				camera.getView().transform(vtrackScreen);
				camera.getProjection().transform(vtrackScreen);
				float fw = (float)getFrameWidth();
				float fh = (float)getFrameHeight();
				pointerX = fw/2f * vtrackScreen.x / vtrackScreen.w + fw/2f;
				pointerY = fh/2f - fh/2f * vtrackScreen.y / vtrackScreen.w;
				showPointer = vtrackScreen.z>0;
			}
			@Override
			protected void paintSelf(GraphAssist g) {
				super.paintSelf(g);
				if(meshActorHover && mousePointerPane.showPointer) {
					int midy = (int)getHeight()/2;
					g.setStroke(3f);
					g.graph.drawOval(midy-10, midy-10, 21, 21);
				}
			}
		};
	}

	public static void main(String[] args) {
		new GLTracker().run();
	}

}
