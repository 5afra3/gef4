/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.projective.Vector3D;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a line (or linear curve).
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 */
public class Line extends BezierCurve {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Line}, which connects the two {@link Point}s
	 * given indirectly by their coordinates
	 * 
	 * @param x1
	 *            the x-coordinate of the start point
	 * @param y1
	 *            the y-coordinate of the start point
	 * @param x2
	 *            the x-coordinate of the end point
	 * @param y2
	 *            the y-coordinate of the end point
	 */
	public Line(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}

	/**
	 * Constructs a new {@link Line} which connects the two given {@link Point}s
	 * 
	 * @param p1
	 *            the start point
	 * @param p2
	 *            the end point
	 */
	public Line(Point p1, Point p2) {
		this(p1.x, p1.y, p2.x, p2.y);
	}

	@Override
	public boolean contains(Point p) {
		if (p == null) {
			return false;
		}

		double distance = Math.abs(new Straight(getP1(), getP2())
				.getSignedDistanceCCW(new Vector(p)));
		return PrecisionUtils.equal(distance, 0) && getBounds().contains(p);
	}

	/**
	 * Tests whether this {@link Line} is equal to the line given implicitly by
	 * the given point coordinates.
	 * 
	 * @param x1
	 *            the x-coordinate of the start point of the line to test
	 * @param y1
	 *            the y-coordinate of the start point of the line to test
	 * @param x2
	 *            the x-coordinate of the end point of the line to test
	 * @param y2
	 *            the y-coordinate of the end point of the line to test
	 * @return <code>true</code> if the given start and end point coordinates
	 *         are (imprecisely) equal to this {@link Line} 's start and end
	 *         point coordinates
	 */
	public boolean equals(double x1, double y1, double x2, double y2) {
		return PrecisionUtils.equal(x1, getX1())
				&& PrecisionUtils.equal(y1, getY1())
				&& PrecisionUtils.equal(x2, getX2())
				&& PrecisionUtils.equal(y2, getY2())
				|| PrecisionUtils.equal(x2, getX1())
				&& PrecisionUtils.equal(y2, getY1())
				&& PrecisionUtils.equal(x1, getX2())
				&& PrecisionUtils.equal(y1, getY2());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Line) {
			Line l = (Line) o;
			return equals(l.getX1(), l.getY1(), l.getX2(), l.getY2());
		}
		return false;
	}

	/**
	 * Returns the smallest {@link Rectangle} containing this {@link Line}'s
	 * start and end point
	 * 
	 * @see IGeometry#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return new Rectangle(getP1(), getP2());
	}

	/**
	 * Returns a new {@link Line}, which has the same start and end point
	 * coordinates as this one.
	 * 
	 * @return a new {@link Line} with the same start and end point coordinates
	 */
	@Override
	public Line getCopy() {
		return new Line(getP1(), getP2());
	}

	/**
	 * Returns the single intersection point between this {@link Line} and the
	 * given one, in case it exists. Note that even in case
	 * {@link Line#intersects} returns true, there may not be a single
	 * intersection point in case both lines overlap in more than one point.
	 * 
	 * @param l
	 *            the Line, for which to compute the intersection point
	 * @return the single intersection point between this {@link Line} and the
	 *         given one, in case it intersects, <code>null</code> instead
	 */
	public Point getIntersection(Line l) {
		Point p1 = getP1();
		Point p2 = getP2();

		// degenerated case
		if (p1.equals(p2)) {
			if (l.contains(p1)) {
				return p1;
			} else if (l.contains(p2)) {
				return p2;
			}
			return null;
		}

		Point lp1 = l.getP1();
		Point lp2 = l.getP2();

		// degenerated case
		if (lp1.equals(lp2)) {
			if (contains(lp1)) {
				return lp1;
			} else if (contains(lp2)) {
				return lp2;
			}
			return null;
		}

		Straight s1 = new Straight(p1, p2);
		Straight s2 = new Straight(lp1, lp2);

		if (s1.isParallelTo(s2)) {
			if (s1.contains(new Vector(lp1))) {
				// end-point-intersection? (no overlap)
				double u1 = s1.getParameterAt(lp1);
				double u2 = s1.getParameterAt(lp2);

				if (PrecisionUtils.equal(u1, 0) && u2 < u1
						|| PrecisionUtils.equal(u1, 1) && u2 > u1) {
					return lp1;
				} else if (PrecisionUtils.equal(u2, 0) && u1 < u2
						|| PrecisionUtils.equal(u2, 1) && u1 > u2) {
					return lp2;
				}
			}

			return null;
		}

		Point intersection = s1.getIntersection(s2).toPoint();
		return contains(intersection) && l.contains(intersection) ? intersection
				: null;
	}

	/**
	 * Provides an optimized version of the
	 * {@link BezierCurve#getIntersectionIntervalPairs(BezierCurve, Set)}
	 * method.
	 * 
	 * @param other
	 * @param intersections
	 * @return see
	 *         {@link BezierCurve#getIntersectionIntervalPairs(BezierCurve, Set)}
	 */
	public Set<IntervalPair> getIntersectionIntervalPairs(Line other,
			Set<Point> intersections) {
		Straight s1 = new Straight(this);
		Straight s2 = new Straight(other);
		Vector vi = s1.getIntersection(s2);
		if (vi != null) {
			Point pi = vi.toPoint();
			if (contains(pi)) {
				double param1 = s1.getParameterAt(pi);
				double param2 = s2.getParameterAt(pi);
				HashSet<IntervalPair> intervalPairs = new HashSet<IntervalPair>();
				intervalPairs.add(new IntervalPair(this, new Interval(param1,
						param1), other, new Interval(param2, param2)));
				return intervalPairs;
			}
		}
		return new HashSet<IntervalPair>();
	}

	@Override
	public Set<IntervalPair> getIntersectionIntervalPairs(BezierCurve other,
			Set<Point> intersections) {
		if (other instanceof Line) {
			return getIntersectionIntervalPairs((Line) other, intersections);
		}
		return super.getIntersectionIntervalPairs(other, intersections);
	}

	@Override
	public Point[] getIntersections(BezierCurve curve) {
		if (curve instanceof Line) {
			Point poi = getIntersection((Line) curve);
			if (poi != null) {
				return new Point[] { poi };
			}
			return new Point[] {};
		}
		return super.getIntersections(curve);
	}

	// TODO: add specialized getOverlap()

	/**
	 * Returns an array, which contains two {@link Point}s representing the
	 * start and end points of this {@link Line}
	 * 
	 * @return an array with two {@link Point}s, whose x and y coordinates match
	 *         those of this {@link Line}'s start and end point
	 */
	@Override
	public Point[] getPoints() {
		return new Point[] { getP1(), getP2() };
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public IGeometry getTransformed(AffineTransform localTransform) {
		Point[] transformed = localTransform.getTransformed(getPoints());
		return new Line(transformed[0], transformed[1]);
	}

	/**
	 * Provides an optimized version of the
	 * {@link BezierCurve#intersects(ICurve)} method.
	 * 
	 * @param l
	 * @return see {@link BezierCurve#intersects(ICurve)}
	 */
	public boolean intersects(Line l) {
		return getIntersection(l) != null;
	}

	@Override
	public boolean intersects(ICurve c) {
		if (c instanceof Line) {
			return intersects((Line) c);
		}
		return super.intersects(c);
	}

	@Override
	public boolean touches(IGeometry g) {
		if (g instanceof Line) {
			return touches((Line) g);
		}
		return super.touches(g);
	}

	/**
	 * Tests whether this {@link Line} and the given one share at least one
	 * common point.
	 * 
	 * @param l
	 *            The {@link Line} to test.
	 * @return <code>true</code> if this {@link Line} and the given one share at
	 *         least one common point, <code>false</code> otherwise.
	 */
	public boolean touches(Line l) {
		// TODO: optimize w.r.t. object creation

		/*
		 * 1) check degenerated (the start and end point imprecisely fall
		 * together) and special cases where the lines have to be regarded as
		 * intersecting, because they touch within the used imprecision, though
		 * they would not intersect with absolute precision.
		 */
		Point p1 = getP1();
		Point p2 = getP2();

		boolean touches = l.contains(p1) || l.contains(p2);
		if (touches || p1.equals(p2)) {
			return touches;
		}

		Point lp1 = l.getP1();
		Point lp2 = l.getP2();

		touches = contains(lp1) || contains(lp2);
		if (touches || lp1.equals(lp2)) {
			return touches;
		}
		Vector3D l1 = new Vector3D(p1).getCrossProduct(new Vector3D(p2));
		Vector3D l2 = new Vector3D(lp1).getCrossProduct(new Vector3D(lp2));

		/*
		 * 2) non-degenerated case. If the two respective straight lines
		 * intersect, the intersection has to be contained by both line segments
		 * for the segments to intersect. If the two respective straight lines
		 * do not intersect, because they are parallel, the getIntersection()
		 * method returns null.
		 */
		Point intersection = l1.getCrossProduct(l2).toPoint();
		return intersection != null && contains(intersection)
				&& l.contains(intersection);
	}

	/**
	 * Tests whether this {@link Line} and the given other {@link Line} overlap,
	 * i.e. they share an infinite number of {@link Point}s.
	 * 
	 * @param l
	 *            the other {@link Line} to test for overlap with this
	 *            {@link Line}
	 * @return <code>true</code> if this {@link Line} and the other {@link Line}
	 *         overlap, otherwise <code>false</code>
	 * @see ICurve#overlaps(ICurve)
	 */
	public boolean overlaps(Line l) {
		return touches(l) && !intersects(l);
	}

	@Override
	public boolean overlaps(BezierCurve c) {
		if (c instanceof Line) {
			return overlaps((Line) c);
		}

		// BezierCurve: in order to overlap, all control points have to lie on a
		// straight through its base line
		Straight s = new Straight(this);
		for (Line seg : PointListUtils.toSegmentsArray(c.getPoints(), false)) {
			if (!s.equals(new Straight(seg))) {
				return false;
			}
		}

		// if the base line overlaps, we are done
		if (overlaps(new Line(c.getP1(), c.getP2()))) {
			return true;
		} else {
			// otherwise, we have to delegate to the general implementation for
			// Bezier curves to take care of a degenerated curve, where the
			// handle points are outside the base line of the Bezier curve.
			return super.touches(c);
		}
	}

	/**
	 * Initializes this {@link Line} with the given start and end point
	 * coordinates
	 * 
	 * @param x1
	 *            the x-coordinate of the start point
	 * @param y1
	 *            the y-coordinate of the start point
	 * @param x2
	 *            the x-coordinate of the end point
	 * @param y2
	 *            the y-coordinate of the end point
	 */
	public void setLine(double x1, double y1, double x2, double y2) {
		setP1(new Point(x1, y1));
		setP2(new Point(x2, y2));
	}

	/**
	 * Initializes this {@link Line} with the start and end point coordinates of
	 * the given one.
	 * 
	 * @param l
	 *            the {@link Line} whose start and end point coordinates should
	 *            be used for initialization
	 */
	public void setLine(Line l) {
		setLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
	}

	/**
	 * Initializes this {@link Line} with the start and end point coordinates
	 * provided by the given points
	 * 
	 * @param p1
	 *            the Point whose coordinates should be used as the start point
	 *            coordinates of this {@link Line}
	 * @param p2
	 *            the Point whose coordinates should be used as the end point
	 *            coordinates of this {@link Line}
	 */
	public void setLine(Point p1, Point p2) {
		setLine(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Sets the x-coordinate of the start {@link Point} of this {@link Line} to
	 * the given value.
	 * 
	 * @param x1
	 */
	public void setX1(double x1) {
		setP1(new Point(x1, getY1()));
	}

	/**
	 * Sets the y-coordinate of the start {@link Point} of this {@link Line} to
	 * the given value.
	 * 
	 * @param y1
	 */
	public void setY1(double y1) {
		setP1(new Point(getX1(), y1));
	}

	/**
	 * Sets the x-coordinate of the end {@link Point} of this {@link Line} to
	 * the given value.
	 * 
	 * @param x2
	 */
	public void setX2(double x2) {
		setP2(new Point(x2, getY2()));
	}

	/**
	 * Sets the y-coordinate of the end {@link Point} of this {@link Line} to
	 * the given value.
	 * 
	 * @param y2
	 */
	public void setY2(double y2) {
		setP2(new Point(getX2(), y2));
	}

	/**
	 * @see IGeometry#toPath()
	 */
	@Override
	public Path toPath() {
		Path path = new Path();
		path.moveTo(getX1(), getY1());
		path.lineTo(getX2(), getY2());
		return path;
	}

	@Override
	public String toString() {
		return "Line: (" + getX1() + ", " + getY1() + ") -> (" + getX2() + ", " + getY2() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	/**
	 * Returns an integer array of dimension 4, whose values represent the
	 * integer-based coordinates of this {@link Line}'s start and end point.
	 * 
	 * @return an array containing integer values, which are obtained by casting
	 *         x1, y1, x2, y2
	 */
	public int[] toSWTPointArray() {
		return PointListUtils.toIntegerArray(PointListUtils
				.toCoordinatesArray(getPoints()));
	}

}
