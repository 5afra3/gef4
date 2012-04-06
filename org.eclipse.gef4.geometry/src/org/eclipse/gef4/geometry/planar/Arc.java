/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.CurveUtils;

/**
 * Represents the geometric shape of an arc, which is defined by its enclosing
 * framing rectangle, a start angle (relative to the x-axis), and an angular
 * extend (in CCW direction).
 * 
 * @author anyssen
 * 
 */
public final class Arc extends AbstractRectangleBasedGeometry<Arc> implements
		ICurve {

	private static final long serialVersionUID = 1L;

	// TODO: move to utilities
	private static final Path toPath(CubicCurve... curves) {
		Path p = new Path();
		for (int i = 0; i < curves.length; i++) {
			if (i == 0) {
				p.moveTo(curves[i].getX1(), curves[i].getY1());
			}
			p.curveTo(curves[i].getCtrlX1(), curves[i].getCtrlY1(),
					curves[i].getCtrlX2(), curves[i].getCtrlY2(),
					curves[i].getX2(), curves[i].getY2());
		}
		return p;
	}

	private Angle startAngle;
	private Angle angularExtent;

	/**
	 * Constructs a new {@link Arc} so that it is fully contained within the
	 * framing rectangle defined by (x, y, width, height), spanning the given
	 * extend (in CCW direction) from the given start angle (relative to the
	 * x-axis).
	 * 
	 * @param x
	 *            the x-coordinate of the framing rectangle
	 * @param y
	 *            the y-coordinate of the framing rectangle
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param angularExtent
	 */
	public Arc(double x, double y, double width, double height,
			Angle startAngle, Angle angularExtent) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.startAngle = startAngle;
		this.angularExtent = angularExtent;
	}

	private CubicCurve computeApproximation(double start, double end) {
		// compute major and minor axis length
		double a = width / 2;
		double b = height / 2;

		// // calculate start and end points of the arc from start to end
		Point startPoint = new Point(x + a + a * Math.cos(start), y + b - b
				* Math.sin(start));
		Point endPoint = new Point(x + a + a * Math.cos(end), y + b - b
				* Math.sin(end));

		// approximation by cubic Bezier according to approximation provided in:
		// http://www.spaceroots.org/documents/ellipse/elliptical-arc.pdf
		double t = Math.tan((end - start) / 2);
		double alpha = Math.sin(end - start)
				* (Math.sqrt(4.0d + 3.0d * t * t) - 1) / 3;
		Point controlPoint1 = new Point(startPoint.x + alpha * -a
				* Math.sin(start), startPoint.y - alpha * b * Math.cos(start));
		Point controlPoint2 = new Point(
				endPoint.x - alpha * -a * Math.sin(end), endPoint.y + alpha * b
						* Math.cos(end));

		Point[] points = new Point[] { startPoint, controlPoint1,
				controlPoint2, endPoint };
		return new CubicCurve(points);
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		return false;
	}

	/**
	 * Returns the extension {@link Angle} of this {@link Arc}, i.e. the
	 * {@link Angle} defining the span of the {@link Arc}.
	 * 
	 * @return the extension {@link Angle} of this {@link Arc}
	 */
	public Angle getAngularExtent() {
		return angularExtent;
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#getCopy()
	 */
	public Arc getCopy() {
		return new Arc(x, y, width, height, startAngle, angularExtent);
	}

	public Point[] getIntersections(ICurve g) {
		return CurveUtils.getIntersections(this, g);
	}

	/**
	 * Returns a {@link Point} representing the start point of this {@link Arc}.
	 * 
	 * @return the start {@link Point} of this {@link Arc}
	 */
	public Point getP1() {
		return getPoint(Angle.fromRad(0));
	}

	public Point getP2() {
		return getPoint(angularExtent);
	}

	/**
	 * Computes a {@link Point} on this {@link Arc}. The {@link Point}'s
	 * coordinates are calculated by moving the given {@link Angle} on the
	 * {@link Arc} starting at the {@link Arc} start {@link Point}.
	 * 
	 * @param angularExtent
	 * @return the {@link Point} at the given {@link Angle}
	 */
	public Point getPoint(Angle angularExtent) {
		double a = width / 2;
		double b = height / 2;

		// // calculate start and end points of the arc from start to end
		return new Point(x + a + a
				* Math.cos(startAngle.rad() + angularExtent.rad()), y + b - b
				* Math.sin(startAngle.rad() + angularExtent.rad()));
	}

	/**
	 * Returns this {@link Arc}'s start {@link Angle}.
	 * 
	 * @return this {@link Arc}'s start {@link Angle}
	 */
	public Angle getStartAngle() {
		return startAngle;
	}

	public double getX1() {
		return getP1().x;
	}

	public double getX2() {
		return getP2().x;
	}

	public double getY1() {
		return getP1().y;
	}

	public double getY2() {
		return getP2().y;
	}

	public boolean intersects(ICurve c) {
		return CurveUtils.getIntersections(this, c).length > 0;
	}

	public boolean overlaps(ICurve c) {
		for (BezierCurve seg1 : toBezier()) {
			if (seg1.overlaps(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the extension {@link Angle} of this {@link Arc}.
	 * 
	 * @param angularExtent
	 *            the new extension {@link Angle} for this {@link Arc}
	 */
	public void setAngularExtent(Angle angularExtent) {
		this.angularExtent = angularExtent;
	}

	/**
	 * Sets the start {@link Angle} of this {@link Arc}.
	 * 
	 * @param startAngle
	 *            the new start {@link Angle} for this {@link Arc}
	 */
	public void setStartAngle(Angle startAngle) {
		this.startAngle = startAngle;
	}

	public CubicCurve[] toBezier() {
		double start = getStartAngle().rad();
		double end = getStartAngle().rad() + getAngularExtent().rad();

		// approximation is for arcs with angle < 90 degrees, so we may have to
		// split the arc into up to 4 cubic curves
		List<CubicCurve> segments = new ArrayList<CubicCurve>();
		if (angularExtent.deg() <= 90.0) {
			segments.add(computeApproximation(start, end));
		} else {
			// two or more segments, the first will be an ellipse segment
			// approximation
			segments.add(computeApproximation(start, start + Math.PI / 2));
			if (angularExtent.deg() <= 180.0) {
				// two segments, calculate the second (which is below 90
				// degrees)
				segments.add(computeApproximation(start + Math.PI / 2, end));
			} else {
				// three or more segments, so calculate the second one
				segments.add(computeApproximation(start + Math.PI / 2, start
						+ Math.PI));
				if (angularExtent.deg() <= 270.0) {
					// three segments, calculate the third (which is below 90
					// degrees)
					segments.add(computeApproximation(start + Math.PI, end));
				} else {
					// four segments (fourth below 90 degrees), so calculate the
					// third and fourth
					segments.add(computeApproximation(start + Math.PI, start
							+ 3 * Math.PI / 2));
					segments.add(computeApproximation(start + 3 * Math.PI / 2,
							end));
				}
			}
		}
		return segments.toArray(new CubicCurve[] {});
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		return toPath(toBezier());
	}
}
