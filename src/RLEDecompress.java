import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RLEDecompress {


    public static void main(String[] args){

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
            //System.out.println("Input file text:" + inputFileText);
            String decodedText = decode(inputFileText);
            //System.out.println("Output file text:" + decodedText);

            List<String> lines = Arrays.asList(decodedText);
            Path file = Paths.get(outputFile.getAbsolutePath());
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Decompressed file size:" + outputFile.length());

        }
        catch (Exception e){
            System.out.println("Compression failed." + e.toString());
        }
    }

    static String decode(String source) {
        StringBuffer stringBuffer = new StringBuffer();

        Pattern pattern = Pattern.compile("[0-9]+|[a-zA-Z]");
        Matcher matcher = pattern.matcher(source);

        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group());
            matcher.find();
            while (num-- != 0) {
                stringBuffer.append(matcher.group());
            }
        }

        return stringBuffer.toString();
    }


}
