import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public final class CanonicalCode {
	
	private int[] codeLengths;
	

	public CanonicalCode(int[] codeLens) {
		// Check basic validity
		Objects.requireNonNull(codeLens);
		if (codeLens.length < 2)
			throw new IllegalArgumentException("At least 2 symbols needed");
		for (int cl : codeLens) {
			if (cl < 0)
				throw new IllegalArgumentException("Illegal code length");
		}
		
		// Copy once and check for tree validity
		codeLengths = codeLens.clone();
		Arrays.sort(codeLengths);
		int currentLevel = codeLengths[codeLengths.length - 1];
		int numNodesAtLevel = 0;
		for (int i = codeLengths.length - 1; i >= 0 && codeLengths[i] > 0; i--) {
			int cl = codeLengths[i];
			while (cl < currentLevel) {
				if (numNodesAtLevel % 2 != 0)
					throw new IllegalArgumentException("Under-full Huffman code tree");
				numNodesAtLevel /= 2;
				currentLevel--;
			}
			numNodesAtLevel++;
		}
		while (currentLevel > 0) {
			if (numNodesAtLevel % 2 != 0)
				throw new IllegalArgumentException("Under-full Huffman code tree");
			numNodesAtLevel /= 2;
			currentLevel--;
		}
		if (numNodesAtLevel < 1)
			throw new IllegalArgumentException("Under-full Huffman code tree");
		if (numNodesAtLevel > 1)
			throw new IllegalArgumentException("Over-full Huffman code tree");
		
		// Copy again
		System.arraycopy(codeLens, 0, codeLengths, 0, codeLens.length);
	}
	

	public CanonicalCode(CodeTree tree, int symbolLimit) {
		Objects.requireNonNull(tree);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("At least 2 symbols needed");
		codeLengths = new int[symbolLimit];
		buildCodeLengths(tree.root, 0);
	}
	
	
	// Recursive helper method for the above constructor.
	private void buildCodeLengths(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			buildCodeLengths(internalNode.leftChild , depth + 1);
			buildCodeLengths(internalNode.rightChild, depth + 1);
		} else if (node instanceof Leaf) {
			int symbol = ((Leaf)node).symbol;
			if (symbol >= codeLengths.length)
				throw new IllegalArgumentException("Symbol exceeds symbol limit");
			// Note: CodeTree already has a checked constraint that disallows a symbol in multiple leaves
			if (codeLengths[symbol] != 0)
				throw new AssertionError("Symbol has more than one code");
			codeLengths[symbol] = depth;
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
	

	public int getSymbolLimit() {
		return codeLengths.length;
	}
	

	public int getCodeLength(int symbol) {
		if (symbol < 0 || symbol >= codeLengths.length)
			throw new IllegalArgumentException("Symbol out of range");
		return codeLengths[symbol];
	}
	

	public CodeTree toCodeTree() {
		List<Node> nodes = new ArrayList<Node>();
		for (int i = max(codeLengths); i >= 0; i--) {  // Descend through code lengths
			if (nodes.size() % 2 != 0)
				throw new AssertionError("Violation of canonical code invariants");
			List<Node> newNodes = new ArrayList<Node>();
			
			// Add leaves for symbols with positive code length i
			if (i > 0) {
				for (int j = 0; j < codeLengths.length; j++) {
					if (codeLengths[j] == i)
						newNodes.add(new Leaf(j));
				}
			}
			
			// Merge pairs of nodes from the previous deeper layer
			for (int j = 0; j < nodes.size(); j += 2)
				newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
			nodes = newNodes;
		}
		
		if (nodes.size() != 1)
			throw new AssertionError("Violation of canonical code invariants");
		return new CodeTree((InternalNode)nodes.get(0), codeLengths.length);
	}
	
	
	// Returns the maximum value in the given array, which must have at least 1 element.
	private static int max(int[] array) {
		int result = array[0];
		for (int x : array)
			result = Math.max(x, result);
		return result;
	}
	
}
