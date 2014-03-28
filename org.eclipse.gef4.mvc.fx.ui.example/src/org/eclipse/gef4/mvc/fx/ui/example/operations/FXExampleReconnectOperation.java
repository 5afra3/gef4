/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.operations;

import java.util.HashMap;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXExampleReconnectOperation extends AbstractOperation {

	private FXGeometricCurvePart curvePart;
	private boolean isStartAnchor;
	private IVisualPart<Node> oldAnchoragePart;
	private IVisualPart<Node> newAnchoragePart;

	public FXExampleReconnectOperation(String label,
			FXGeometricCurvePart curvePart, boolean isStart,
			IVisualPart<Node> oldShapePart, IVisualPart<Node> newShapePart) {
		super(label);
		this.curvePart = curvePart;
		this.isStartAnchor = isStart;
		this.oldAnchoragePart = oldShapePart;
		this.newAnchoragePart = newShapePart;
	}

	private void addAnchoragePart(IVisualPart<Node> shapePart) {
		shapePart.addAnchored(curvePart, new HashMap<Object, Object>() {
			{
				put("vertex", isStartAnchor ? 0 : 1);
			}
		});
		IFXConnection visual = (IFXConnection) curvePart.getVisual();
		if (isStartAnchor) {
			visual.getStartAnchor().recomputePositions();
		} else {
			visual.getEndAnchor().recomputePositions();
		}
	}

	private void removeCurrentAnchor() {
		IFXConnection visual = (IFXConnection) curvePart.getVisual();
		IFXAnchor currentAnchor = isStartAnchor ? visual.getStartAnchor()
				: visual.getEndAnchor();
		Node anchorageNode = currentAnchor.getAnchorageNode();
		if (anchorageNode != null) {
			curvePart.getRoot().getViewer().getVisualPartMap()
					.get(anchorageNode)
					.removeAnchored(curvePart, new HashMap<Object, Object>() {
						{
							put("vertex", isStartAnchor ? 0 : 1);
						}
					});
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		removeCurrentAnchor();
		addAnchoragePart(newAnchoragePart);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		removeCurrentAnchor();
		addAnchoragePart(oldAnchoragePart);
		return Status.OK_STATUS;
	}

}
