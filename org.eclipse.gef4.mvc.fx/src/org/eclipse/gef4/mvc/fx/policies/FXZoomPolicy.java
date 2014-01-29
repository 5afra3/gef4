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
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;
import javafx.scene.Parent;

import org.eclipse.gef4.mvc.fx.parts.FXRootVisualPart;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractZoomPolicy;

public class FXZoomPolicy extends AbstractZoomPolicy<Node> {

	@Override
	protected void applyZoomFactor(Double zoomFactor) {
		if (zoomFactor <= 0) {
			throw new IllegalArgumentException(
					"Expected: positive double. Given: <" + zoomFactor + ">.");
		}

		IRootVisualPart<Node> root = getHost().getRoot();
		if (root instanceof FXRootVisualPart) {
			FXRootVisualPart fxRvp = (FXRootVisualPart) root;
			Parent layers = fxRvp.getContentLayer().getParent();
			if (layers != null) {
				layers.setScaleX(zoomFactor);
				layers.setScaleY(zoomFactor);
			}
		}
	}
	
}
