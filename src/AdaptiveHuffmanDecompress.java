import java.io.*;
import java.util.Arrays;

public final class AdaptiveHuffmanDecompress {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java AdaptiveHuffmanDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File(args[0]);
		File outputFile = new File(args[1]);
		System.out.println("Compressed file size:" + inputFile.length());

		// Perform file decompression
		try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
				decompress(in, out);
			}
			System.out.println("Decompressed file size:" + outputFile.length());

		}
	}
	
	
	private static void decompress(BitInputStream in, OutputStream out) throws IOException {
		int[] initialFrequencies = new int[257];
		Arrays.fill(initialFrequencies, 1);
		
		FrequencyTable frequencies = new FrequencyTable(initialFrequencies);
		HuffmanDecoder decoder = new HuffmanDecoder(in);
		decoder.codeTree = frequencies.buildCodeTree();
		int count = 0;  // Number of bytes of output file
		while (true) {
			// Decode and write one byte
			int symbol = decoder.read();
			if (symbol == 256)
				break;
			out.write(symbol);
			count++;
			
			// Update the frequency table
			frequencies.increment(symbol);
			if (count < 262144 && isPowerOf2(count) || count % 262144 == 0)  // Update code tree
				decoder.codeTree = frequencies.buildCodeTree();
			if (count % 262144 == 0)  // Reset frequency table
				frequencies = new FrequencyTable(initialFrequencies);
		}
	}
	
	
	private static boolean isPowerOf2(int x) {
		return x > 0 && Integer.bitCount(x) == 1;
	}
	
}
