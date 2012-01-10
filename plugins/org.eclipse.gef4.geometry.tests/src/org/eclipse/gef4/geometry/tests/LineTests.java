/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (Research Group Software Construction, RWTH Aachen University) - contribution for Bugzilla 245182
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.shapes.Line;
import org.eclipse.gef4.geometry.shapes.Rectangle;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.junit.Test;

/**
 * Unit tests for {@link Straight}.
 * 
 * @author anyssen
 * 
 */
public class LineTests {

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private static final double RECOGNIZABLE_FRACTION = PRECISION_FRACTION
			+ PRECISION_FRACTION / 10;
	private static final double UNRECOGNIZABLE_FRACTION = PRECISION_FRACTION
			- PRECISION_FRACTION / 10;

	@Test
	public void test_constructors() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.equals(new Line(new Point(), new Point(5, 0))));
	}

	@Test
	public void test_contains_Point() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.contains(l1.getP1()));
		assertTrue(l1.contains(l1.getP2()));
		for (double x = -5; x <= 10; x += 0.1) {
			assertTrue(l1.contains(new Point(x, 0)) == (PrecisionUtils
					.smallerEqual(0, x) && PrecisionUtils.smallerEqual(x, 5)));
		}

		l1 = new Line(0, 0, 0, 0);
		assertTrue(l1.contains(l1.getP1()));
		assertTrue(l1.contains(l1.getP2()));
		assertFalse(l1.contains(new Point(1, 1)));
	}

	@Test
	public void test_contains_Rect() {
		assertFalse(new Line(0, 0, 5, 0).contains(new Rectangle()));
		assertFalse(new Line(0, 0, 5, 0).contains(new Rectangle(0, 0, 5, 0)));
		assertFalse(new Line(0, 0, 5, 0).contains(new Rectangle(1, 1, 1, 1)));
	}

	@Test
	public void test_copy() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.equals(l1.getCopy()));
		assertTrue(l1.equals(l1.clone()));
		assertTrue(l1.getCopy().equals(l1.clone()));
	}

	@Test
	public void test_equals() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.equals(l1));
		assertTrue(l1.equals(new Line(0.0, 0.0, 5.0, 0.0)));
		assertTrue(l1.equals(new Line(5.0, 0.0, 0.0, 0.0)));
		assertFalse(l1.equals(new Line(0.1, 0.0, 5.0, 0.0)));
		assertFalse(l1.equals(new Line(0.0, 0.1, 5.0, 0.0)));
		assertFalse(l1.equals(new Line(0.0, 0.0, 5.1, 0.0)));
		assertFalse(l1.equals(new Line(0.0, 0.0, 5.0, 0.1)));
		assertFalse(l1.equals(new Point()));
		assertFalse(l1.equals(null));

		assertTrue(l1.equals(new Line(UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION, 5.0 + UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION)));
		assertTrue(l1.equals(new Line(-UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION, 5.0 - UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION)));
		assertFalse(l1.equals(new Line(RECOGNIZABLE_FRACTION,
				RECOGNIZABLE_FRACTION, 5.0 + RECOGNIZABLE_FRACTION,
				RECOGNIZABLE_FRACTION)));
		assertFalse(l1.equals(new Line(-RECOGNIZABLE_FRACTION,
				-RECOGNIZABLE_FRACTION, 5.0 - RECOGNIZABLE_FRACTION,
				-RECOGNIZABLE_FRACTION)));
	}

	@Test
	public void test_getBounds() {
		Line l1 = new Line(0, 0, 5, 0);
		Rectangle bounds = l1.getBounds();
		assertTrue(bounds.getLeft().equals(l1.getP1()));
		assertTrue(bounds.getRight().equals(l1.getP2()));
		assertTrue(bounds.getTopLeft().equals(l1.getP1()));
		assertTrue(bounds.getBottomLeft().equals(l1.getP1()));
		assertTrue(bounds.getTopRight().equals(l1.getP2()));
		assertTrue(bounds.getBottomRight().equals(l1.getP2()));

		l1 = new Line(-5, -5, 5, 5);
		bounds = l1.getBounds();
		assertTrue(bounds.getTopLeft().equals(l1.getP1()));
		assertTrue(bounds.getBottomRight().equals(l1.getP2()));

		l1 = new Line(-5, 5, 5, -5);
		bounds = l1.getBounds();
		assertTrue(bounds.getBottomLeft().equals(l1.getP1()));
		assertTrue(bounds.getTopRight().equals(l1.getP2()));
	}

	@Test
	public void test_getIntersection_with_Line() {
		// simple intersection
		Line l1 = new Line(0, 0, 4, 4);
		Line l2 = new Line(0, 4, 4, 0);
		assertTrue(l1.intersects(l2));
		assertTrue(l1.getIntersection(l2).equals(new Point(2, 2)));
		assertTrue(l2.getIntersection(l1).equals(new Point(2, 2)));

		// lines touch in one point
		Line l3 = new Line(4, 4, 7, 9);
		assertTrue(l1.getIntersection(l3).equals(new Point(4, 4)));
		assertTrue(l3.getIntersection(l1).equals(new Point(4, 4)));

		// lines overlap
		Line l4 = new Line(2, 2, 6, 6);
		assertTrue(l1.getIntersection(l4) == null);
		assertTrue(l4.getIntersection(l1) == null);

		// lines overlap in one end point
		Line l5 = new Line(4, 4, 6, 6);
		assertTrue(l1.getIntersection(l5).equals(new Point(4, 4)));
		assertTrue(l5.getIntersection(l1).equals(new Point(4, 4)));
		l5 = new Line(6, 6, 4, 4);
		assertTrue(l1.getIntersection(l5).equals(new Point(4, 4)));
		assertTrue(l5.getIntersection(l1).equals(new Point(4, 4)));

		Line l6 = new Line(-1, -1, 0, 0);
		assertTrue(l1.getIntersection(l6).equals(new Point()));
		assertTrue(l6.getIntersection(l1).equals(new Point()));
		l6 = new Line(0, 0, -1, -1);
		assertTrue(l1.getIntersection(l6).equals(new Point()));
		assertTrue(l6.getIntersection(l1).equals(new Point()));

		// lines do not intersect
		Line l7 = new Line(4, 0, 5, 4);
		assertNull(l1.getIntersection(l7));
		assertNull(l7.getIntersection(l1));
	}

	@Test
	public void test_getters() {
		for (double x1 = -2; x1 <= 2; x1 += 0.2) {
			for (double y1 = -2; y1 <= 2; y1 += 0.2) {
				Point p1 = new Point(x1, y1);

				for (double x2 = -2; x2 <= 2; x2 += 0.2) {
					for (double y2 = -2; y2 <= 2; y2 += 0.2) {
						Point p2 = new Point(x2, y2);
						Line line = new Line(p1, p2);
						assertTrue(line.getP1().equals(p1));
						assertTrue(line.getP2().equals(p2));
						assertTrue(PrecisionUtils.equal(line.getX1(), p1.x));
						assertTrue(PrecisionUtils.equal(line.getX2(), p2.x));
						assertTrue(PrecisionUtils.equal(line.getY1(), p1.y));
						assertTrue(PrecisionUtils.equal(line.getY2(), p2.y));

						Point[] points = line.getPoints();
						assertTrue(points[0].equals(p1));
						assertTrue(points[1].equals(p2));
					}
				}
			}
		}
	}

	@Test
	public void test_hashCode() {
		// hashCode() has to be the same for two equal()ly lines
		// (I think this is impossible with the current PrecisionUtils.)
		Line l1 = new Line(0, 0, 5, 0);

		assertEquals(l1.hashCode(), l1.hashCode());
		assertEquals(l1.hashCode(), new Line(0.0, 0.0, 5.0, 0.0).hashCode());

		assertTrue(l1.hashCode() == new Line(UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION, 5.0 + UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION).hashCode());
		assertTrue(l1.hashCode() == new Line(-UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION, 5.0 - UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION).hashCode());
	}

	@Test
	public void test_intersects_with_Line() {
		// simple intersection
		Line l1 = new Line(0, 0, 4, 4);
		Line l2 = new Line(0, 4, 4, 0);
		assertTrue(l1.intersects(l2));
		assertTrue(l2.intersects(l1));

		// lines touch in one point
		Line l3 = new Line(4, 4, 7, 9);
		assertTrue(l1.intersects(l3));
		assertTrue(l3.intersects(l1));

		// lines overlap
		Line l4 = new Line(2, 2, 6, 6);
		assertTrue(l1.intersects(l4));
		assertTrue(l4.intersects(l1));

		// one line is a single point
		Line l5 = new Line(1, 1, 1, 1);
		assertTrue(l5.intersects(l1));
		assertTrue(l1.intersects(l5));

		// straights would intersect, but these lines do not
		Line l6 = new Line(4, 0, 5, 4);
		assertFalse(l6.intersects(l1));
		assertFalse(l1.intersects(l6));
	}

	@Test
	public void test_intersects_with_Rect() {
		Line l1 = new Line(0, 0, 4, 4);
		Rectangle r1 = new Rectangle(0, 4, 4, 4);
		assertTrue(l1.intersects(r1));
	}

	@Test
	public void test_setters() {
		for (double x1 = -2; x1 <= 2; x1 += 0.2) {
			for (double y1 = -2; y1 <= 2; y1 += 0.2) {
				Point p1 = new Point(x1, y1);

				for (double x2 = -2; x2 <= 2; x2 += 0.2) {
					for (double y2 = -2; y2 <= 2; y2 += 0.2) {
						Point p2 = new Point(x2, y2);

						Line line = new Line(new Point(-5, -5), new Point(-10,
								-10));

						assertFalse(line.getP1().equals(p1));
						assertFalse(line.getP2().equals(p2));

						line.setP1(p1);
						line.setP2(p2);

						assertTrue(line.getP1().equals(p1));
						assertTrue(line.getP2().equals(p2));

						line.setX1(-5);
						line.setY1(-5);
						assertTrue(line.getP1().equals(new Point(-5, -5)));

						line.setX2(5);
						line.setY2(5);
						assertTrue(line.getP2().equals(new Point(5, 5)));

						Line l2 = new Line(p1, p2);
						line.setLine(l2);
						assertTrue(line.equals(l2));

						l2 = new Line(p2, p1);
						line.setLine(p2, p1);
						assertTrue(line.equals(l2));

						line.setLine(0, 1, 2, 3);
						assertTrue(line.equals(new Line(0, 1, 2, 3)));
					}
				}
			}
		}
	}

	@Test
	public void test_toString() {
		Line l1 = new Line(0, 0, 5, 0);
		assertEquals("Line: (0.0, 0.0) -> (5.0, 0.0)", l1.toString());
	}

	@Test
	public void test_toSWTPointArray() {
		Line l1 = new Line(0.9, 0.1, 1.1, 2.9);
		int[] ints = l1.toSWTPointArray();
		assertEquals(0, ints[0]);
		assertEquals(0, ints[1]);
		assertEquals(1, ints[2]);
		assertEquals(2, ints[3]);
	}

}
