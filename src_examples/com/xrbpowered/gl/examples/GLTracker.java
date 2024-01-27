package com.xrbpowered.gl.examples;

import static com.xrbpowered.zoomui.MouseInfo.LEFT;
import static com.xrbpowered.zoomui.MouseInfo.RIGHT;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

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
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;

public class GLTracker extends UIClient {

	private StandardShader shader;
	private StaticMesh mesh;
	private StaticMeshActor meshActor;
	private ActorPicker picker;

	private CameraActor camera = null;
	private Controller controller;
	private Controller activeController = null;

	private static final Color transparentColor = new Color(0x00ffffff, true);
	private static final Color pointerColor = new Color(0x555555);
	
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
		protected void paintBackground(GraphAssist g) {
			g.graph.setBackground(transparentColor);
			g.graph.clearRect(0, 0, (int)getWidth(), (int)getHeight());
			g.setColor(pointerColor);
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
				int d = (int)(getHeight()/getPixelSize())/2;
				pane.setAnchor((int)pointerX-d, (int)pointerY-d);
				requestRepaint = true;
				super.render(target);
			}
		}
	}
	
	private UINode uiRoot, offsPane;
	private int quadX = 1;
	private int quadY = 1;
	
	public GLTracker() {
		super("GLTracker", 1.25f);
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
		clearColor = new Color(0xdddddd);

		UINode uiOffs = new UINode(getContainer()) {
			@Override
			public void layout() {
				offsPane.setSize(getWidth()/2, getHeight()/2);
				offsPane.setPosition(quadX*getWidth()/2, quadY*getHeight()/2);
				uiRoot.setSize(offsPane.getWidth(), offsPane.getHeight());
				uiRoot.setPosition(offsPane.getX(), offsPane.getY());
				super.layout();
			}
			@Override
			public boolean onMouseDown(float x, float y, MouseInfo mouse) {
				if(mouse.eventButton==RIGHT) {
					quadX = x>getWidth()/2 ? 1 : 0;
					quadY = y>getHeight()/2 ? 1 : 0;
					invalidateLayout();
					repaint();
				}
				return true;
			}
		};
		
		offsPane = new UIOffscreen(uiOffs) {
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
			public boolean onMouseDown(float x, float y, MouseInfo mouse) {
				if(mouse.eventButton==LEFT) {
					activeController = controller;
					getRoot().resetFocus();
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

				float px = getPixelSize();
				int mx = (int)(rootToLocalX(input.getMouseX())/px);
				int my = (int)((getHeight()-rootToLocalY(input.getMouseY()))/px);
				picker.startPicking(mx, my, target);
				picker.drawActor(meshActor, 1);
				meshActorHover = picker.finishPicking(target)==1;
				
				shader.setCamera(camera);
				meshActor.draw();
			}
		};
		
		uiRoot = new UINode(uiOffs);
		
		mousePointerPane = new PointerPane(uiRoot) {
			@Override
			public void updatePointer() {
				pointerX = input.getMouseX();
				pointerY = input.getMouseY();
			}
		};
		
		new PointerPane(uiRoot) {
			private Vector4f vtrackScreen = new Vector4f();
			private AffineTransform tx = new AffineTransform();
			private Point2D pt = new Point2D.Float(); 
			@Override
			public void paint(GraphAssist g) {
				tx.setTransform(g.getTransform());
				super.paint(g);
			}
			@Override
			public void updatePointer() {
				vtrackScreen.set(meshActor.position.x, meshActor.position.y, meshActor.position.z, 1);
				camera.getView().transform(vtrackScreen);
				camera.getProjection().transform(vtrackScreen);
				float fw = getParent().getWidth();// (float)getFrameWidth();
				float fh = getParent().getHeight();// (float)getFrameHeight();
				pt.setLocation(
						fw/2f * vtrackScreen.x / vtrackScreen.w + fw/2f,
						fh/2f - fh/2f * vtrackScreen.y / vtrackScreen.w
				);
				tx.transform(pt, pt);
				pointerX = (float)pt.getX();
				pointerY = (float)pt.getY();
				showPointer = vtrackScreen.z>0;
			}
			@Override
			protected void paintBackground(GraphAssist g) {
				super.paintBackground(g);
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
