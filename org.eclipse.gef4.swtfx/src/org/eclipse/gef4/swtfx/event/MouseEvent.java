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
package org.eclipse.gef4.swtfx.event;

import org.eclipse.gef4.swtfx.INode;

/**
 * Represents a mouse event. There are several different mouse event types:
 * 
 * <ul>
 * <li>MOUSE_PRESSED: fired when a mouse button is pressed.</li>
 * <li>MOUSE_RELEASED: fired when a mouse button is released.</li>
 * <li>MOUSE_SCROLLED: fired when the mouse wheel is scrolled.</li>
 * <li>MOUSE_MOVED: fired when the mouse is moved.</li>
 * <li>TODO: MOUSE_DRAGGED: fired when the mouse is moved while being pressed.</li>
 * <li>MOUSE_ENTERED_TARGET: fired when the mouse enters an {@link INode}</li>
 * <li>MOUSE_EXITED_TARGET: fired when the mouse exits an {@link INode}</li>
 * <li>MOUSE_ENTERED: only send to the entered {@link INode}</li>
 * <li>MOUSE_EXITED: only send to the exited {@link INode}</li>
 * </ul>
 * 
 * Every mouse event is associated with the following attributes:
 * 
 * <ul>
 * <li>{@link #getButton()}: the id of the pressed/released button<br />
 * You can use the SWT#BUTTONx constants to check which one.</li>
 * <li>{@link #getClickCount()}: the scroll direction/speed if button = 0<br />
 * Normally, a scroll event will set this field to -3 or 3.</li>
 * <li>{@link #getTargetX()}: the x coordinate of the mouse cursor</li>
 * <li>{@link #getTargetY()}: the y coordinate of the mouse cursor</li>
 * </ul>
 * 
 * @author mwienand
 * 
 */
public class MouseEvent extends InputEvent {

	public static final EventType<MouseEvent> ANY = new EventType<MouseEvent>(
			InputEvent.ANY, "MouseEvent");

	public static final EventType<MouseEvent> MOUSE_PRESSED = new EventType<MouseEvent>(
			ANY, "MousePressEvent");

	public static final EventType<MouseEvent> MOUSE_RELEASED = new EventType<MouseEvent>(
			ANY, "MouseReleaseEvent");

	public static final EventType<MouseEvent> MOUSE_SCROLLED = new EventType<MouseEvent>(
			ANY, "MouseScrollEvent");

	public static final EventType<MouseEvent> MOUSE_MOVED = new EventType<MouseEvent>(
			ANY, "MouseMoveEvent");

	public static final EventType<MouseEvent> MOUSE_ENTERED_TARGET = new EventType<MouseEvent>(
			ANY, "MouseEnterTargetEvent");

	public static final EventType<MouseEvent> MOUSE_EXITED_TARGET = new EventType<MouseEvent>(
			ANY, "MouseExitTargetEvent");

	public static final EventType<MouseEvent> MOUSE_ENTERED = new EventType<MouseEvent>(
			MOUSE_ENTERED_TARGET, "MouseEnterEvent");

	public static final EventType<MouseEvent> MOUSE_EXITED = new EventType<MouseEvent>(
			MOUSE_EXITED_TARGET, "MouseExitEvent");

	private static final long serialVersionUID = 1L;

	private int button;
	private int count;
	private double targetX;
	private double targetY;
	private double sceneX;
	private double sceneY;
	private double displayX;
	private double displayY;

	public MouseEvent(Object source, IEventTarget target,
			EventType<? extends MouseEvent> type, int button, int count,
			double targetX, double targetY, double sceneX, double sceneY,
			double displayX, double displayY) {
		super(source, target, type);

		this.button = button;
		this.count = count;
		this.targetX = targetX;
		this.targetY = targetY;
		this.sceneX = sceneX;
		this.sceneY = sceneY;
		this.displayX = displayX;
		this.displayY = displayY;
	}

	public int getButton() {
		return button;
	}

	public int getClickCount() {
		return count;
	}

	public double getDisplayX() {
		return displayX;
	}

	public double getDisplayY() {
		return displayY;
	}

	public double getSceneX() {
		return sceneX;
	}

	public double getSceneY() {
		return sceneY;
	}

	public double getTargetX() {
		return targetX;
	}

	public double getTargetY() {
		return targetY;
	}

	@Override
	public String toString() {
		return getEventType().getName() + "(button=" + button + ", count="
				+ count + ", x=" + "(display:" + displayX + ", scene:" + sceneX
				+ ", target:" + targetX + "), y=(display:" + displayY
				+ ", scene:" + sceneY + ", target:" + targetY + "))";
	}

}
