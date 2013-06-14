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
package org.eclipse.gef4.swt.canvas.ev;

public abstract class AbstractEventDispatcher implements IEventDispatcher {

	// private AbstractEventDispatcher nextDispatcher;
	// private AbstractEventDispatcher prevDispatcher;

	// public Event dispatchBubblingEvent(Event event) {
	// return event;
	// }
	//
	// public Event dispatchCapturingEvent(Event event) {
	// return event;
	// }

	public abstract Event dispatchBubblingEvent(Event event);

	public abstract Event dispatchCapturingEvent(Event event);

	@Override
	public Event dispatchEvent(Event event, IEventDispatchChain tail) {
		event = dispatchCapturingEvent(event);

		// TODO: is event == null even possible?
		if (event != null && !event.isConsumed()) {
			// forward the event to the rest of the chain
			event = tail.dispatchEvent(event);

			// TODO: is event == null even possible?
			if (event != null && !event.isConsumed()) {
				event = dispatchBubblingEvent(event);
			}
		}

		// TODO: is event == null even possible?
		return event == null || event.isConsumed() ? null : event;
	}

	// public AbstractEventDispatcher getNextDispatcher() {
	// return nextDispatcher;
	// }
	//
	// public AbstractEventDispatcher getPreviousDispatcher() {
	// return prevDispatcher;
	// }
	//
	// public void insertNextDispatcher(AbstractEventDispatcher nextDispatcher)
	// {
	// if (this.nextDispatcher != null) {
	// this.nextDispatcher.prevDispatcher = nextDispatcher;
	// }
	// nextDispatcher.nextDispatcher = this.nextDispatcher;
	// nextDispatcher.prevDispatcher = this;
	// this.nextDispatcher = nextDispatcher;
	// }

}
