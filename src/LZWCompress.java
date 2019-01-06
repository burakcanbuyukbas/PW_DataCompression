import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LZWCompress {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java LZWCompress InputFile OutputFile");
            System.exit(1);
            return;
        }
        File inputFile  = new File(args[0]);
        File outputFile = new File(args[1]);
        System.out.println("Input file size:" + inputFile.length());

        try{
            String inputFileText = new String(Files.readAllBytes(Paths.get(inputFile.getAbsolutePath())));
            System.out.println("Input file text:" + inputFileText);
            String encodedText = encode(inputFileText).toString();
            System.out.println("Output file text:" + encodedText);

            List<String> lines = Arrays.asList(encodedText);
            Path file = Paths.get(outputFile.getAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Compressed file size reduced to:" + outputFile.length());

        }
        catch (Exception e){
            System.out.println("Compression failed:" + e.toString());
        }
    }

    public static List<Integer> encode(String uncompressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);

        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));
        return result;
    }


}
