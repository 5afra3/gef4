package org.eclipse.gef4.mvc.fx.example.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionFeedbackByEffectPolicy;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleCurvePart extends AbstractFXExampleElementPart implements
		PropertyChangeListener {

	private GeometryNode<ICurve> visual;
	private List<IAnchor<Node>> anchors = new ArrayList<IAnchor<Node>>();

	public List<IHandlePart<Node>> createWayPointHandles() {
		ArrayList<IHandlePart<Node>> handles = new ArrayList<IHandlePart<Node>>();

		// create selection handles on the vertices
		int i = 0;
		for (Point wayPoint : getModel().getWayPoints()) {
			handles.add(new FXWayPointHandlePart.Select(this, i, wayPoint));
			i++;
		}

		// create insertion handles on the edges
		i = 0;
		for (Line line : ((Polyline) visual.getGeometry()).getCurves()) {
			Point midPoint = line.get(0.5);
			handles.add(new FXWayPointHandlePart.Create(this, i, midPoint));
			i++;
		}

		return handles;
	}

	public FXExampleCurvePart() {
		visual = new GeometryNode<ICurve>();
		installEditPolicy(ISelectionPolicy.class,
				new ISelectionPolicy.Impl<Node>());
		installEditPolicy(IHoverPolicy.class, new IHoverPolicy.Impl<Node>() {
			@Override
			public boolean isHoverable() {
				return !getHost().getRoot().getViewer().getSelectionModel()
						.getSelected().contains(getHost());
			}
		});
		// TODO: we need proper feedback for curves
		installEditPolicy(AbstractSelectionFeedbackPolicy.class,
				new FXSelectionFeedbackByEffectPolicy() {

					@Override
					public void activate() {
						super.activate();
						getModel().addPropertyChangeListener(this);
					}

					@Override
					public void deactivate() {
						getModel().removePropertyChangeListener(this);
						super.deactivate();
					}

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						super.propertyChange(event);
						if (AbstractFXGeometricElement.GEOMETRY_PROPERTY
								.equals(event.getPropertyName())) {
							hideFeedback();
							removeHandles();
							addHandles();
							showPrimaryFeedback();
						}
					}

					@Override
					public List<IHandlePart<Node>> createHandles() {
						return createWayPointHandles();
					}

					@Override
					protected void hideFeedback() {
						getHost().getVisual().setEffect(null);
					}

					@Override
					protected void showSecondaryFeedback() {
						getHost().getVisual().setEffect(
								getPrimarySelectionFeedbackEffect());
					}

					@Override
					protected void showPrimaryFeedback() {
						getHost().getVisual().setEffect(
								getSecondarySelectionFeedbackEffect());
					}
				});
		installEditPolicy(AbstractWayPointPolicy.class,
				new AbstractWayPointPolicy() {
					private List<Point> wayPoints = new ArrayList<Point>();
					private boolean isCreate;

					@Override
					public void selectWayPoint(int wayPointIndex) {
						isCreate = false;
						wayPoints.clear();
						wayPoints.addAll(getModel().getWayPoints());
					}

					@Override
					public void createWayPoint(int wayPointIndex, Point p) {
						isCreate = true;
						wayPoints.clear();
						wayPoints.addAll(getModel().getWayPoints());
						wayPoints.add(wayPointIndex, new Point(p));
					}

					@Override
					public void updateWayPoint(int wayPointIndex, Point p) {
						Point point = wayPoints.get(wayPointIndex);
						point.x = p.x;
						point.y = p.y;
						refreshVisualWith(wayPoints);
					}

					@Override
					public void commitWayPoint(int wayPointIndex, Point p) {
						if (isCreate) {
							getModel().addWayPoint(wayPointIndex, p);
						} else {
							getModel().setWayPoint(wayPointIndex, p);
						}
					}

					@Override
					public void removeWayPoint(int wayPointIndex) {
						getModel().removeWayPoint(wayPointIndex);
					}
				});
	}

	@Override
	public FXGeometricCurve getModel() {
		return (FXGeometricCurve) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof FXGeometricCurve)) {
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
		// TODO: compare way points to identify if we need to refresh
		// use anchors as start and end point
		FXGeometricCurve curveVisual = getModel();
		List<Point> wayPoints = curveVisual.getWayPoints();
		refreshVisualWith(wayPoints);
	}

	private void refreshVisualWith(List<Point> wayPoints) {
		Point[] startEnd = computeStartEnd();
		if (startEnd.length == 2) {
			ArrayList<Point> points = new ArrayList<Point>(wayPoints.size() + 2);
			points.add(startEnd[0]);
			points.addAll(wayPoints);
			points.add(startEnd[1]);
			visual.setGeometry(new Polyline(points.toArray(new Point[0])));
		}
	}

	private Point[] computeStartEnd() {
		if (anchors.size() != 2) {
			return new Point[] {};
		}
		Node startNode = anchors.get(0).getAnchorage();
		Node endNode = anchors.get(1).getAnchorage();

		// compute reference points in local coordinate space
		Point startReference = JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						endNode.localToScene(endNode.getBoundsInLocal())))
				.getCenter();
		Point endReference = JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						startNode.localToScene(startNode.getBoundsInLocal())))
				.getCenter();

		// compute new anchor positions
		try {
			Point start = anchors.get(0).getPosition(this.getVisual(),
					startReference);
			Point end = anchors.get(1).getPosition(this.getVisual(),
					endReference);
			return new Point[] { start, end };
		} catch (IllegalArgumentException x) {
			return new Point[] { startReference, endReference };
		}
	}

	@Override
	public void attachVisualToAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
		anchors.add(anchor);
		anchor.addPropertyChangeListener(this);
	}

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
		anchors.remove(anchor);
		anchor.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(IAnchor.REPRESH)) {
			if (anchors.size() == 2) {
				refreshVisual();
			}
		}
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		return null;
	}

}
