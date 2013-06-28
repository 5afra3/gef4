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

import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.IFigure;
import org.eclipse.gef4.swt.fx.INode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

public class FocusTraversalDispatcher extends AbstractEventDispatcher {

	private INode target;

	public FocusTraversalDispatcher(INode target) {
		this.target = target;
	}

	@Override
	public Event dispatchBubblingEvent(Event event) {
		return event;
	}

	@Override
	public Event dispatchCapturingEvent(Event event) {
		if (event.getEventType() == TraverseEvent.ANY) {
			TraverseEvent traverseEvent = (TraverseEvent) event;

			IEventTarget target = traverseEvent.getTarget();

			if (target instanceof IFigure) {
				target = ((IFigure) target).getContainer();
			}

			if (target instanceof Group) {
				Group g = (Group) target;
				if (g.isFocusTraversable()) {
					boolean directionNext = traverseEvent.getDetail() == SWT.TRAVERSE_ARROW_NEXT
							|| traverseEvent.getDetail() == SWT.TRAVERSE_PAGE_NEXT
							|| traverseEvent.getDetail() == SWT.TRAVERSE_TAB_NEXT
							|| traverseEvent.getDetail() == SWT.TRAVERSE_RETURN;

					IFigure nextFocusFigure = trav(directionNext, g);
					g.setFocusFigure(nextFocusFigure);

					if (nextFocusFigure == null) {
						if (directionNext) {
							Control[] children = g.getChildren();
							if (children.length > 0) {
								children[0].forceFocus();
							}
						} else {
							g.getParent().forceFocus();
						}
					}

					g.redraw();
					traverseEvent.consume();
				}
			} else {
				return event;
			}
		}

		return event;
	}

	private IFigure trav(boolean directionNext, Group container) {
		return directionNext ? container.getNextFocusFigure() : container
				.getPreviousFocusFigure();
	}

}
