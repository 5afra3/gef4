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
package org.eclipse.gef4.swt.canvas.ev.types;

import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventTarget;

/**
 * The ActionEvent class is the base class of all events emitted by the
 * application and not by an input device.
 * 
 * TODO: List uses of ActionEvents. (transition events, user events)
 * 
 * @author mwienand
 * 
 */
public class ActionEvent extends Event {

	public static final EventType<ActionEvent> ANY = new EventType<ActionEvent>(
			EventType.ROOT, "ActionEvent");

	private static final long serialVersionUID = 1L;

	public ActionEvent(Object source, IEventTarget target,
			EventType<? extends ActionEvent> type) {
		super(source, target, type);
	}

}
