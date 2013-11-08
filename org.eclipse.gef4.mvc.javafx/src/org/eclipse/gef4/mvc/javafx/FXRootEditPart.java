package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.eclipse.gef4.mvc.parts.AbstractRootEditPart;
import org.eclipse.gef4.mvc.parts.IContentsEditPart;
import org.eclipse.gef4.mvc.parts.INodeEditPart;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public class FXRootEditPart extends AbstractRootEditPart<Node> {

	private Group layers;
	private Pane primaryLayer;

	public FXRootEditPart() {
		layers = new Group();
		primaryLayer = new Pane();
		primaryLayer.setPrefSize(1024, 768);
		layers.getChildren().add(primaryLayer);
	}

	public Pane getPrimaryLayer() {
		return primaryLayer;
	}

	@Override
	public void setViewer(IEditPartViewer<Node> newViewer) {
		if (getViewer() != null) {
			unregisterVisual();
		}
		if (!(newViewer instanceof FXViewer)) {
			throw new IllegalArgumentException();
		}
		super.setViewer(newViewer);
		if (getViewer() != null) {
			registerVisual();
		}
	}

	@Override
	public FXViewer getViewer() {
		return (FXViewer) super.getViewer();
	}

	@Override
	public void refreshVisual() {
		// nothing to do
	}

	@Override
	protected void registerVisual() {
		getViewer().getVisualPartMap().put(layers, this);
	}

	@Override
	protected void unregisterVisual() {
		getViewer().getVisualPartMap().remove(layers);
	}

	@Override
	public Node getVisual() {
		return layers;
	}

	@Override
	protected void addChildVisual(IContentsEditPart<Node> child, int index) {
		primaryLayer.getChildren().add(index, child.getVisual());
	}

	@Override
	protected void removeChildVisual(IContentsEditPart<Node> child) {
		primaryLayer.getChildren().remove(child);
	}

	@Override
	protected boolean isNodeModel(Object model) {
		return true;
	}

	@Override
	protected boolean isConnectionModel(Object model) {
		return false;
	}

}
