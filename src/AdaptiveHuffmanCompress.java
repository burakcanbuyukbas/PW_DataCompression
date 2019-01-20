import java.io.*;
import java.util.Arrays;

public final class AdaptiveHuffmanCompress {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java AdaptiveHuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File(args[0]);
		File outputFile = new File(args[1]);
		System.out.println("Input file size:" + inputFile.length());

		// Perform file compression
		try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
			try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				compress(in, out);
			}
			System.out.println("Compressed file size reduced to:" + outputFile.length());
		}
	}
	
	
	private static void compress(InputStream in, BitOutputStream out) throws IOException {
		int[] initialFrequences = new int[257];
		Arrays.fill(initialFrequences, 1);
		
		FrequencyTable frequencies = new FrequencyTable(initialFrequences);
		HuffmanEncoder encoder = new HuffmanEncoder(out);
		encoder.codeTree = frequencies.buildCodeTree();
		int count = 0;  // Number of bytes read from the input file
		while (true) {
			// Read and encode one byte
			int symbol = in.read();
			if (symbol == -1)
				break;
			encoder.write(symbol);
			count++;

			// Update the frequency table
			frequencies.increment(symbol);
			if (count < 262144 && isPowerOf2(count) || count % 262144 == 0)  // Update code tree
				encoder.codeTree = frequencies.buildCodeTree();
			if (count % 262144 == 0)  // Reset frequency table
				frequencies = new FrequencyTable(initialFrequences);
		}
		encoder.write(256);
	}
	
	
	private static boolean isPowerOf2(int x) {
		return x > 0 && Integer.bitCount(x) == 1;
	}
	
}
