/*******************************************************************************
 * Copyright (c) 2011, 2012 itemis AG and others.
 * 
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

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.geometry.convert.AWT2Geometry;
import org.eclipse.gef4.geometry.convert.Geometry2AWT;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a path, which may consist of independent
 * subgraphs.
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 * 
 */
public class Path extends AbstractGeometry implements IGeometry {

	/**
	 * Representation for different types of {@link Segment}s.
	 * 
	 * @see #MOVE_TO
	 * @see #LINE_TO
	 * @see #QUAD_TO
	 * @see #CUBIC_TO
	 */
	public class Segment {

		/**
		 * A {@link #MOVE_TO} {@link Segment} represents a change of position
		 * while piecewise building a {@link Path}, without inserting a new
		 * curve.
		 * 
		 * @see Path#moveTo(double, double)
		 */
		public static final int MOVE_TO = 0;

		/**
		 * A {@link #LINE_TO} {@link Segment} represents a {@link Line} from the
		 * previous position of a {@link Path} to the {@link Point} at index 0
		 * associated with the {@link Segment}.
		 * 
		 * @see Path#lineTo(double, double)
		 */
		public static final int LINE_TO = 1;

		/**
		 * A {@link #QUAD_TO} {@link Segment} represents a
		 * {@link QuadraticCurve} from the previous position of a {@link Path}
		 * to the {@link Point} at index 1 associated with the {@link Segment}.
		 * The {@link Point} at index 0 is used as the handle {@link Point} of
		 * the {@link QuadraticCurve}.
		 * 
		 * @see Path#quadTo(double, double, double, double)
		 */
		public static final int QUAD_TO = 2;

		/**
		 * A {@link #CUBIC_TO} {@link Segment} represents a {@link CubicCurve}
		 * from the previous position of a {@link Path} to the {@link Point} at
		 * index 2 associated with the {@link Segment}. The {@link Point}s at
		 * indices 0 and 1 are used as the handle {@link Point}s of the
		 * {@link CubicCurve}.
		 * 
		 * @see Path#cubicTo(double, double, double, double, double, double)
		 */
		public static final int CUBIC_TO = 3;

		/**
		 * A {@link #CLOSE} {@link Segment} represents the link from the current
		 * position of a {@link Path} to the position of the last
		 * {@link #MOVE_TO} {@link Segment}.
		 * 
		 * @see Path#close()
		 */
		public static final int CLOSE = 4;

		private int type;
		private Point[] points;

