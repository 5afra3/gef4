/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swt.canvas;

import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatcher;
import org.eclipse.gef4.swt.canvas.ev.IEventHandler;
import org.eclipse.gef4.swt.canvas.ev.IEventTarget;

public interface INode extends IEventTarget {

	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter);

	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler);

	IEventDispatcher getEventDispatcher();

	INode getParentNode();

	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter);

	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler);

}
