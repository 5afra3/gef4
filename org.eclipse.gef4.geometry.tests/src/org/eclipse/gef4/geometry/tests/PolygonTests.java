/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Shatalin (Borland) - initial API and implementation
 *    Alexander Nyssen (itemis AG) - contribution for Bugzilla #162082
 *    Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *    
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.junit.Test;

/**
 * Unit tests for {@link Polygon}.
 * 
 * @author ahunter
 * @author anyssen
 * 
 */
public class PolygonTests {

	private static final Polygon CONCAVE_OCTAGON = new Polygon(new Point(0, 0),
			new Point(0, 4), new Point(2, 4), new Point(2, 2), new Point(4, 2),
			new Point(4, 4), new Point(6, 4), new Point(6, 0));

	private static final Polygon CONCAVE_PENTAGON = new Polygon(
			new Point(0, 0), new Point(0, 8), new Point(4, 4), new Point(8, 8),
			new Point(8, 0));

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private static final double RECOGNIZABLE_FRACTION = PRECISION_FRACTION
			+ PRECISION_FRACTION / 10;

	private static final Polygon RECTANGLE = new Polygon(new Point(0, 0),
			new Point(0, 2), new Point(2, 2), new Point(2, 0));

	private static final Polygon RHOMB = new Polygon(new Point(0, 2),
			new Point(2, 0), new Point(4, 2), new Point(2, 4));

	private static final double UNRECOGNIZABLE_FRACTION = PRECISION_FRACTION
			- PRECISION_FRACTION / 10;

	@Test
	public void test_constructors() {
		assertEquals(RHOMB, new Polygon(new Point(2, 0), new Point(4, 2),
				new Point(2, 4), new Point(0, 2)));
		assertEquals(RHOMB, new Polygon(new Point[] { new Point(2, 0),
				new Point(4, 2), new Point(2, 4), new Point(0, 2) }));
		assertEquals(RHOMB, new Polygon(2, 0, 4, 2, 2, 4, 0, 2));
		assertEquals(RHOMB,
				new Polygon(new double[] { 2, 0, 4, 2, 2, 4, 0, 2 }));
	}

	@Test
	public void test_contains_Ellipse() {
		assertTrue(RHOMB.contains(new Ellipse(1, 1, 2, 2)));
		assertFalse(RHOMB.contains(new Ellipse(0, 0, 4, 4)));
	}

	@Test
	public void test_contains_Line() {
		assertFalse(RHOMB.contains(new Line(-1, 1, 1, -1)));
		assertFalse(RHOMB.contains(new Line(-1, 2, 2, 2)));
		assertFalse(RHOMB.contains(new Line(2, 2, 5, 2)));
		assertTrue(RHOMB.contains(new Line(0, 2, 2, 0)));
		assertTrue(RHOMB.contains(new Line(0, 2, 2, 4)));
		assertTrue(RHOMB.contains(new Line(0, 2, 2, 2)));
		assertTrue(RHOMB.contains(new Line(1, 2, 3, 2)));
		assertTrue(new Polygon(new Point(), new Point(0, 5), new Point(5, 5),
				new Point(5, 0), new Point(2.5, 2.5)).contains(new Line(1, 2.5,
				4, 2.5)));
		assertFalse(new Polygon(new Point(), new Point(0, 5), new Point(5, 5),
				new Point(5, 0), new Point(2.5, 2.5)).contains(new Line(1, 2,
				4, 2)));
	}

