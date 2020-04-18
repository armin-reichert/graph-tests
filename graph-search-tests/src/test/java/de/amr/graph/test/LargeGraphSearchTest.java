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
import de.amr.maze.alg.ust.WilsonUSTRandomCell;

public class LargeGraphSearchTest {

	GridGraph2D<TraversalState, Integer> graph;

	GridGraph2D<TraversalState, Integer> randomDenseGrid(int c, int r, GridTopology gridTopology) {
		GridGraph2D<TraversalState, Integer> g = new GridGraph<TraversalState, Integer>(c, r, gridTopology,
				v -> TraversalState.UNVISITED, (u, v) -> 0, UndirectedEdge::new);
		new WilsonUSTRandomCell(g).createMaze(0, 0);
		int numFullGridEdges = gridTopology == Grid4Topology.get() ? 2 * c * r - c - r : 4 * c * r - 3 * c - 3 * r + 2;
		int maxEdgesToAdd = numFullGridEdges - g.numEdges();
		int numEdgesToAdd = maxEdgesToAdd * 50 / 100;
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
		graph = randomDenseGrid(100, 1000, Grid8Topology.get());
	}

	@Test
	public void testLargeGraphSearch() {
		GraphSearch search = new AStarSearch(graph, (u, v) -> 1, graph::euclidean);
		System.out.println(String.format("Graph has %,d vertices and %,d edges", graph.numVertices(), graph.numEdges()));
		long time = System.nanoTime();
		Path path = search.findPath(0, graph.numVertices() - 1);
		time = System.nanoTime() - time;
		assertTrue("Path is not empty", path.numEdges() > 0);
		System.out.println("Path length is " + path.numEdges());
		System.out.println(String.format("Search time: %d milliseconds", time / 1_000_000));
	}
}
