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
package org.eclipse.gef4.mvc.viewer;

import org.eclipse.gef4.mvc.parts.IRootPart;

public interface IViewerBound<V> {

	/**
	 * Returns the {@link IVisualPartViewer} this {@link IViewerBound} is bound to.
	 * 
	 * @return The {@link IVisualPartViewer} this {@link IRootPart} is
	 *         attached to.
	 */
	public abstract IVisualPartViewer<V> getViewer();

	/**
	 * Sets the {@link IVisualPartViewer} this {@link IViewerBound} is to be bound to.
	 * 
	 * @param viewer
	 *            the {@link IVisualPartViewer} this {@link IViewerBound} should be
	 *            attached to.
	 */
	public abstract void setViewer(IVisualPartViewer<V> viewer);

}