package de.amr.graph.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.amr.graph.core.impl.UGraph;
import de.amr.graph.pathfinder.api.GraphSearchObserver;

class DFSTracer implements GraphSearchObserver {

	final UGraph<String, Integer> graph;
	final List<Integer> trace = new ArrayList<>();

	public DFSTracer(UGraph<String, Integer> graph) {
		this.graph = graph;
	}

	@Override
	public void vertexAddedToFrontier(int vertex) {
		trace.add(vertex);
	}

	public String getVertexTrace() {
		return trace.stream().map(String::valueOf).collect(Collectors.joining());
	}

	public String getVertexLabelTrace() {
		return trace.stream().map(graph::get).collect(Collectors.joining());
	}
}