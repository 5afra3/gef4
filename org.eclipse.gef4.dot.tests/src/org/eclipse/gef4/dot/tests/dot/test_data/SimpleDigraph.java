/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests.dot.test_data;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation demonstrating
 * directed graph support.
 */
public class SimpleDigraph {
	/**
	 */
	public Graph getGraph() {

		/* Global settings, here we set the directed property: */
		Graph.Builder graph = new Graph.Builder().attr(Attr.Key.EDGE_STYLE,
				Graph.Attr.Value.CONNECTIONS_DIRECTED).attr(Attr.Key.LAYOUT,
				new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder().attr(Attr.Key.LABEL, "1").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder().attr(Attr.Key.LABEL, "2").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.Key.LABEL, "3").build(); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).build();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3).build();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();

	}
}
