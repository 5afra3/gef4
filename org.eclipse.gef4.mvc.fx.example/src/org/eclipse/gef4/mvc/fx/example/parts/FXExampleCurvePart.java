package org.eclipse.gef4.mvc.fx.example.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurveElement;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXHoverFeedbackPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionFeedbackPolicy;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractHandlePolicy;
import org.eclipse.gef4.mvc.policies.AbstractHoverFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleCurvePart extends AbstractFXContentPart implements
		PropertyChangeListener {

	private GeometryNode<ICurve> visual;
	private List<IAnchor<Node>> anchors = new ArrayList<IAnchor<Node>>();

	@SuppressWarnings("unchecked")
	public FXExampleCurvePart() {
		visual = new GeometryNode<ICurve>();
		installEditPolicy(AbstractSelectionFeedbackPolicy.class, new FXSelectionFeedbackPolicy() {
			@Override
			public Node createFeedback(Node selectionVisual) {
				if (selectionVisual instanceof GeometryNode) {
					GeometryNode<ICurve> gn = (GeometryNode<ICurve>) selectionVisual;
					return new GeometryNode<ICurve>((ICurve) gn.getGeometry().getCopy());
				}
				return super.createFeedback(selectionVisual);
			}
		});
		installEditPolicy(AbstractHoverFeedbackPolicy.class, new FXHoverFeedbackPolicy() {
			@Override
			public Node createFeedback(Node selectionVisual) {
				if (selectionVisual instanceof GeometryNode) {
					GeometryNode<ICurve> gn = (GeometryNode<ICurve>) selectionVisual;
					return new GeometryNode<ICurve>((ICurve) gn.getGeometry().getCopy());
				}
				return super.createFeedback(selectionVisual);
			}
		});
		installEditPolicy(AbstractHandlePolicy.class, new AbstractHandlePolicy<Node>() {
			@Override
			public List<IHandlePart<Node>> createSelectionHandles() {
				List<IHandlePart<Node>> handles = new ArrayList<IHandlePart<Node>>();
//				handles.add(new FXBendHandlePart());
				return handles;
			}
			
			@Override
			public List<IHandlePart<Node>> createHoverHandles() {
				return super.createSelectionHandles();
			}
		});
	}

	@Override
	public FXGeometricCurveElement getModel() {
		return (FXGeometricCurveElement) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof FXGeometricCurveElement)) {
			throw new IllegalArgumentException(
					"Only ICurve models are supported.");
		}
		super.setModel(model);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		FXGeometricCurveElement curveVisual = getModel();
		if (visual.getGeometry() != curveVisual.geometry) {
			visual.setGeometry(curveVisual.geometry);
		}
		visual.toBack();
	}

	@Override
	public void attachVisualToAnchorageVisual(IAnchor<Node> anchor) {
		anchors.add(anchor);
		anchor.addPropertyChangeListener(this);
	}

	@Override
	public void detachVisualFromAnchorageVisual(IAnchor<Node> anchor) {
		anchors.remove(anchor);
		anchor.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(IAnchor.REPRESH)) {
			updateModel();
			refreshVisual();
		}
	}

	/*
	 * TODO: - position computation (currently somewhere inside the bounds) -
	 * source / target distinction
	 */
	private void updateModel() {
		if (anchors.size() == 2) {
			// use anchors as start and end point
			Node startNode = anchors.get(0).getAnchorage();
			Node endNode = anchors.get(1).getAnchorage();

			// compute reference points in local coordinate space
			Point startReference = JavaFX2Geometry.toRectangle(
					getVisual().sceneToLocal(
							endNode.localToScene(endNode.getBoundsInLocal())))
					.getCenter();
			Point endReference = JavaFX2Geometry.toRectangle(
					getVisual()
							.sceneToLocal(
									startNode.localToScene(startNode
											.getBoundsInLocal()))).getCenter();

			try {
				// compute new anchor positions
				Point start = anchors.get(0).getPosition(this.getVisual(),
						startReference);
				Point end = anchors.get(1).getPosition(this.getVisual(),
						endReference);

				// update model
				getModel().geometry = createCurve(start, end);
			} catch (IllegalArgumentException e) {
				// When no intersection point can be found by the ChopBoxAnchor,
				// the connection is invisible
				// TODO: handle this proactively
			}
		}
	}

//	public ICurve createCurve(Point start, Point end) {
//		Point[] points = new Point[anchorPoints.size() + 2];
//		points[0] = start;
//		for (int i = 0; i < anchorPoints.size(); i++) {
//			points[i+1] = anchorPoints.get(i);
//		}
//		points[points.length - 1] = end;
//		return PolyBezier.interpolateCubic(points);
//	}
	
	public ICurve createCurve(Point start, Point end) {
		return new org.eclipse.gef4.geometry.planar.Line(start, end);
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
//		System.out.println("getAnchor() == null ?!");
		return null;
	}

//	public List<Point> getAnchorPoints() {
//		return anchorPoints;
//	}

}
