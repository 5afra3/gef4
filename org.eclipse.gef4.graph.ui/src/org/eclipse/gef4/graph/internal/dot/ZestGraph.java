/*******************************************************************************
 * Copyright (c) 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.internal.dot;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Wrap {@link org.eclipse.gef4.graph.Graph} in
 * {@link org.eclipse.gef4.zest.core.widgets.Graph} API.
 * <p/>
 * http://bugs.eclipse.org/372365
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class ZestGraph extends Graph {

	public ZestGraph(Composite parent, int style) {
		super(parent, style);
	}

	public ZestGraph(Composite parent, int style,
			org.eclipse.gef4.graph.Graph dotGraph) {
		super(parent, style);
		Map<Node, GraphNode> nodes = new HashMap<Node, GraphNode>();
		for (Node node : dotGraph.getNodes()) {
			GraphNode graphNode = dotNodeToZestNode(node);
			nodes.put(node, graphNode);
		}
		for (Edge edge : dotGraph.getEdges()) {
			GraphNode sourceZestNode = nodes.get(edge.getSource());
			GraphNode targetZestNode = nodes.get(edge.getTarget());
			dotEdgeToZestEdge(edge, sourceZestNode, targetZestNode);
		}
		setLayout(dotGraph);
		setGraphData(dotGraph);
		setGraphType(dotGraph);
	}

	private void setLayout(org.eclipse.gef4.graph.Graph dotGraph) {
		LayoutAlgorithm algorithm = new TreeLayoutAlgorithm();
		Object layout = dotGraph.getAttribute(Attr.LAYOUT.toString());
		if (layout != null)
			algorithm = (LayoutAlgorithm) layout;
		this.setLayoutAlgorithm(algorithm, true);
	}

	private void setGraphData(org.eclipse.gef4.graph.Graph dotGraph) {
		for (Map.Entry<String, Object> entry : dotGraph.getAttributes()
				.entrySet())
			this.setData(entry.getKey(), entry.getValue());
	}

	private void setGraphType(org.eclipse.gef4.graph.Graph dotGraph) {
		@SuppressWarnings("serial")
		Map<ZestStyle, Integer> map = new HashMap<ZestStyle, Integer>() {
			{
				put(ZestStyle.GRAPH_DIRECTED, ZestStyles.CONNECTIONS_DIRECTED);
				put(ZestStyle.GRAPH, ZestStyles.CONNECTIONS_SOLID);
			}
		};
		Object type = dotGraph.getAttribute(Attr.GRAPH_TYPE.toString());
		if (type != null)
			this.setConnectionStyle(map.get(ZestStyle.valueOf(type.toString())));
	}

	private GraphConnection dotEdgeToZestEdge(Edge edge,
			GraphNode sourceZestNode, GraphNode targetZestNode) {
		@SuppressWarnings("serial")
		Map<ZestStyle, Integer> map = new HashMap<ZestStyle, Integer>() {
			{
				put(ZestStyle.LINE_DASH, SWT.LINE_DASH);
				put(ZestStyle.LINE_DASHDOT, SWT.LINE_DASHDOT);
				put(ZestStyle.LINE_DASHDOTDOT, SWT.LINE_DASHDOTDOT);
				put(ZestStyle.LINE_DOT, SWT.LINE_DOT);
				put(ZestStyle.LINE_SOLID, SWT.LINE_SOLID);
				put(ZestStyle.NONE, ZestStyles.NONE);
			}
		};
		GraphConnection connection = new GraphConnection(this, SWT.NONE,
				sourceZestNode, targetZestNode);
		Object edgeStyle = edge.getAttribute(Attr.EDGE_STYLE.toString());
		if (edgeStyle != null) {
			Integer style = map.get(ZestStyle.valueOf(edgeStyle.toString()));
			connection.setLineStyle(style);
		}
		Object label = edge.getAttribute(Attr.LABEL.toString());
		if (label != null)
			connection.setText(label.toString());
		Object data = edge.getAttribute(Attr.DATA.toString());
		if (data != null)
			connection.setData(data);
		return connection;
	}

	private GraphNode dotNodeToZestNode(Node node) {
		GraphNode graphNode = new GraphNode(this, SWT.NONE);
		Object label = node.getAttribute(Attr.LABEL.toString());
		if (label != null)
			graphNode.setText(label.toString());
		Object data = node.getAttribute(Attr.DATA.toString());
		if (data != null)
			graphNode.setData(data);
		return graphNode;
	}
}
