package org.eclipse.gef4.mvc.fx.parts;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;

abstract public class AbstractFXHandlePart extends AbstractHandlePart<Node> {

	private ChangeListener<Object> changeListener = new ChangeListener<Object>() {
		@Override
		public void changed(ObservableValue<?> observable, Object oldValue,
				Object newValue) {
			refreshVisual();
		}
	};

	public void attachVisualToAnchorageVisual(Node anchorageVisual,
			org.eclipse.gef4.mvc.anchors.IAnchor<Node> anchor) {
		anchorageVisual.layoutXProperty().addListener(changeListener);
		anchorageVisual.layoutYProperty().addListener(changeListener);
		anchorageVisual.layoutBoundsProperty().addListener(changeListener);
	};

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
		anchorageVisual.layoutXProperty().removeListener(changeListener);
		anchorageVisual.layoutYProperty().removeListener(changeListener);
		anchorageVisual.layoutBoundsProperty().removeListener(changeListener);
	}

}
