/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - multi selection handles in root part
 *     
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditDomain.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The AbstractSelectionFeedbackPolicy is responsible for creating and removing
 * selection feedback.
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public abstract class AbstractSelectionFeedbackPolicy<V> extends
		AbstractFeedbackPolicy<V> implements PropertyChangeListener {

	private List<IHandlePart<V>> multiHandleParts;

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getSelectionModel()
				.addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getSelectionModel()
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(ISelectionModel.SELECTION_PROPERTY)) {
			List<IContentPart<V>> oldSelection = (List<IContentPart<V>>) event
					.getOldValue();
			List<IContentPart<V>> newSelection = (List<IContentPart<V>>) event
					.getNewValue();

			if (getHost() instanceof IRootPart) {
				removeMultiHandles((IRootPart<V>) getHost(), oldSelection);
				if (newSelection.size() > 1) {
					addMultiHandles((IRootPart<V>) getHost(), newSelection);
				}
				return;
			}

			boolean inOld = oldSelection.contains(getHost());
			boolean inNew = newSelection.contains(getHost());

			if (inOld) {
				removeHandles();
				hideFeedback();
			}

			if (inNew) {
				if (newSelection.get(0) == getHost()) {
					showPrimaryFeedback();
					if (newSelection.size() <= 1) {
						addHandles();
					}
				} else {
					showSecondaryFeedback();
				}
			}
		}
	}

	private void addMultiHandles(IRootPart<V> rootPart,
			List<IContentPart<V>> newSelection) {
		// attach to anchorages
		if (getHandlePartFactory() != null) {
			multiHandleParts = getHandlePartFactory()
					.createSelectionHandleParts(newSelection);
		}
		if (multiHandleParts != null && !multiHandleParts.isEmpty()) {
			rootPart.addHandleParts(multiHandleParts);
			for (IContentPart<V> selected : newSelection) {
				for (IVisualPart<V> handlePart : multiHandleParts) {
					selected.addAnchored(handlePart);
				}
			}
		}
	}

	private void removeMultiHandles(IRootPart<V> rootPart,
			List<IContentPart<V>> oldSelection) {
		// attach to anchorages
		if (multiHandleParts != null && !multiHandleParts.isEmpty()) {
			rootPart.removeHandleParts(multiHandleParts);
			for (IContentPart<V> selected : oldSelection) {
				for (IVisualPart<V> handlePart : multiHandleParts) {
					selected.removeAnchored(handlePart);
				}
			}
			multiHandleParts.clear();
		}
	}

	private IHandlePartFactory<V> getHandlePartFactory() {
		return getHost().getRoot().getViewer().getHandlePartFactory();
	}

	protected abstract void hideFeedback();

	@Override
	public List<IHandlePart<V>> createHandles() {
		IVisualPart<V> host = getHost();
		IHandlePartFactory<V> factory = getHandlePartFactory(host);
		if (host instanceof IContentPart) {
			List<IContentPart<V>> parts = new ArrayList<IContentPart<V>>(1);
			parts.add((IContentPart<V>) host);
			List<IHandlePart<V>> handleParts = factory
					.createSelectionHandleParts(parts);
			return handleParts;
		}
		return Collections.<IHandlePart<V>> emptyList();
	}

	private IHandlePartFactory<V> getHandlePartFactory(IVisualPart<V> host) {
		return host.getRoot().getViewer().getHandlePartFactory();
	}

	protected abstract void showSecondaryFeedback();

	protected abstract void showPrimaryFeedback();

}
