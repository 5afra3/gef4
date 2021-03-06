/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXBoxHandlePart extends AbstractFXHandlePart {

	private Rectangle visual = null;
	private Pos pos;

	public FXBoxHandlePart(List<IContentPart<Node>> targetParts, Pos pos) {
		this.pos = pos;
		visual = new Rectangle();
		visual.setFill(Color.web("#d5faff"));
		visual.setStroke(Color.web("#5a61af"));
		visual.setWidth(5);
		visual.setHeight(5);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		Bounds unionedBoundsInScene = getUnionedBoundsInScene(getAnchorages());
		if (unionedBoundsInScene != null) {
			Bounds layoutBounds = visual.getParent().sceneToLocal(
					unionedBoundsInScene);
			double xInset = getXInset();
			double yInset = getYInset();
			if (Pos.TOP_LEFT == getPos()) {
				visual.setLayoutX(layoutBounds.getMinX() - xInset);
				visual.setLayoutY(layoutBounds.getMinY() - yInset);
			} else if (Pos.TOP_RIGHT == getPos()) {
				visual.setLayoutX(layoutBounds.getMaxX() - xInset);
				visual.setLayoutY(layoutBounds.getMinY() - yInset);
			} else if (Pos.BOTTOM_RIGHT == getPos()) {
				visual.setLayoutX(layoutBounds.getMaxX() - xInset);
				visual.setLayoutY(layoutBounds.getMaxY() - yInset);
			} else if (Pos.BOTTOM_LEFT == getPos()) {
				visual.setLayoutX(layoutBounds.getMinX() - xInset);
				visual.setLayoutY(layoutBounds.getMaxY() - yInset);
			} else {
				throw new IllegalArgumentException(
						"Unsupported position constant.");
			}
		}
	}

	protected double getXInset() {
		double xInset = visual.getWidth() / 2.0;
		return xInset;
	}

	protected double getYInset() {
		double yInset = visual.getHeight() / 2.0;
		return yInset;
	}

	private Bounds getUnionedBoundsInScene(List<IVisualPart<Node>> selection) {
		org.eclipse.gef4.geometry.planar.Rectangle unionedBoundsInScene = null;
		for (IVisualPart<Node> cp : selection) {
			Bounds boundsInScene = cp.getVisual().localToScene(
					cp.getVisual().getLayoutBounds());
			if (unionedBoundsInScene == null) {
				unionedBoundsInScene = JavaFX2Geometry
						.toRectangle(boundsInScene);
			} else {
				unionedBoundsInScene.union(JavaFX2Geometry
						.toRectangle(boundsInScene));
			}
		}
		if (unionedBoundsInScene != null) {
			return Geometry2JavaFX.toFXBounds(unionedBoundsInScene);
		} else {
			return null;
		}
	}

	public Pos getPos() {
		return pos;
	}

}
