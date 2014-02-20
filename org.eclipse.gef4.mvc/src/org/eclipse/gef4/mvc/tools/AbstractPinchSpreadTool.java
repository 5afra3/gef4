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
package org.eclipse.gef4.mvc.tools;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPinchSpreadPolicy;

public class AbstractPinchSpreadTool<V> extends AbstractTool<V> {

	@SuppressWarnings("rawtypes")
	public static final Class<IPinchSpreadPolicy> TOOL_POLICY_KEY = IPinchSpreadPolicy.class;

	@SuppressWarnings("unchecked")
	protected IPinchSpreadPolicy<V> getToolPolicy(IVisualPart<V> targetPart) {
		return targetPart.getBound(TOOL_POLICY_KEY);
	}

	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 */
	protected void pinchDetected(List<IVisualPart<V>> targetParts,
			double partialFactor, double totalFactor) {
		for (IVisualPart<V> targetPart : targetParts) {
			IPinchSpreadPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.pinchDetected(partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	protected void pinch(List<IVisualPart<V>> targetParts, double partialFactor,
			double totalFactor) {
		for (IVisualPart<V> targetPart : targetParts) {
			IPinchSpreadPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.pinchDetected(partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 */
	protected void pinchFinished(List<IVisualPart<V>> targetParts,
			double partialFactor, double totalFactor) {
		for (IVisualPart<V> targetPart : targetParts) {
			IPinchSpreadPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.pinchDetected(partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Reaction to the detection of spread (open fingers) gestures.
	 */
	protected void spreadDetected(List<IVisualPart<V>> targetParts,
			double partialFactor, double totalFactor) {
		for (IVisualPart<V> targetPart : targetParts) {
			IPinchSpreadPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.pinchDetected(partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Continuous reaction to spread (open fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	protected void spread(List<IVisualPart<V>> targetParts, double partialFactor,
			double totalFactor) {
		for (IVisualPart<V> targetPart : targetParts) {
			IPinchSpreadPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.pinchDetected(partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Reaction to the finish of spread (open fingers) gestures.
	 */
	protected void spreadFinished(List<IVisualPart<V>> targetParts,
			double partialFactor, double totalFactor) {
		for (IVisualPart<V> targetPart : targetParts) {
			IPinchSpreadPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.pinchDetected(partialFactor, totalFactor);
			}
		}
	}

}
