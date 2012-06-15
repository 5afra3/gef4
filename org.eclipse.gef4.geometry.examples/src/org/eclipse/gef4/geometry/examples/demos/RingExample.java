/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.examples.demos;

import org.eclipse.gef4.geometry.examples.intersection.AbstractIntersectionExample;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Ring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class RingExample extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new RingExample("Ring Example");
	}

	public RingExample(String title) {
		super(title);
	}

	protected AbstractControllableShape createControllableShape1(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 100));
				addControlPoint(new Point(400, 100));
				addControlPoint(new Point(400, 200));

				addControlPoint(new Point(400, 100));
				addControlPoint(new Point(400, 400));
				addControlPoint(new Point(300, 400));

				addControlPoint(new Point(400, 400));
				addControlPoint(new Point(100, 400));
				addControlPoint(new Point(100, 300));

				addControlPoint(new Point(100, 400));
				addControlPoint(new Point(100, 100));
				addControlPoint(new Point(200, 100));
			}

			@Override
			public Ring createGeometry() {
				Point[] cp = getControlPoints();
				Polygon[] polygons = new Polygon[cp.length / 3];
				for (int i = 0; i < polygons.length; i++) {
					polygons[i] = new Polygon(cp[3 * i], cp[3 * i + 1],
							cp[3 * i + 2]);
					// System.out.println("R" + i + " " + cp[2 * i] + "\\"
					// + cp[2 * i + 1]);
				}
				Ring ring = new Ring(polygons);
				return ring;
			}

			@Override
			public void drawShape(GC gc) {
				Ring ring = createGeometry();

				gc.setAlpha(64);
				gc.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLUE));
				for (Polygon p : ring.getShapes()) {
					gc.fillPolygon(p.toSWTPointArray());
				}

				gc.setAlpha(255);
				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_RED));
				for (Polygon p : ring.getShapes()) {
					gc.drawPolygon(p.toSWTPointArray());
				}

				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLACK));
				int lineWidth = gc.getLineWidth();
				gc.setLineWidth(lineWidth + 2);
				for (Line l : ring.getOutlineSegments()) {
					gc.drawLine((int) (l.getX1()), (int) (l.getY1()),
							(int) (l.getX2()), (int) (l.getY2()));
				}
				gc.setLineWidth(lineWidth);

				// gc.setForeground(Display.getCurrent().getSystemColor(
				// SWT.COLOR_BLACK));
				// for (Line l : region.getOutlineSegments()) {
				// gc.drawLine((int) (l.getX1()), (int) (l.getY1()),
				// (int) (l.getX2()), (int) (l.getY2()));
				// }
			}
		};
	}

	protected AbstractControllableShape createControllableShape2(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
			}

			@Override
			public IGeometry createGeometry() {
				return new Line(-10, -10, -10, -10);
			}

			@Override
			public void drawShape(GC gc) {
			}
		};
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return new Point[] {};
	}
}
