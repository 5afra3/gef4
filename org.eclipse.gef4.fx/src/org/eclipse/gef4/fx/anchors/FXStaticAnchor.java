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
package org.eclipse.gef4.fx.anchors;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;

public class FXStaticAnchor extends AbstractFXNodeAnchor {

	public FXStaticAnchor(Node anchorage) {
		super(anchorage);
	}

	@Override
	public Point computePosition(Node anchored, Point referencePoint) {
		return referencePoint;
	}
	
}