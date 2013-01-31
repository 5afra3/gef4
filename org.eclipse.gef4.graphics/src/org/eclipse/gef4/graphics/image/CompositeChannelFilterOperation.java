/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A CompositeChannelFilterOperation combines a set of
 * {@link AbstractChannelFilterOperation}s to apply them at once to an
 * {@link Image}. The combination of multiple
 * {@link AbstractChannelFilterOperation}s does not only ease the appliance by
 * grouping the operations, it does also increase the execution speed for the
 * grouped operations. In fact, if when {@link IImageOperation#apply(Image)
 * applying} the operations sequentially, the {@link Image}'s pixels have to be
 * iterated for each of the operations. But if they are
 * {@link IImageOperation#apply(Image) applied} as a group, the {@link Image}'s
 * pixels do only have to be iterated once.
 * 
 * @author mwienand
 * 
 */
public class CompositeChannelFilterOperation extends
AbstractChannelFilterOperation {

	private List<AbstractChannelFilterOperation> channelOps = null;

	/**
	 * <p>
	 * Constructs a new {@link CompositeChannelFilterOperation} from the given
	 * {@link AbstractChannelFilterOperation}s.
	 * </p>
	 * 
	 * <p>
	 * The provided {@link AbstractChannelFilterOperation}s are applied in the
	 * given order when this {@link CompositeChannelFilterOperation} is
	 * {@link #apply(Image) applied}.
	 * </p>
	 * 
	 * @param pixelOps
	 *            the {@link AbstractChannelFilterOperation}s to apply when this
	 *            {@link CompositeChannelFilterOperation} is applied
	 */
	public CompositeChannelFilterOperation(
			AbstractChannelFilterOperation... pixelOps) {
		setChannelFilterOperations(pixelOps);
	}

	/**
	 * <p>
	 * Returns the {@link AbstractChannelFilterOperation}s that are combined in
	 * this {@link CompositeChannelFilterOperation}.
	 * </p>
	 * 
	 * @return the {@link AbstractChannelFilterOperation}s that are combined in
	 *         this {@link CompositeChannelFilterOperation}
	 */
	public AbstractChannelFilterOperation[] getChannelFilterOperations() {
		return channelOps.toArray(new AbstractChannelFilterOperation[] {});
	}

	@Override
	protected int processChannel(int v, int x, int y, int i, Image input) {
		int res = v;
		for (AbstractChannelFilterOperation chOp : channelOps) {
			res = chOp.processChannel(res, x, y, i, input);
		}
		return res;
	}

	/**
	 * Sets the {@link AbstractChannelFilterOperation}s to apply when this
	 * {@link CompositeChannelFilterOperation} is applied.
	 * 
	 * @param channelOps
	 *            the new {@link AbstractChannelFilterOperation}s to apply when
	 *            this {@link CompositeChannelFilterOperation} is applied
	 */
	public void setChannelFilterOperations(
			AbstractChannelFilterOperation... channelOps) {
		this.channelOps = channelOps == null ? new ArrayList<AbstractChannelFilterOperation>()
				: new ArrayList<AbstractChannelFilterOperation>(
						Arrays.asList(channelOps));
	}

}
