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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.editparts.SimpleRootEditPart.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.policies.ContentPartSynchronizationPolicy;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractRootPart<V> extends AbstractVisualPart<V>
		implements IRootPart<V> {

	private IVisualPartViewer<V> viewer;
	
	public AbstractRootPart() {
		installPolicy(ContentPartSynchronizationPolicy.class, new ContentPartSynchronizationPolicy<V>());
	}

	public IRootPart<V> getRoot() {
		return this;
	}

	public IVisualPartViewer<V> getViewer() {
		return viewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<V>> getContentPartChildren() {
		return PartUtilities.filterParts(getChildren(), IContentPart.class);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<IHandlePart<V>> getHandlePartChildren() {
		return PartUtilities.filterParts(getChildren(), IHandlePart.class);
	}

	/**
	 * @see IRootPart#setViewer(EditPartViewer)
	 */
	public void setViewer(IVisualPartViewer<V> newViewer) {
		if (viewer == newViewer)
			return;
		viewer = newViewer;
	}

	@Override
	public void attachVisualToAnchorageVisual(V anchorageVisual, IAnchor<V> anchor) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	public void detachVisualFromAnchorageVisual(V anchorageVisual, IAnchor<V> anchor) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	protected IAnchor<V> getAnchor(IVisualPart<V> anchored) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

}