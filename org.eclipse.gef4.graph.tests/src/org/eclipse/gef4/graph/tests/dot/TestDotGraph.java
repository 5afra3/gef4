/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.graph.Graph;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotGraph} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestDotGraph {

	@Test
	public void sampleUsage() {
		Graph graph = new Graph("digraph{1->2}"); //$NON-NLS-1$
		assertNodesEdgesCount(2, 1, graph);
		graph.withDot("node[label=zested]; 2->3; 2->4"); //$NON-NLS-1$
		assertNodesEdgesCount(4, 3, graph);
		graph.withDot("edge[style=dashed]; 3->5; 4->6"); //$NON-NLS-1$
		assertNodesEdgesCount(6, 5, graph);
	}

	@Test
	public void graphAttributesToDataMapping() {
		String dotInput = "digraph{ graph[key1=graph_value1 key2=graph_value2]; 1->2 }";
		Graph graph = new Graph(dotInput);
		assertEquals("graph_value1", graph.getAttribute("key1"));
		assertEquals("graph_value2", graph.getAttribute("key2"));
	}

	private void assertNodesEdgesCount(int n, int e, Graph graph) {
		Assert.assertEquals(n, graph.getNodes().size());
		Assert.assertEquals(e, graph.getEdges().size());
	}
}
