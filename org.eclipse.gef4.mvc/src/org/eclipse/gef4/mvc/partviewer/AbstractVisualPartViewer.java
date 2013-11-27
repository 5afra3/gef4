/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.partviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.ContentPartMultiSelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IContentPartSelectionModel;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The base implementation for EditPartViewer.
 * 
 * @author hudsonr
 */
public abstract class AbstractVisualPartViewer<V> implements
		IVisualPartViewer<V> {

	private Map<Object, IContentPart<V>> contentsToContentPartMap = new HashMap<Object, IContentPart<V>>();
	private Map<V, IVisualPart<V>> visualsToVisualPartMap = new HashMap<V, IVisualPart<V>>();

	private IEditDomain<V> editDomain;
	private IRootVisualPart<V> rootPart;
	private IContentPartFactory<V> contentPartFactory;
	private IContentPartSelectionModel<V> contentPartSelection;
	
	private IHandlePartFactory<V> handlePartFactory;

	/**
	 * @see IVisualPartViewer#setContentPartFactory(org.eclipse.gef4.mvc.viewer.IContentPartFactory)
	 */
	public void setContentPartFactory(IContentPartFactory<V> factory) {
		this.contentPartFactory = factory;
	}

	/**
	 * @see IVisualPartViewer#getContentPartFactory()
	 */
	public IContentPartFactory<V> getContentPartFactory() {
		return contentPartFactory;
	}

	/**
	 * Constructs the viewer and calls {@link #init()}.
	 */
	public AbstractVisualPartViewer() {
		setContentPartSelection(new ContentPartMultiSelectionModel<V>());
	}

	/**
	 * @see IVisualPartViewer#getContents()
	 */
	public Object getContents() {
		IRootVisualPart<V> rootPart = getRootPart();
		if (rootPart == null) {
			return null;
		}
		IContentPart<V> contentRoot = rootPart.getRootContentPart();
		if (contentRoot == null) {
			return null;
		}
		return contentRoot.getModel();
	}

	/**
	 * @see IVisualPartViewer#getEditDomain()
	 */
	public IEditDomain<V> getEditDomain() {
		return editDomain;
	}

	/**
	 * @see IVisualPartViewer#getContentPartMap()
	 */
	public Map<Object, IContentPart<V>> getContentPartMap() {
		return contentsToContentPartMap;
	}

	/**
	 * @see IVisualPartViewer#getRootPart()
	 */
	public IRootVisualPart<V> getRootPart() {
		return rootPart;
	}

	/**
	 * @see IVisualPartViewer#getVisualPartMap()
	 */
	public Map<V, IVisualPart<V>> getVisualPartMap() {
		return visualsToVisualPartMap;
	}

	/**
	 * @see IVisualPartViewer#setContents(Object)
	 */
	public void setContents(Object contents) {
		// TODO: make this null-safe and ensure it gets set when root is set
		// afterwards
		IContentPart<V> rootContentPart = getContentPartFactory()
				.createRootContentPart(getRootPart(), contents);
		rootContentPart.setModel(contents);
		getRootPart().setRootContentPart(rootContentPart);
	}

	/**
	 * @see IVisualPartViewer#setDomain(EditDomain)
	 */
	public void setEditDomain(IEditDomain<V> editdomain) {
		if (editDomain == editdomain)
			return;
		if (editDomain != null) {
			editDomain.setViewer(null);
		}
		this.editDomain = editdomain;
		if (editDomain != null) {
			editDomain.setViewer(this);
		}
	}

	@Override
	public IContentPartSelectionModel<V> getContentPartSelection() {
		return contentPartSelection;
	}

	@Override
	public void setContentPartSelection(
			IContentPartSelectionModel<V> contentPartSelection) {
		if (this.contentPartSelection == contentPartSelection) {
			return;
		}
		// TODO: if tools may register on selection, we need to deactivate/activate them here
		this.contentPartSelection = contentPartSelection;
	}

	/**
	 * @see IVisualPartViewer#setRootPart(IRootVisualPart)
	 */
	public void setRootPart(IRootVisualPart<V> rootEditPart) {
		if (this.rootPart != null) {
			this.rootPart.deactivate();
			this.rootPart.setViewer(null);
		}
		this.rootPart = rootEditPart;
		if (this.rootPart != null) {
			this.rootPart.setViewer(this);
			this.rootPart.activate();
		}
	}

	@Override
	public IHandlePartFactory<V> getHandlePartFactory() {
		return handlePartFactory;
	}

	@Override
	public void setHandlePartFactory(IHandlePartFactory<V> factory) {
		this.handlePartFactory = factory;
	}

}
