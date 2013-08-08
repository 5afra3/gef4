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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;

/**
 * The AbstractParent is the provided abstract {@link IParent} implementation.
 * If you want to create your own {@link INode} which can be embedded into the
 * SWT widget hierarchy, subclassing AbstractParent is the way to go!
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractParent extends AbstractNode implements IParent {

	/**
	 * {@link List} of children.
	 */
	private List<INode> children = new LinkedList<INode>();

	/**
	 * Real height, assigned during layout.
	 */
	private double height = 0;

	/**
	 * Real width, assigned during layout.
	 */
	private double width = 0;

	/**
	 * The {@link Scene} which an {@link INode} belongs to can be determined
	 * using {@link INode#getScene()}. But the scene is not necessarily cached
	 * as a field in an {@link INode} implementation. Instead, a node may ask
	 * its parent for the scene. For this to work, the {@link Scene#getRoot()
	 * root parent} will get the scene assigned during its
	 * {@link Scene#Scene(org.eclipse.swt.widgets.Composite, IParent)
	 * construction}.
	 */
	private Scene scene = null;

	@Override
	public void addChildNodes(INode... nodes) {
		for (INode node : nodes) {
			if (children.contains(node)) {
				throw new IllegalStateException(
						"The given INode ("
								+ node
								+ ") is already registered as a child of this IParent ("
								+ this + ").");
			}
			children.add(node);
			node.setParentNode(this);
		}
	}

	@Override
	public double computeMaxHeight(double width) {
		return Double.MAX_VALUE;
	}

	@Override
	public double computeMaxWidth(double height) {
		return Double.MAX_VALUE;
	}

	@Override
	public double computeMinHeight(double width) {
		// TODO: sum of top and bottom padding
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		// TODO: sum of left and right padding
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		// TODO: top and bottom padding
		double yMin = 0;
		double yMax = 0;
		for (INode node : getChildNodes()) {
			if (node.isManaged()) {
				double y = node.getLayoutBounds().getY() + node.getLayoutY();
				yMin = Math.min(yMin, y);
				yMax = Math.max(yMax, y + node.computePrefHeight(-1));
			}
		}
		return yMax - yMin;
	}

	@Override
	public double computePrefWidth(double height) {
		// TODO: left and right padding
		double xMin = 0;
		double xMax = 0;
		for (INode node : getChildNodes()) {
			if (node.isManaged()) {
				double x = node.getLayoutBounds().getX() + node.getLayoutX();
				xMin = Math.min(xMin, x);
				xMax = Math.max(xMax, x + node.computePrefWidth(-1));
			}
		}
		return xMax - xMin;
	}

	@Override
	public boolean contains(double localX, double localY) {
		Point local = new Point(localX, localY);
		Point abs = localToAbsolute(local);
		return getAbsoluteBounds().contains(abs);
	}

	private Rectangle getAbsoluteBounds() {
		return getLayoutBounds().getTransformed(getLocalToAbsoluteTransform())
				.getBounds();
	}

	@Override
	public Rectangle getBoundsInLocal() {
		return getLayoutBounds();
	}

	@Override
	public List<INode> getChildNodes() {
		return children;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public Rectangle getLayoutBounds() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}

	@Override
	public INode getNodeAt(Point localPosition) {
		// System.out.println("getNodeAt(" + localPosition + ")");

		// System.out.println("area: " + getWidth() + " x " + getHeight());

		if (!contains(localPosition)) {
			return null;
		}

		Point parentLocal = localPosition; // just for the name
		Point childLocal = new Point(); // to store local positions

		ListIterator<INode> it = children.listIterator(children.size());
		while (it.hasPrevious()) {
			INode node = it.previous();
			node.parentToLocal(parentLocal, childLocal);

			// System.out.println("  " + node + " local: " + childLocal);

			if (node.contains(childLocal.x, childLocal.y)) {
				if (node instanceof IParent) {
					INode n = ((IParent) node).getNodeAt(childLocal);
					return n == null ? node : n;
				}
				return node;
			}
		}

		return null; // no figure at that position
	}

	@Override
	public Scene getScene() {
		if (scene != null) {
			return scene;
		} else {
			return getParentNode().getScene();
		}
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void layout() {
		layoutChildren();
		for (INode node : getChildNodes()) {
			if (node instanceof IParent) {
				((IParent) node).layout();
			}
		}
	}

	@Override
	public void layoutChildren() {
		for (INode node : getChildNodes()) {
			if (node.isResizable() && node.isManaged()) {
				node.autosize();
			}
		}
	}

	private void paintMyFigures(GraphicsContext g) {
		// our rendering order is the reverse of SWT's
		for (INode node : getChildNodes()) {
			if (node instanceof IFigure) {
				IFigure figure = (IFigure) node;

				// save & guard the gc
				g.save();
				g.setUpGuard(); // TODO: evaluate if we need this, really

				/*
				 * Compute transformation matrix: Take the
				 * local-to-absolute-transform and subtract the absolute scene
				 * location from it (translation).
				 */
				AffineTransform tx = figure.getLocalToAbsoluteTransform();
				org.eclipse.swt.graphics.Point location = getScene()
						.getLocation();
				location = getScene().toDisplay(location);
				tx.preConcatenate(new AffineTransform().translate(-location.x,
						-location.y));
				figure.getPaintStateByReference().setTransformByReference(tx);
				// g.setTransform(tx);

				// actually paint it
				figure.paint(g);

				// take down guard & restore gc
				try {
					g.takeDownGuard();
				} catch (IllegalStateException x) {
					throw new IllegalStateException(
							"Did you forget to call restore() in your drawing code?",
							x);
				}
				g.restore();
			}
		}
	}

	private void paintOtherFigures(GraphicsContext g) {
		for (INode node : getChildNodes()) {
			if (node instanceof IParent) {
				((IParent) node).renderFigures(g);
			}
		}
	}

	@Override
	public void renderFigures(GraphicsContext g) {
		paintMyFigures(g);
		paintOtherFigures(g);
	}

	@Override
	public void resize(double width, double height) {
		setWidth(width);
		setHeight(height);
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return "AbstractParent@" + System.identityHashCode(this);
	}

}
