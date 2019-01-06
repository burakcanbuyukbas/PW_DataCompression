import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RLECompress {


    public static void main(String[] args){

        if (args.length != 2) {
            System.err.println("Usage: java RLECompress InputFile OutputFile");
            System.exit(1);
            return;
        }
        File inputFile  = new File(args[0]);
        File outputFile = new File(args[1]);
        System.out.println("Input file size:" + inputFile.length());

        try{
            String inputFileText = new String(Files.readAllBytes(Paths.get(inputFile.getAbsolutePath())));
            //System.out.println("Input file text:" + inputFileText);
            String encodedText = encode(inputFileText);
            //System.out.println("Output file text:" + encodedText);

            List<String> lines = Arrays.asList(encodedText);
            Path file = Paths.get(outputFile.getAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Compressed file size reduced to:" + outputFile.length());

        }
        catch (Exception e){
            System.out.println("Compression failed:" + e.toString());
        }
    }

    static String encode(String source) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < source.length(); i++) {
            int runLength = 1;

            while (i + 1 < source.length() && source.charAt(i) == source.charAt(i + 1)) {
                runLength++;
                i++;
            }

            stringBuffer.append(runLength); // AAA -> 3
            stringBuffer.append(source.charAt(i)); // A
        }

        return stringBuffer.toString();
    }


}
