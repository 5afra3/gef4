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
package org.eclipse.gef4.mvc.models;

import java.util.List;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface IContentModel extends IPropertyChangeSupport {

	public static final String CONTENTS_PROPERTY = "contents";
	
	public void setContents(List<Object> contents);
	
	public List<Object> getContents();
	
}
