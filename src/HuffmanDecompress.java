import java.io.*;

public final class HuffmanDecompress {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java HuffmanDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File(args[0]);
		File outputFile = new File(args[1]);
		System.out.println("Compressed file size:" + inputFile.length());

		// Perform file decompression
		try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
				CanonicalCode canonCode = readCodeLengthTable(in);
				CodeTree code = canonCode.toCodeTree();
				decompress(code, in, out);
			}
			System.out.println("Decompressed file size:" + outputFile.length());
		}
	}
	
	private static CanonicalCode readCodeLengthTable(BitInputStream in) throws IOException {
		int[] codeLengths = new int[257];
		for (int i = 0; i < codeLengths.length; i++) {
			// For this file format, we read 8 bits in big endian
			int val = 0;
			for (int j = 0; j < 8; j++)
				val = (val << 1) | in.readNoEof();
			codeLengths[i] = val;
		}
		return new CanonicalCode(codeLengths);
	}
	
	private static void decompress(CodeTree code, BitInputStream in, OutputStream out) throws IOException {
		HuffmanDecoder dec = new HuffmanDecoder(in);
		dec.codeTree = code;
		while (true) {
			int symbol = dec.read();
			if (symbol == 256)  // EOF symbol
				break;
			out.write(symbol);
		}
	}
	
}
