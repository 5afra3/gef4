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
package org.eclipse.gef4.swtfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractSwtFXControl<T extends Control> extends Region {

	private static final int[] FORWARD_EVENT_TYPES = new int[] { SWT.FocusIn,
			SWT.FocusOut, SWT.HardKeyDown, SWT.HardKeyUp, SWT.KeyDown,
			SWT.KeyUp, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel, SWT.Move, SWT.Traverse,
			SWT.Verify };

	private T control;

	private Listener swtForwardListener;

	private SwtFXCanvas canvas = null;

	public AbstractSwtFXControl() {
		parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> observable,
					Parent oldValue, Parent newValue) {
				System.out.println(AbstractSwtFXControl.this
						+ " :: parent change from <" + oldValue + "> to <"
						+ newValue + ">.");
				SwtFXCanvas newCanvas = getSwtFXCanvas(newValue);
				canvasChanged(canvas, newCanvas);
				canvas = newCanvas;
			}
		});
	}

	private void _hookControl(final SwtFXCanvas newCanvas) {
		setControl(createControl(newCanvas));
		if (newCanvas != null) {
			swtForwardListener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					Point location = control.getLocation();
					event.x += location.x;
					event.y += location.y;
					newCanvas.notifyListeners(event.type, event);
				}
			};
			for (int eventType : FORWARD_EVENT_TYPES) {
				control.addListener(eventType, swtForwardListener);
			}
		}
		hookControl();
	}

	protected void _unhookControl(SwtFXCanvas oldCanvas) {
		if (swtForwardListener != null) {
			for (int eventType : FORWARD_EVENT_TYPES) {
				control.removeListener(eventType, swtForwardListener);
			}
			swtForwardListener = null;
		}
		unhookControl();
		getControl().dispose();
	}

	protected void canvasChanged(SwtFXCanvas oldCanvas, SwtFXCanvas newCanvas) {
		System.out.println(this + " :: canvas change from <" + oldCanvas
				+ "> to <" + newCanvas + ">.");
		if (oldCanvas != null) {
			if (oldCanvas != newCanvas) {
				_unhookControl(oldCanvas);
			}
		}
		if (newCanvas != null) {
			if (oldCanvas != newCanvas) {
				_hookControl(newCanvas);
			}
		}
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computePrefHeight(width);
	}

	@Override
	protected double computeMaxWidth(double height) {
		return computePrefWidth(height);
	}

	@Override
	protected double computeMinHeight(double width) {
		return computePrefHeight(width);
	}

	@Override
	protected double computeMinWidth(double height) {
		return computePrefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		if (control == null) {
			return 0;
		}
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
	}

	@Override
	protected double computePrefWidth(double height) {
		if (control == null) {
			return 0;
		}
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	}

	abstract protected T createControl(SwtFXCanvas fxCanvas);

	/**
	 * We do not manage children. Therefore, it is illegal to alter the children
	 * list in any way.
	 */
	@Override
	protected ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}

	public T getControl() {
		return control;
	}

	/**
	 * Retrieves the underlying {@link SwtFXCanvas} from a given {@link Node}.
	 * In case no {@link SwtFXCanvas} can be found, <code>null</code> is
	 * returned.
	 * 
	 * @param node
	 * @return the {@link SwtFXCanvas} of the given {@link Node} or
	 *         <code>null</code>
	 */
	protected SwtFXCanvas getSwtFXCanvas(Node node) {
		if (node == null) {
			return null;
		}
		Scene scene = node.getScene();
		if (scene != null) {
			if (!(scene instanceof SwtFXScene)) {
				throw new IllegalArgumentException();
			}
			SwtFXCanvas fxCanvas = ((SwtFXScene) scene).getFXCanvas();
			return fxCanvas;
		}
		return null;
	}

	/**
	 * Used to register special listeners on the specific {@link Control}.
	 */
	protected void hookControl() {
	}

	@Override
	public void relocate(double paramDouble1, double paramDouble2) {
		super.relocate(paramDouble1, paramDouble2);
		updateSwtBounds();
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		updateSwtBounds();
	}

	protected void setControl(T control) {
		this.control = control;
	}

	/**
	 * Used to unregister special listeners from the specific {@link Control}.
	 */
	protected void unhookControl() {
	}

	public void updateSwtBounds() {
		if (control == null) {
			return;
		}

		Bounds bounds = localToScene(getLayoutBounds());

		control.setBounds((int) Math.ceil(bounds.getMinX()),
				(int) Math.ceil(bounds.getMinY()),
				(int) Math.ceil(bounds.getWidth()),
				(int) Math.ceil(bounds.getHeight()));
	}

}