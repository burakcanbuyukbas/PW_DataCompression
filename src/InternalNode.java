import java.util.Objects;

public final class InternalNode extends Node {
	
	public final Node leftChild;
	
	public final Node rightChild;
	
	
	
	public InternalNode(Node left, Node right) {
		leftChild = Objects.requireNonNull(left);
		rightChild = Objects.requireNonNull(right);
	}
	
}
