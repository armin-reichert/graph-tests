package de.amr.graph.test;

import static de.amr.datastruct.StreamUtils.randomElement;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridTopology;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.Grid8Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DepthFirstSearch;
import de.amr.graph.pathfinder.impl.HillClimbingSearch;
import de.amr.maze.alg.ust.WilsonUSTRandomCell;

public class LargeGraphSearchTest {

	GridGraph2D<TraversalState, Integer> graph;
	int source, target;

	GridGraph2D<TraversalState, Integer> randomGrid(int c, int r, GridTopology gridTopology) {
		GridGraph2D<TraversalState, Integer> g = new GridGraph<TraversalState, Integer>(c, r, gridTopology,
				v -> TraversalState.UNVISITED, (u, v) -> 0, UndirectedEdge::new);
		new WilsonUSTRandomCell(g).createMaze(0, 0);
		int numFullGridEdges = gridTopology == Grid4Topology.get() ? 2 * c * r - c - r : 4 * c * r - 3 * c - 3 * r + 2;
		int maxEdgesToAdd = numFullGridEdges - g.numEdges();
		int numEdgesToAdd = maxEdgesToAdd * 10 / 100;
		while (numEdgesToAdd > 0) {
			int v = new Random().nextInt(g.numVertices());
			Optional<Integer> unconnectedNeighbor = randomElement(g.neighbors(v).filter(w -> !g.adjacent(v, w)));
			if (unconnectedNeighbor.isPresent()) {
				g.addEdge(v, unconnectedNeighbor.get());
				numEdgesToAdd--;
			}
		}
		return g;
	}

	@Before
	public void createFixture() {
		graph = randomGrid(500, 1000, Grid8Topology.get());
		System.out.println(String.format("Graph has %,d vertices and %,d edges", graph.numVertices(), graph.numEdges()));
		System.out.println();
		source = 0;
		target = graph.numVertices() - 1;
	}

	void testLargeGraph(GraphSearch search) {
		System.out.println("Path finder: " + search.getClass());
		long time = System.nanoTime();
		Path path = search.findPath(source, target);
		time = System.nanoTime() - time;
		assertTrue("Path is not empty", path.numEdges() > 0);
		assertTrue("Path starts with source ", path.source() == source);
		assertTrue("Path ends with target ", path.target() == target);
		System.out.println("Path length is " + path.numEdges());
		System.out.println(String.format("Search time: %d milliseconds", time / 1_000_000));
		System.out.println();
	}

	@Test
	public void testLargeGraph() {
		testLargeGraph(new BreadthFirstSearch(graph));
		testLargeGraph(new DepthFirstSearch(graph));
		testLargeGraph(new AStarSearch(graph, (u, v) -> 1, graph::euclidean));
		testLargeGraph(new BestFirstSearch(graph, v -> graph.manhattan(v, target)));
		testLargeGraph(new HillClimbingSearch(graph, v -> graph.manhattan(v, target)));
	}
}