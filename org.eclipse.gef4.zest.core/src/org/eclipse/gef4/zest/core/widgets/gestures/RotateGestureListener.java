/*******************************************************************************
 * Copyright 2012, Zoltan Ujhelyi. All rights reserved. This program and the 
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Zoltan Ujhelyi
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets.gestures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Transform;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.GraphWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;

/**
 * A gesture listener for rotate gestures in Graph widgets.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class RotateGestureListener implements GestureListener {

	GraphWidget graph;
	double rotate;
	List<GraphItem> nodes;
	List<Point> originalLocations;
	double xCenter, yCenter;

	void storePosition(List<GraphItem> nodes) {
		originalLocations = new ArrayList<Point>();
		Iterator<GraphItem> it = nodes.iterator();
		Transform t = new Transform();
		t.setTranslation(-xCenter, -yCenter);
		while (it.hasNext()) {
			GraphNode node = (GraphNode) it.next();
			originalLocations.add(t.getTransformed(node.getLocation()));
		}
	}

	void updatePositions(double rotation) {
		Transform t = new Transform();
		t.setRotation(rotation);
		t.setTranslation(xCenter, yCenter);
		for (int i = 0; i < nodes.size(); i++) {
			GraphNode node = (GraphNode) nodes.get(i);
			Point p = (Point) originalLocations.get(i);
			Point rot = t.getTransformed(p);
			node.setLocation(rot.preciseX(), rot.preciseY());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.GestureListener#gesture(org.eclipse.swt.events
	 * .GestureEvent)
	 */
	public void gesture(GestureEvent e) {
		if (!(e.widget instanceof GraphWidget)) {
			return;
		}
		switch (e.detail) {
		case SWT.GESTURE_BEGIN:
			graph = (GraphWidget) e.widget;
			rotate = 0.0;
			nodes = graph.getSelection();
			if (nodes.isEmpty()) {
				nodes.addAll(graph.getNodes());
			}
			xCenter = 0;// e.x;
			yCenter = 0;// e.y;
			Iterator<GraphItem> it = nodes.iterator();
			while (it.hasNext()) {
				GraphNode node = (GraphNode) it.next();
				Point location = node.getLocation();
				xCenter += location.preciseX();
				yCenter += location.preciseY();
			}
			xCenter = xCenter / nodes.size();
			yCenter = yCenter / nodes.size();
			storePosition(nodes);
			break;
		case SWT.GESTURE_END:
			break;
		case SWT.GESTURE_ROTATE:
			updatePositions(e.rotation / 2 / Math.PI);
			break;
		default:
			// Do nothing
		}
	}

}
