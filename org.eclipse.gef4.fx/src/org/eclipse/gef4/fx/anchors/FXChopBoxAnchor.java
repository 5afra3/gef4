/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Ny??en (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class FXChopBoxAnchor extends AbstractFXAnchor {

	public FXChopBoxAnchor(Node anchorage) {
		super(anchorage);
	}

	/**
	 * @param anchored
	 *            The to be anchored {@link Node} for which the anchor position
	 *            is to be determined.
	 * @param referencePoint
	 *            A reference {@link Point} used for calculation of the anchor
	 *            position, provided within the local coordinate system of the
	 *            to be anchored {@link Node}.
	 * @return Point The anchor position within the local coordinate system of
	 *         the to be anchored {@link Node}.
	 */
	@Override
	public Point computePosition(Node anchored, Point referencePoint) {
		// compute intersection point between outline of anchorage reference
		// shape and line through anchorage and anchor reference points.

		AffineTransform anchorageToSceneTransform = JavaFX2Geometry
				.toAffineTransform(getAnchorageNode().getLocalToSceneTransform());

		AffineTransform anchoredToSceneTransform = JavaFX2Geometry
				.toAffineTransform(anchored.getLocalToSceneTransform());

		Point anchorageReferencePointInScene = anchorageToSceneTransform
				.getTransformed(getAnchorageReferencePoint());
		Point anchorReferencePointInScene = anchoredToSceneTransform
				.getTransformed(referencePoint);
		Line referenceLineInScene = new Line(anchorageReferencePointInScene,
				anchorReferencePointInScene);

		IShape anchorageReferenceShapeInScene = getAnchorageReferenceShape()
				.getTransformed(anchorageToSceneTransform);

		Point[] intersectionPoints = anchorageReferenceShapeInScene
				.getOutline().getIntersections(referenceLineInScene);
		if (intersectionPoints.length > 0) {
			return JavaFX2Geometry.toPoint(anchored
					.sceneToLocal(Geometry2JavaFX
							.toFXPoint(intersectionPoints[0])));
		}
		
		// do not fail hard... use center
		return JavaFX2Geometry.toPoint(anchored
				.sceneToLocal(Geometry2JavaFX
						.toFXPoint(anchorageReferencePointInScene)));
		
		// TODO: investigate where the wrong reference point is set
//		throw new IllegalArgumentException("Invalid reference point "
//				+ referencePoint);
	}

	/**
	 * Returns the anchorage reference {@link IShape} which is used to compute
	 * the intersection point which is used as the anchor position. By default,
	 * a {@link Rectangle} matching the layout-bounds of the anchorage
	 * {@link Node} is returned. Clients may override this method to use other
	 * geometric shapes instead.
	 * 
	 * @return The anchorage reference {@link IShape} within the local
	 *         coordinate system of the anchorage {@link Node}
	 */
	protected IShape getAnchorageReferenceShape() {
		return JavaFX2Geometry.toRectangle(getAnchorageNode().getLayoutBounds());
	}

	/**
	 * @return The anchorage reference point within the local coordinate system
	 *         of the anchorage {@link Node}.
	 */
	protected Point getAnchorageReferencePoint() {
		return getAnchorageReferenceShape().getBounds().getCenter();
	}

}
