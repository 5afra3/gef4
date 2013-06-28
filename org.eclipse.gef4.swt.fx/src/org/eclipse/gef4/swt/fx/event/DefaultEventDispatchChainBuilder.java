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
package org.eclipse.gef4.swt.fx.event;

import org.eclipse.gef4.swt.fx.AbstractFigure;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.INode;

/**
 * Provides the {@link #buildEventDispatchChain(INode, IEventDispatchChain)
 * default implementation} of the
 * {@link IEventTarget#buildEventDispatchChain(IEventDispatchChain)} method used
 * in the {@link Group} and {@link AbstractFigure} classes.
 * 
 * @author mwienand
 * 
 */
public class DefaultEventDispatchChainBuilder {

	/**
	 * Recursively builds an {@link IEventDispatchChain} for the given
	 * {@link INode target}. The target's {@link IEventDispatcher} is prepended
	 * to the current {@link IEventDispatchChain chain} together with a
	 * {@link MouseTrackDispatcher} for the target.
	 * 
	 * @param target
	 *            the event target
	 * @param tail
	 *            the current {@link IEventDispatchChain}
	 * @return an {@link IEventDispatchChain} for the given target
	 * 
	 * @see IEventTarget#buildEventDispatchChain(IEventDispatchChain)
	 */
	public static IEventDispatchChain buildEventDispatchChain(
			final INode target, final IEventDispatchChain tail) {
		tail.prepend(target.getEventDispatcher());
		tail.prepend(new MouseTrackDispatcher(target));
		tail.prepend(new FocusTraversalDispatcher(target));
		INode next = target.getParentNode();
		if (next != null) {
			return next.buildEventDispatchChain(tail);
		}
		return tail;
	}

}