	/**
	 * Testing points inside/outside the pentagon located on bottom concave
	 * tangent. Excluding points of CONCAVE_PENTAGON border - separate test
	 * present for it
	 */
	@Test
	public void test_contains_Point_BottomConcavePentagonTangentPoints() {
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(-1, 4)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(1, 4)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(5, 4)));
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(9, 4)));
	}

	/**
	 * Testing points inside/outside the rhomb located in bottop half. Excluding
	 * points of RHOMB border - separate test present for it
	 */
	@Test
	public void test_contains_Point_BottomRhombHalfPoints() {
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(0, 3)));
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(2, 3)));
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(4, 3)));
	}

	/**
	 * Testing points outside the rhomb located on bottom horizontal tangent
	 * line. Excluding points of RHOMB border - separate test present for it
	 */
	@Test
	public void test_contains_Point_BottomRhombTangentPoints() {
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(0, 4)));
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(4, 4)));
	}

	/**
	 * Testing points located of the horizontal line containing one of the
	 * "concave" edges
	 */
	@Test
	public void test_contains_Point_ConcaveOctagonBottomTangentPoints() {
		assertFalse("This point is outside the octagon",
				CONCAVE_OCTAGON.contains(new Point(-1, 2)));
		assertTrue("This point is inside the octagon",
				CONCAVE_OCTAGON.contains(new Point(0, 2)));
		assertTrue("This point is inside the octagon",
				CONCAVE_OCTAGON.contains(new Point(1, 2)));
		assertTrue("This point is inside the octagon",
				CONCAVE_OCTAGON.contains(new Point(2, 2)));
		assertTrue("This point is inside the octagon",
				CONCAVE_OCTAGON.contains(new Point(3, 2)));
		assertTrue("This point is inside the octagon",
				CONCAVE_OCTAGON.contains(new Point(4, 2)));
		assertTrue("This point is inside the octagon",
				CONCAVE_OCTAGON.contains(new Point(5, 2)));
		assertFalse("This point is outside the octagon",
				CONCAVE_OCTAGON.contains(new Point(7, 2)));
	}

	/**
	 * Testing points of CONCAVE_PENTAGON border - all vertexes + points on
	 * concave edges
	 */
	@Test
	public void test_contains_Point_ConcavePentagonBorderPoints() {
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(0, 8)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(2, 6)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(4, 4)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(6, 6)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(8, 8)));
	}

	/**
	 * Testing points inside/outside the pentagon located on equator of concave.
	 * Excluding points of CONCAVE_PENTAGON border - separate test present for
	 * it
	 */
	@Test
	public void test_contains_Point_ConcavePentagonEquatorPoints() {
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(-1, 6)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(1, 6)));
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(4, 6)));
		assertTrue("This point is inside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(7, 6)));
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(9, 6)));
	}

	@Test
	public void test_contains_Point_RectangleBorderPoints() {
		assertTrue("This point is a corner point of the rectangle",
				RECTANGLE.contains(new Point(0, 0)));
		assertTrue("This point is on the side of the rectangle",
				RECTANGLE.contains(new Point(0, 1)));
		assertTrue("This point is on the side of the rectangle",
				RECTANGLE.contains(new Point(0, 1.999999999999)));
		assertTrue("This point is a corner point of the rectangle",
				RECTANGLE.contains(new Point(0, 2)));
		assertTrue("This point is a corner point of the rectangle",
				RECTANGLE.contains(new Point(2, 2)));
		assertTrue("This point is a corner point of the rectangle",
				RECTANGLE.contains(new Point(2, 0)));
	}

	/**
	 * Testing points of RHOMB border - all vertexes + one point on the edge
	 */
	@Test
	public void test_contains_Point_RhombBorderPoints() {
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(0, 2)));
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(2, 4)));
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(2, 2)));
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(1, 1)));
	}

	/**
	 * Testing points inside/outside the rhomb located on the equator. Excluding
	 * points of RHOMB border - separate test present for it
	 */
	@Test
	public void test_contains_Point_RhombEquatorPoints() {
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(-1, 2)));
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(2, 2)));
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(5, 2)));
	}

	@Test
	public void test_contains_Point_special() {
		assertFalse(new Polygon(new Point[] {}).contains(new Point()));
		assertFalse(new Polygon(new Point()).contains(new Point(1, 1)));
		assertTrue(new Polygon(new Point(1, 1)).contains(1, 1));
		assertFalse(new Polygon(new Point(), new Point(1, 1)).contains(1, 0));
		assertTrue(new Polygon(new Point(), new Point(1, 1)).contains(0, 0));
	}

	/**
	 * Testing points outside the pentagon located on top concave tangent.
	 * Excluding points of CONCAVE_PENTAGON border - separate test present for
	 * it
	 */
	@Test
	public void test_contains_Point_TopConcavePentagonTangentPoints() {
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(-1, 8)));
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(4, 8)));
		assertFalse("This point is outside the pentagon",
				CONCAVE_PENTAGON.contains(new Point(9, 8)));
	}

	/**
	 * Testing points inside/outside the rhomb located in top half. Excluding
	 * points of RHOMB border - separate test present for it
	 */
	@Test
	public void test_contains_Point_TopRhombHalfPoints() {
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(0, 1)));
		assertTrue("This point is inside the rhomb",
				RHOMB.contains(new Point(2, 1)));
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(4, 1)));
	}

	/**
	 * Testing points outside the rhomb located on top horizontal tangent line.
	 * Excluding points of RHOMB border - separate test present for it
	 */
	@Test
	public void test_contains_Point_TopRhombTangentPoints() {
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(0, 0)));
		assertFalse("This point is outside the rhomb",
				RHOMB.contains(new Point(4, 0)));
	}

	@Test
	public void test_contains_Polyline() {
		assertTrue(RHOMB.contains(new Polyline(0, 2, 2, 2)));
	}

	@Test
	public void test_contains_Rectangle_Rectangle() {
		assertTrue("The rectangle contains itself", RECTANGLE.getBounds()
				.equals((new Rectangle(new Point(0, 0), new Point(2, 2)))));
	}

	@Test
	public void test_contains_Rectangle_Rhomb() {
		assertTrue("This rectangle is inside the rhomb",
				RHOMB.contains(new Rectangle(1.5, 1.5, 1, 1)));
		assertTrue("This rectangle is inside the rhomb",
				RHOMB.contains(new Rectangle(1, 1, 2, 2)));
		assertFalse("This rectangle is partly outside the rhomb",
				RHOMB.contains(new Rectangle(0, 0, 2, 2)));
	}

	@Test
	public void test_copy() {
		assertEquals(RHOMB, RHOMB.getCopy());
		assertEquals(RHOMB, RHOMB.clone());
		assertEquals(RHOMB.getCopy(), RHOMB.clone());
	}

	@Test
	public void test_equals() {
		// check all permutations are regarded
		assertEquals(RHOMB, RHOMB);
		assertEquals(RHOMB, new Polygon(new Point(2, 0), new Point(4, 2),
				new Point(2, 4), new Point(0, 2)));
		assertTrue(RHOMB.equals(new Point[] { new Point(2, 0), new Point(4, 2),
				new Point(2, 4), new Point(0, 2) }));
		assertEquals(RHOMB, new Polygon(new Point(4, 2), new Point(2, 4),
				new Point(0, 2), new Point(2, 0)));
		assertTrue(RHOMB.equals(new Point[] { new Point(4, 2), new Point(2, 4),
				new Point(0, 2), new Point(2, 0) }));
		assertEquals(RHOMB, new Polygon(new Point(2, 4), new Point(0, 2),
				new Point(2, 0), new Point(4, 2)));
		assertTrue(RHOMB.equals(new Point[] { new Point(2, 4), new Point(0, 2),
				new Point(2, 0), new Point(4, 2) }));
		assertEquals(RHOMB, new Polygon(new Point(0, 2), new Point(2, 0),
				new Point(4, 2), new Point(2, 4)));
		assertTrue(RHOMB.equals(new Point[] { new Point(0, 2), new Point(2, 0),
				new Point(4, 2), new Point(2, 4) }));

		// check reverse
		assertEquals(RHOMB, new Polygon(new Point(0, 2), new Point(2, 4),
				new Point(4, 2), new Point(2, 0)));
		assertEquals(RHOMB, new Polygon(new Point(2, 0), new Point(0, 2),
				new Point(2, 4), new Point(4, 2)));
		assertEquals(RHOMB, new Polygon(new Point(4, 2), new Point(2, 0),
				new Point(0, 2), new Point(2, 4)));
		assertEquals(RHOMB, new Polygon(new Point(2, 4), new Point(4, 2),
				new Point(2, 0), new Point(0, 2)));

		// check other type
		assertFalse(RHOMB.equals(new Point()));

		// check unequal polygon
		assertFalse(RHOMB.equals(new Polygon(new Point(), new Point(1, 1),
				new Point(0, 2))));
		assertFalse(RHOMB.equals(new Point[] { new Point(), new Point(1, 1),
				new Point(0, 2) }));
		assertFalse(RHOMB.equals(new Polygon(new Point(), new Point(1, 1),
				new Point(0, 2), new Point(-1, 1))));
		assertFalse(RHOMB.equals(new Point[] { new Point(), new Point(1, 1),
				new Point(0, 2), new Point(-1, 1) }));

		// check degenerated case:
		assertFalse(new Polygon(new Point(), new Point(1, 0), new Point(),
				new Point(1, 0), new Point(), new Point(2, 0))
				.equals(new Polygon(new Point(), new Point(1, 0), new Point(),
						new Point(2, 0), new Point(), new Point(2, 0))));
	}

	@Test
	public void test_getBounds() {
		assertEquals(RECTANGLE, RECTANGLE.getBounds().toPolygon());
	}

	@Test
	public void test_getCoordinates() {
		assertEquals(RHOMB, new Polygon(RHOMB.getCoordinates()));
		assertEquals(RECTANGLE, new Polygon(RECTANGLE.getCoordinates()));
		assertEquals(CONCAVE_PENTAGON,
				new Polygon(CONCAVE_PENTAGON.getCoordinates()));
		assertEquals(CONCAVE_OCTAGON,
				new Polygon(CONCAVE_OCTAGON.getCoordinates()));
		assertFalse(RECTANGLE.equals(new Polygon(RHOMB.getCoordinates())));
	}

	@Test
	public void test_getIntersections_Ellipse() {
		assertTrue(new Polygon(RHOMB.getIntersections(new Ellipse(0, 0, 4, 4)))
				.getBounds().equals(RHOMB.getBounds()));
	}

	@Test
	public void test_getIntersections_Polygon() {
		// TODO: something's wrong... debug this test
		assertEquals(2, RHOMB.getIntersections(RECTANGLE).length);
		assertEquals(4,
				RHOMB.getIntersections(RHOMB.getBounds().toPolygon()).length);
	}

	@Test
	public void test_getIntersections_Polyline() {
		assertEquals(1, RHOMB.getIntersections(new Polyline(0, 0, 0, 4)).length);
		assertEquals(2,
				RHOMB.getIntersections(new Polyline(0, 0, 0, 4, 4, 4)).length);
		assertEquals(
				3,
				RHOMB.getIntersections(new Polyline(0, 0, 0, 4, 4, 4, 2, 2)).length);
		assertEquals(4, RHOMB.getIntersections(new Polyline(0, 0, 0, 4, 4, 4,
				2, 2, 4, 0)).length);
		assertEquals(5, RHOMB.getIntersections(new Polyline(0, 0, 0, 4, 4, 4,
				2, 2, 4, 0, 0, 0)).length);
	}

	@Test
	public void test_getIntersections_Rectangle() {
		assertEquals(4, RHOMB.getIntersections(RHOMB.getBounds()).length);
	}

	@Test
	public void test_getPoints() {
		assertEquals(RHOMB, new Polygon(RHOMB.getPoints()));
		assertEquals(RECTANGLE, new Polygon(RECTANGLE.getPoints()));
		assertEquals(CONCAVE_PENTAGON,
				new Polygon(CONCAVE_PENTAGON.getPoints()));
		assertEquals(CONCAVE_OCTAGON, new Polygon(CONCAVE_OCTAGON.getPoints()));
		assertFalse(RECTANGLE.equals(new Polygon(RHOMB.getPoints())));
	}

	@Test
	public void test_getSegments() {
		Rectangle bounds = RECTANGLE.getBounds();

		for (Line s1 : bounds.getOutlineSegments()) {
			boolean foundIt = false;
			for (Line s2 : RECTANGLE.getOutlineSegments()) {
				if (s1.equals(s2)) {
					foundIt = true;
					break;
				}
			}
			assertTrue(foundIt);
		}
	}

	@Test
	public void test_getTranslated() {
		assertTrue(RHOMB.equals(RHOMB.getTranslated(new Point())));
		assertTrue(RHOMB.equals(RHOMB.getTranslated(0, 0)));
		assertTrue(RHOMB.equals(RHOMB.getTranslated(new Point(
				UNRECOGNIZABLE_FRACTION, 0))));
		assertTrue(RHOMB.equals(RHOMB.getTranslated(new Point(0,
				UNRECOGNIZABLE_FRACTION))));
		assertTrue(RHOMB.equals(RHOMB.getTranslated(new Point(
				UNRECOGNIZABLE_FRACTION, UNRECOGNIZABLE_FRACTION))));
		assertTrue(RHOMB
				.equals(RHOMB.getTranslated(UNRECOGNIZABLE_FRACTION, 0)));
		assertTrue(RHOMB
				.equals(RHOMB.getTranslated(0, UNRECOGNIZABLE_FRACTION)));
		assertTrue(RHOMB.equals(RHOMB.getTranslated(UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION)));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(new Point(1, 0))));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(new Point(0, 1))));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(new Point(1, 1))));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(1, 0)));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(0, 1)));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(1, 1)));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(new Point(
				RECOGNIZABLE_FRACTION, 0))));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(new Point(9,
				RECOGNIZABLE_FRACTION))));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(new Point(
				RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION))));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(RECOGNIZABLE_FRACTION, 0)));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(9, RECOGNIZABLE_FRACTION)));
		assertFalse(RHOMB.equals(RHOMB.getTranslated(RECOGNIZABLE_FRACTION,
				RECOGNIZABLE_FRACTION)));
	}

	@Test
	public void test_intersects_Ellipse() {
		assertTrue(RHOMB.intersects(new Ellipse(0, 0, 4, 4)));
	}

	@Test
	public void test_intersects_Line() {
		assertFalse(RHOMB.intersects(new Line(-1, 1, 1, -1)));
		assertTrue(RHOMB.intersects(new Line(-1, 2, 2, 2)));
		assertTrue(RHOMB.intersects(new Line(2, 2, 5, 2)));
		assertTrue(RHOMB.intersects(new Line(0, 2, 2, 0)));
		assertTrue(RHOMB.intersects(new Line(0, 2, 2, 4)));
		assertTrue(RHOMB.intersects(new Line(0, 2, 2, 2)));
		assertTrue(RHOMB.intersects(new Line(1, 2, 3, 2)));
		assertTrue(new Polygon(new Point(), new Point(0, 5), new Point(5, 5),
				new Point(5, 0), new Point(2.5, 2.5)).intersects(new Line(1,
				2.5, 4, 2.5)));
		assertTrue(RHOMB.intersects(new Line(-1, 2, 5, 2)));
	}

	@Test
	public void test_intersects_Polygon_Rhomb() {
		assertTrue(
				"The rhomb intersects itself, because it touches/contains itself",
				RHOMB.intersects(RHOMB));

		assertTrue(
				"The rhomb intersects its shrinked self, because its shrinked self is fully contained by the rhomb",
				RHOMB.intersects(RHOMB.getCopy().scale(0.5, new Point(2, 2))));

		assertTrue(
				"The rhomb intersects its expanded self, because its expanded self fully contains the rhomb",
				RHOMB.intersects(RHOMB.getCopy().scale(2, new Point(2, 2))));

		assertTrue(RHOMB.contains(new Point(4, 2)));
		assertTrue(RHOMB.getTranslated(new Point(4, 0)).contains(
				new Point(4, 2)));

		assertTrue("The rhomb touches the given one",
				RHOMB.intersects(RHOMB.getTranslated(4, 0)));
		assertTrue("The rhomb intersects the given one",
				RHOMB.intersects(RHOMB.getTranslated(2, 0)));
	}

	@Test
	public void test_intersects_Polyline() {
		assertFalse(RHOMB.intersects(new Polyline(0, 0, -1, -1)));
		assertTrue(RHOMB.intersects(new Polyline(0, 0, 0, 4)));
		assertTrue(RHOMB.intersects(new Polyline(0, 0, 0, 4, 4, 4)));
		assertTrue(RHOMB.intersects(new Polyline(0, 0, 0, 4, 4, 4, 2, 2)));
		assertTrue(RHOMB.intersects(new Polyline(0, 0, 0, 4, 4, 4, 2, 2, 4, 0)));
		assertTrue(RHOMB.intersects(new Polyline(0, 0, 0, 4, 4, 4, 2, 2, 4, 0,
				0, 0)));
	}

	@Test
	public void test_intersects_Rectangle_Rhomb() {
		assertTrue(
				"This rectangle is inside the rhomb and does therefore intersect it",
				RHOMB.intersects(new Rectangle(1.5, 1.5, 1, 1)));
		assertTrue(
				"This rectangle is inside the rhomb and does therefore intersect it",
				RHOMB.intersects(new Rectangle(1, 1, 2, 2)));
		assertTrue(
				"This rectangle is partly outside the rhomb and intersects it (intersection points are two polygon points)",
				RHOMB.intersects(new Rectangle(0, 0, 2, 2)));

		assertTrue(RHOMB.contains(new Point(0, 2)));
		assertTrue(new Rectangle(-2, 0, 2, 2).contains(new Point(0, 2)));
		assertTrue(
				"This rectangle is outside the rhomb and touches it in Point (0,2), which is contained in both",
				RHOMB.intersects(new Rectangle(-2, 0, 2, 2)));
	}

	@Test
	public void test_rotateCCW() {
		assertEquals(RHOMB,
				RHOMB.getCopy().rotateCCW(Angle.fromDeg(90), new Point(2, 2)));

		assertEquals(
				RHOMB.getCopy().rotateCCW(Angle.fromDeg(45), new Point(2, 2)),
				RHOMB.getCopy().rotateCCW(Angle.fromDeg(90 + 45),
						new Point(2, 2)));
	}

	@Test
	public void test_rotateCW() {
		assertEquals(RHOMB,
				RHOMB.getCopy().rotateCW(Angle.fromDeg(90), new Point(2, 2)));

		assertEquals(
				RHOMB.getCopy().rotateCW(Angle.fromDeg(45), new Point(2, 2)),
				RHOMB.getCopy().rotateCW(Angle.fromDeg(90 + 45),
						new Point(2, 2)));
	}

	@Test
	public void test_toString() {
		assertEquals("Polygon: <no points>",
				new Polygon(new Point[] {}).toString());
		assertEquals(
				"Polygon: (0.0, 2.0) -> (2.0, 0.0) -> (4.0, 2.0) -> (2.0, 4.0) -> (0.0, 2.0)",
				RHOMB.toString());
	}

	@Test
	public void test_toSWTPointArray() {
		double[] coords = RHOMB.getCoordinates();
		int[] ints = RHOMB.toSWTPointArray();
		assertEquals(coords.length, ints.length);
		for (int i = 0; i < coords.length; i++) {
			assertEquals((int) coords[i], ints[i]);
		}
	}

}
