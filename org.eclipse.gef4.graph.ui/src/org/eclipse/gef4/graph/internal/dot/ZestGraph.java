package org.eclipse.gef4.graph.internal.dot;

import java.util.List;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ZestGraph extends Graph {

	public ZestGraph(Composite parent, int style) {
		super(parent, style);
	}

	public ZestGraph(Composite parent, int style,
			org.eclipse.gef4.graph.Graph dotGraph) {
		super(parent, style);
		List<Edge> edges = dotGraph.getEdges();
		for (Edge edge : edges) {
			Node source = edge.getSource();
			Node target = edge.getTarget();
			GraphNode sourceZestNode = dotNodeToZestNode(source);
			GraphNode targetZestNode = dotNodeToZestNode(target);
			dotEdgeToZestEdge(edge, sourceZestNode, targetZestNode);
		}
		// TODO: set layout from DOT:
		this.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
	}

	private GraphConnection dotEdgeToZestEdge(Edge edge,
			GraphNode sourceZestNode, GraphNode targetZestNode) {
		// TODO: pass through all attributes, like this:
		String key = org.eclipse.gef4.graph.Graph.Attr.EDGE_STYLE.toString();
		Object val = edge.getAttribute(key);
		System.out.printf("key: %s val: %s\n", key, val); //$NON-NLS-1$
		return new GraphConnection(this, SWT.NONE, sourceZestNode,
				targetZestNode); // Integer.valueOf((String) val)
	}

	private GraphNode dotNodeToZestNode(Node source) {
		// TODO: pass through all attributes, like this:
		String key = org.eclipse.gef4.graph.Graph.Attr.NODE_STYLE.toString();
		Object val = source.getAttribute(key);
		System.out.printf("key: %s val: %s\n", key, val); //$NON-NLS-1$
		return new GraphNode(this, SWT.NONE); // Integer.valueOf((String) val)
	}
}
