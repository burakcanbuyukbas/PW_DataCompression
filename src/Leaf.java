public final class Leaf extends Node {
	
	public final int symbol;
	
	
	
	public Leaf(int sym) {
		if (sym < 0)
			throw new IllegalArgumentException("Invalid argument.");
		symbol = sym;
	}
	
}