		/**
		 * Constructs a new {@link Segment} of the given type. The passed-in
		 * {@link Point}s are associated with this {@link Segment}.
		 * 
		 * @param type
		 *            The type of the new {@link Segment}. It is one of
		 *            <ul>
		 *            <li>{@link #MOVE_TO}</li>
		 *            <li>{@link #LINE_TO}</li>
		 *            <li>{@link #QUAD_TO}</li>
		 *            <li>{@link #CUBIC_TO}</li>
		 *            </ul>
		 * @param points
		 *            the {@link Point}s to associate with this {@link Segment}
		 */
		public Segment(int type, Point... points) {
			switch (type) {
			case MOVE_TO:
				if (points == null || points.length != 1) {
					throw new IllegalArgumentException(
							"A Segment of type MOVE_TO has to be associate with exactly 1 point: new Segment("
									+ type + ", " + points + ")");
				}
				break;
			case LINE_TO:
				if (points == null || points.length != 1) {
					throw new IllegalArgumentException(
							"A Segment of type LINE_TO has to be associate with exactly 1 point: new Segment("
									+ type + ", " + points + ")");
				}
				break;
			case QUAD_TO:
				if (points == null || points.length != 2) {
					throw new IllegalArgumentException(
							"A Segment of type QUAD_TO has to be associate with exactly 2 points: new Segment("
									+ type + ", " + points + ")");
				}
				break;
			case CUBIC_TO:
				if (points == null || points.length != 3) {
					throw new IllegalArgumentException(
							"A Segment of type CUBIC_TO has to be associate with exactly 3 point: new Segment("
									+ type + ", " + points + ")");
				}
				break;
			default:
				throw new IllegalArgumentException(
						"You can only create Segments of types MOVE_TO, LINE_TO, QUAD_TO, or CUBIC_TO: new Segment("
								+ type + ", " + points + ")");
			}

			this.type = type;
			this.points = Point.getCopy(points);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Segment) {
				Segment s = (Segment) obj;
				if (s.type == type && Arrays.equals(s.points, points)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns a copy of this {@link Segment}. The associated {@link Point}s
		 * are copied, too.
		 * 
		 * @return a copy of this {@link Segment}
		 */
		public Segment getCopy() {
			return new Segment(type, getPoints());
		}

		/**
		 * Returns a copy of the {@link Point}s associated with this
		 * {@link Segment}.
		 * 
		 * @return a copy of the {@link Point}s associated with this
		 *         {@link Segment}.
		 */
		public Point[] getPoints() {
			return Point.getCopy(points);
		}

		/**
		 * Returns the type of this {@link Segment}.
		 * 
		 * @return the type of this {@link Segment}
		 * @see #MOVE_TO
		 * @see #LINE_TO
		 * @see #QUAD_TO
		 * @see #CUBIC_TO
		 */
		public int getType() {
			return type;
		}

		@Override
		public int hashCode() {
			return type;
		}

	}

	/**
	 * Winding rule for determining the interior of the {@link Path}. Indicates
	 * that a {@link Point} is regarded to lie inside the {@link Path}, if any
	 * ray starting in that {@link Point} and pointing to infinity crosses the
	 * {@link Segment}s of the {@link Path} an odd number of times.
	 */
	public static final int WIND_EVEN_ODD = 0;

	/**
	 * Winding rule for determining the interior of the {@link Path}. Indicates
	 * that a {@link Point} is regarded to lie inside the {@link Path}, if any
	 * ray starting from that {@link Point} and pointing to infinity is crossed
	 * by {@link Path} {@link Segment}s a different number of times in the
	 * counter-clockwise direction than in the clockwise direction.
	 */
	public static final int WIND_NON_ZERO = 1;

	private static final long serialVersionUID = 1L;

	private int windingRule = WIND_NON_ZERO;

	private List<Segment> segments = new ArrayList<Segment>();

	/**
	 * Creates a new empty path with a default winding rule of
	 * {@link #WIND_NON_ZERO}.
	 */
	public Path() {
	}

	/**
	 * Creates a new empty path with given winding rule.
	 * 
	 * @param windingRule
	 *            the winding rule to use; one of {@link #WIND_EVEN_ODD} or
	 *            {@link #WIND_NON_ZERO}
	 */
	public Path(int windingRule) {
		this.windingRule = windingRule;
	}

	/**
	 * Creates a path from the given segments, using the given winding rule.
	 * 
	 * @param windingRule
	 *            the winding rule to use; one of {@link #WIND_EVEN_ODD} or
	 *            {@link #WIND_NON_ZERO}
	 * @param segments
	 *            The segments to initialize the path with
	 */
	public Path(int windingRule, Segment... segments) {
		this(windingRule);
		for (Segment s : segments) {
			this.segments.add(s.getCopy());
		}
	}

	/**
	 * Creates a path from the given segments, using the default winding rule
	 * {@link #WIND_NON_ZERO}.
	 * 
	 * @param segments
	 *            The segments to initialize the path with
	 */
	public Path(Segment... segments) {
		this(WIND_NON_ZERO, segments);
	}

	/**
	 * Closes the current sub-path by drawing a straight line (line-to) to the
	 * location of the last move to.
	 */
	public final void close() {
		segments.add(new Segment(Segment.CLOSE));
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		return Geometry2AWT.toAWTPath(this)
				.contains(Geometry2AWT.toAWTPoint(p));
	}

	/**
	 * Returns <code>true</code> if the given {@link Rectangle} is contained
	 * within {@link IGeometry}, <code>false</code> otherwise.
	 * 
	 * TODO: Generalize to arbitrary {@link IGeometry} objects.
	 * 
	 * @param r
	 *            The {@link Rectangle} to test
	 * @return <code>true</code> if the {@link Rectangle} is fully contained
	 *         within this {@link IGeometry}
	 */
	public boolean contains(Rectangle r) {
		return Geometry2AWT.toAWTPath(this).contains(
				Geometry2AWT.toAWTRectangle(r));
	}

	/**
	 * Adds a cubic Bezier curve segment from the current position to the
	 * specified end position, using the two provided control points as Bezier
	 * control points.
	 * 
	 * @param control1X
	 *            The x-coordinate of the first Bezier control point
	 * @param control1Y
	 *            The y-coordinate of the first Bezier control point
	 * @param control2X
	 *            The x-coordinate of the second Bezier control point
	 * @param control2Y
	 *            The y-coordinate of the second Bezier control point
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void cubicTo(double control1X, double control1Y,
			double control2X, double control2Y, double x, double y) {
		segments.add(new Segment(Segment.CUBIC_TO, new Point(control1X,
				control1Y), new Point(control2X, control2Y), new Point(x, y)));
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		return AWT2Geometry.toRectangle(Geometry2AWT.toAWTPath(this)
				.getBounds2D());
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#getCopy()
	 */
	public Path getCopy() {
		return new Path(getWindingRule(), getSegments());
	}

	/**
	 * Returns the segments that make up this path.
	 * 
	 * @return an array of {@link Segment}s representing the segments of this
	 *         path
	 */
	public Segment[] getSegments() {
		Segment[] segments = new Segment[this.segments.size()];
		for (int i = 0; i < segments.length; i++) {
			segments[i] = this.segments.get(i).getCopy();
		}
		return segments;
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public IGeometry getTransformed(AffineTransform t) {
		return AWT2Geometry.toPath(new Path2D.Double(Geometry2AWT
				.toAWTPath(this), Geometry2AWT.toAWTAffineTransform(t)));
	}

	/**
	 * Returns the winding rule used to determine the interior of this path.
	 * 
	 * @return the winding rule, i.e. one of {@link #WIND_EVEN_ODD} or
	 *         {@link #WIND_NON_ZERO}
	 */
	public int getWindingRule() {
		return windingRule;
	}

	/**
	 * Adds a straight line segment from the current position to the specified
	 * end position.
	 * 
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void lineTo(double x, double y) {
		segments.add(new Segment(Segment.LINE_TO, new Point(x, y)));
	}

	/**
	 * Changes the current position without adding a new segment.
	 * 
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void moveTo(double x, double y) {
		segments.add(new Segment(Segment.MOVE_TO, new Point(x, y)));
	}

	/**
	 * Adds a quadratic curve segment from the current position to the specified
	 * end position, using the provided control point as a parametric control
	 * point.
	 * 
	 * @param controlX
	 *            The x-coordinate of the control point
	 * @param controlY
	 *            The y-coordinate of the control point
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 */
	public final void quadTo(double controlX, double controlY, double x,
			double y) {
		segments.add(new Segment(Segment.QUAD_TO,
				new Point(controlX, controlY), new Point(x, y)));
	}

	/**
	 * Resets the path to be empty.
	 */
	public final void reset() {
		segments.clear();
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		return getCopy();
	}

	/**
	 * Tests whether this {@link Path} and the given {@link Rectangle} touch,
	 * i.e. they have at least one {@link Point} in common.
	 * 
	 * @param r
	 *            the {@link Rectangle} to test for at least one {@link Point}
	 *            in common with this {@link Path}
	 * @return <code>true</code> if this {@link Path} and the {@link Rectangle}
	 *         touch, otherwise <code>false</code>
	 * @see IGeometry#touches(IGeometry)
	 */
	public boolean touches(Rectangle r) {
		return Geometry2AWT.toAWTPath(this).intersects(
				Geometry2AWT.toAWTRectangle(r));
	}

}
