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
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;

public class FXAnchorPointHandlePart extends AbstractFXHandlePart {

	private Rectangle visual;
	private Point startPoint;
	private Point currentPosition;
	private int wayPointIndex;

	public FXAnchorPointHandlePart(int index, Point wayPoint) {
		this.wayPointIndex = index;
		startPoint = wayPoint;
		currentPosition = new Point(startPoint);
		visual = new Rectangle(5, 5);
		visual.setTranslateY(-visual.getHeight() / 2);
		visual.setFill(new LinearGradient(0, 0, 0, 5, true,
				CycleMethod.NO_CYCLE, new Stop[] {
						new Stop(0.0, Color.web("#e4fbff")),
						new Stop(0.5, Color.web("#a5d3fb")),
						new Stop(1.0, Color.web("#d5faff")) }));
		visual.setStroke(Color.web("#5a61af"));
		installBound(AbstractFXDragPolicy.class, new AbstractFXDragPolicy() {
			@Override
			public void drag(Point mouseLocation, Dimension delta) {
				AbstractWayPointPolicy policy = getPolicy();
				currentPosition = startPoint.getTranslated(delta.width,
						delta.height);
				policy.updateWayPoint(wayPointIndex, currentPosition);
			}

			@Override
			public void press(Point mouseLocation) {
				AbstractWayPointPolicy policy = getPolicy();
				policy.selectWayPoint(wayPointIndex);
			}

			@Override
			public void release(Point mouseLocation, Dimension delta) {
				AbstractWayPointPolicy policy = getPolicy();
				currentPosition = startPoint.getTranslated(delta.width,
						delta.height);
				policy.commitWayPoint(wayPointIndex, currentPosition);
			}
		});
	}

	protected AbstractWayPointPolicy getPolicy() {
		return getAnchorages().get(0).getBound(
				AbstractWayPointPolicy.class);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		visual.setLayoutX(currentPosition.x);
		visual.setLayoutY(currentPosition.y);
	}

}
