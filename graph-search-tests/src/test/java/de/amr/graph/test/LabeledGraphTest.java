package de.amr.graph.test;

import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.impl.UGraph;
import de.amr.graph.pathfinder.impl.DepthFirstSearch;

public class LabeledGraphTest {

	private UGraph<String, Integer> graph;

	private UGraph<String, Integer> createSampleGraph() {
		UGraph<String, Integer> g = new UGraph<>();
		IntStream.range(0, 8).forEach(g::addVertex);
		g.set(0, "S");
		g.set(1, "A");
		g.set(2, "B");
		g.set(3, "C");
		g.set(4, "D");
		g.set(5, "E");
		g.set(6, "F");
		g.set(7, "G");
		g.addEdge(0, 1); // S-A
		g.addEdge(1, 2); // A-B
		g.addEdge(2, 3); // B-C
		g.addEdge(0, 4); // A-D
		g.addEdge(4, 1); // D-A
		g.addEdge(4, 5); // D-E
		g.addEdge(5, 2); // E-B
		g.addEdge(5, 6); // E-F
		g.addEdge(6, 7); // F-G
		return g;
	}

	@Before
	public void setUp() {
		graph = new UGraph<>();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetLabelOnNonexistingVertex() {
		graph.set(0, "A");
	}

	@Test
	public void testSetLabel() {
		graph.addVertex(0);
		graph.set(0, "A");
		Assert.assertEquals("A", graph.get(0));
	}

	@Test
	public void testDefaultVertexLabel() {
		graph.setDefaultVertexLabel(v -> "42");
		graph.addVertex(0);
		Assert.assertEquals("42", graph.get(0));
		graph.set(0, "43");
		Assert.assertNotEquals("42", graph.get(0));
	}

	@Test
	public void testSampleGraph() {
		UGraph<String, Integer> g = createSampleGraph();
		Assert.assertTrue(g.adjacent(0, 1));
		Assert.assertTrue(g.adjacent(1, 2));
		Assert.assertTrue(g.adjacent(2, 3));
		Assert.assertTrue(g.adjacent(0, 4));
		Assert.assertTrue(g.adjacent(4, 1));
		Assert.assertTrue(g.adjacent(4, 5));
		Assert.assertTrue(g.adjacent(5, 2));
		Assert.assertTrue(g.adjacent(5, 6));
		Assert.assertTrue(g.adjacent(6, 7));
	}

	@Test
	public void testSampleGraphDFS() {
		UGraph<String, Integer> g = createSampleGraph();
		DepthFirstSearch dfs = new DepthFirstSearch(g);
		DFSTracer tracer = new DFSTracer(g);
		dfs.addObserver(tracer);
		dfs.exploreGraph(0);
		System.out.println(tracer.getVertexTrace());
		System.out.println(tracer.getVertexLabelTrace());
	}
}
