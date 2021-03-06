/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.ui.example.model.AbstractFXGeometricElement;

public abstract class AbstractFXGeometricElementPart extends
		AbstractFXContentPart implements PropertyChangeListener {

	@Override
	public AbstractFXGeometricElement<?> getContent() {
		return (AbstractFXGeometricElement<?>) super.getContent();
	}

	@Override
	public void activate() {
		super.activate();
		getContent().addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getContent().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getContent()) {
			refreshVisual();
		}
	}

	@Override
	public void refreshVisual() {
		Node visual = getVisual();
		AbstractFXGeometricElement<?> content = getContent();
		if (visual.getEffect() != content.getEffect()) {
			visual.setEffect(content.getEffect());
		}
	}

}
