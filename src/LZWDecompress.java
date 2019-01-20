import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LZWDecompress {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java RLEDecompress InputFile OutputFile");
            System.exit(1);
            return;
        }
        File inputFile  = new File(args[0]);
        File outputFile = new File(args[1]);
        System.out.println("Compressed file size:" + inputFile.length());

        try{
            String inputFileText = new String(Files.readAllBytes(Paths.get(inputFile.getAbsolutePath())));
            inputFileText = inputFileText.trim();
            inputFileText = inputFileText.substring(1, inputFileText.length() - 1);
            String[] str = inputFileText.split(", ");
            //System.out.println("Input file text:" + inputFileText);

            List<Integer> intList = new ArrayList<>();
            for(String s : str) intList.add(Integer.valueOf(s));

            String decodedText = decode(intList);
            //System.out.println("Output file text:" + decodedText);

            List<String> lines = Arrays.asList(decodedText);
            Path file = Paths.get(outputFile.getAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Decompressed file size:" + outputFile.length());

        }
        catch (Exception e){
            System.out.println("Compression failed:" + e.toString());
        }
    }


    /** Decompress a list of output ks to a string. */
    public static String decode(List<Integer> compressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char)i);

        String w = "" + (char)(int)compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k))
                entry = dictionary.get(k);
            else if (k == dictSize)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));

            w = entry;
        }
        return result.toString();
    }
}
