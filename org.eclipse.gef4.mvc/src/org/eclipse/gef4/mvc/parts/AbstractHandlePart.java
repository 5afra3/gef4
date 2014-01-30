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
package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.anchors.IAnchor;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public abstract class AbstractHandlePart<V> extends AbstractVisualPart<V>
		implements IHandlePart<V> {

	private List<IContentPart<V>> targetContentParts;

	@Override
	public List<IContentPart<V>> getTargetContentParts() {
		if(targetContentParts == null){
			return Collections.emptyList();
		}
		return targetContentParts;
	}

	@Override
	public void setTargetContentParts(List<IContentPart<V>> targetContentParts) {
		if(this.targetContentParts == null){
			this.targetContentParts = new ArrayList<IContentPart<V>>();
		}
		this.targetContentParts.addAll(targetContentParts);
	}

	@Override
	protected void addChildVisual(IVisualPart<V> child, int index) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support children");
	}

	@Override
	protected void removeChildVisual(IVisualPart<V> child) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

	@Override
	public void attachVisualToAnchorageVisual(IAnchor<V> anchor) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

	@Override
	public void detachVisualFromAnchorageVisual(IAnchor<V> anchor) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

	@Override
	protected IAnchor<V> getAnchor(IVisualPart<V> anchored) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

}
