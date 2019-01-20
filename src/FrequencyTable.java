import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public final class FrequencyTable {

	private int[] frequencies;

	public FrequencyTable(int[] freqs) {
		Objects.requireNonNull(freqs);
		if (freqs.length < 2)
			throw new IllegalArgumentException("At least 2 symbols needed");
		frequencies = freqs.clone();  // Defensive copy
		for (int x : frequencies) {
			if (x < 0)
				throw new IllegalArgumentException("Negative frequency");
		}
	}

	public int getSymbolLimit() {
		return frequencies.length;
	}
	

	public int get(int symbol) {
		checkSymbol(symbol);
		return frequencies[symbol];
	}

	public void set(int symbol, int freq) {
		checkSymbol(symbol);
		if (freq < 0)
			throw new IllegalArgumentException("Negative frequency");
		frequencies[symbol] = freq;
	}
	

	public void increment(int symbol) {
		checkSymbol(symbol);
		if (frequencies[symbol] == Integer.MAX_VALUE)
			throw new IllegalStateException("Maximum frequency reached");
		frequencies[symbol]++;
	}
	
	
	// Returns silently if 0 <= symbol < frequencies.length, otherwise throws an exception.
	private void checkSymbol(int symbol) {
		if (symbol < 0 || symbol >= frequencies.length)
			throw new IllegalArgumentException("Symbol out of range");
	}
	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < frequencies.length; i++)
			sb.append(String.format("%d\t%d%n", i, frequencies[i]));
		return sb.toString();
	}
	

	public CodeTree buildCodeTree() {
		Queue<NodeWithFrequency> pqueue = new PriorityQueue<NodeWithFrequency>();
		
		// Add leaves for symbols with non-zero frequency
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, frequencies[i]));
		}
		
		// Pad with zero-frequency symbols until queue has at least 2 items
		for (int i = 0; i < frequencies.length && pqueue.size() < 2; i++) {
			if (frequencies[i] == 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, 0));
		}
		if (pqueue.size() < 2)
			throw new AssertionError();
		
		// Repeatedly tie together two nodes with the lowest frequency
		while (pqueue.size() > 1) {
			NodeWithFrequency x = pqueue.remove();
			NodeWithFrequency y = pqueue.remove();
			pqueue.add(new NodeWithFrequency(
				new InternalNode(x.node, y.node),
				Math.min(x.lowestSymbol, y.lowestSymbol),
				x.frequency + y.frequency));
		}
		
		// Return the remaining node
		return new CodeTree((InternalNode)pqueue.remove().node, frequencies.length);
	}
	
	
	
	// Helper structure for buildCodeTree()
	private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {
		
		public final Node node;
		public final int lowestSymbol;
		public final long frequency;  // Using wider type prevents overflow
		
		
		public NodeWithFrequency(Node nd, int lowSym, long freq) {
			node = nd;
			lowestSymbol = lowSym;
			frequency = freq;
		}
		
		
		// Sort by ascending frequency, breaking ties by ascending symbol value.
		public int compareTo(NodeWithFrequency other) {
			if (frequency < other.frequency)
				return -1;
			else if (frequency > other.frequency)
				return 1;
			else if (lowestSymbol < other.lowestSymbol)
				return -1;
			else if (lowestSymbol > other.lowestSymbol)
				return 1;
			else
				return 0;
		}
		
	}
	
}
