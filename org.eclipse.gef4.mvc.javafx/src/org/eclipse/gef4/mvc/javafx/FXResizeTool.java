package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.aspects.resizerelocate.AbstractResizeRelocateTool;
import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXResizeTool extends AbstractResizeRelocateTool<Node> {

	private Pos pos;
	private IEditDomain<Node> domain = null;

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> handlePart = getDomain().getViewer()
					.getVisualPartMap().get(event.getTarget());
			if (handlePart instanceof FXHandlePart) {
				pos = ((FXHandlePart) handlePart).getPos();
			} else {
				pos = Pos.BOTTOM_RIGHT;
			}
			initResize(new Point(event.getSceneX(),
					event.getSceneY()));
		}
	};

	private EventHandler<MouseEvent> draggedFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			performResize(new Point(event.getSceneX(), event.getSceneY()));
		}
	};

	private EventHandler<MouseEvent> releasedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// TODO: the resize tool should not know anything about this
			// this responsibility should either be placed in the domain, the
			// viewer, the selection tool or the handle tool (the most
			// appropriate location probably)
			// the handle tool should in this case also push the handle tool to
			// the stack
			domain = getDomain(); // we need this to properly unregister
			commitResize(new Point(event.getSceneX(), event.getSceneY()));
			domain.popTool(); // remove ourselves from the tool stack
			pos = null;
		}

	};

	@Override
	public void activate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedFilter);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

	@Override
	public void deactivate() {
		// TODO: proper handling of domain registration
		((FXViewer) domain.getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) domain.getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedFilter);
		((FXViewer) domain.getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
		domain = null;
	}

	@Override
	protected Rectangle getVisualBounds(IContentPart<Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}
		return toRectangle(contentPart.getVisual().localToScene(
				contentPart.getVisual().getBoundsInLocal()));
	}

	private Rectangle toRectangle(Bounds bounds) {
		return new Rectangle(bounds.getMinX(), bounds.getMinY(),
				bounds.getWidth(), bounds.getHeight());
	}

	@Override
	protected ReferencePoint getReferencePoint() {
		if (pos == Pos.BOTTOM_RIGHT) {
			return ReferencePoint.BOTTOM_RIGHT;
		} else if (pos == Pos.BOTTOM_LEFT) {
			return ReferencePoint.BOTTOM_LEFT;
		} else if (pos == Pos.TOP_RIGHT) {
			return ReferencePoint.TOP_RIGHT;
		} else if (pos == Pos.TOP_LEFT) {
			return ReferencePoint.TOP_LEFT;
		} else {
			throw new IllegalStateException("unknown Pos!");
		}
	}

}
