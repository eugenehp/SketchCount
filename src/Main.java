import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {
    public static void main( String[] args ) {
        String inputFilePath = "";
        File directory = new File(".");
        String sketchTableFilePath = "." + File.separator + "Count_Sketch.txt";
        String sketchTableInputFilePath = "";
        double accuracy = 100; // accuracy of the results ε
        double probability = 100; // probability of bad estimate δ

        if(args.length == 0) {
            printInstructions();
            return;
        }

        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            String[] ar = arg.split("=");


            if(ar.length == 2) {
                String key = ar[0];
                String value = ar[1];

                if(key.equals("Input_File"))
                    inputFilePath = value;

                if(key.equals("Count_Sketch"))
                    sketchTableInputFilePath = value;

                if(key.equals("accuracy"))
                    accuracy= Integer.valueOf(value);

                if(key.equals("probability"))
                    probability= Integer.valueOf(value);
            }
        }

        if(inputFilePath.length() == 0){
            printInstructions();
            return;
        }

        int seed = 0;
        CountSketch engine = new CountSketch(accuracy, probability, seed);

        if(sketchTableInputFilePath.length() > 0) {
            loadTable(sketchTableInputFilePath, engine, accuracy);
        }

        readInputFile(inputFilePath, engine);

        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("To exit type `q` and hit <Enter>.");
            System.out.println("To save table type `s` and hit <Enter>.");
            System.out.println("========================================");
            System.out.println("Enter a word to get: ");

            while (true) {

                String input = br.readLine();

                if ("q".equals(input)) {
                    System.out.println("Exit!");
                    System.exit(0);
                } else if ("s".equals(input)) {
                    saveTable(sketchTableFilePath, engine);
                    System.out.println("Saved to "+sketchTableFilePath);
                } else {
//                    System.out.println("Estimating ["+ input +"]");
                    long estimate = engine.estimateCount(input);
                    System.out.println( "Estimated frequency for " + input + " is " + estimate );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readInputFile(String filePath, CountSketch engine)
    {
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                engine.add(line, 1);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveTable(String filePath, CountSketch engine) {
        long[][] matrix = engine.getData();

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    String delimiter = (j == matrix[i].length-1) ? "" : ",";
                    bw.write(matrix[i][j] + delimiter);
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadTable(String filePath, CountSketch engine, double dimension) {
        System.out.println("Loaded Count_Sketch table from " + filePath);

        try {
            long[][] data = new long[(int)dimension][];
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int i=0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(",");
                long[] row = new long[strings.length];

                for(int j=0;j<strings.length;j++)
                    row[j] = Integer.valueOf(strings[j]);

                data[i] = row;
                i++;
            }
            fileReader.close();

            engine.setData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printGrid(long[][] data)
    {
        int count = data.length;
        System.out.println("printGrid "+ count);
        for(int i = 0; i < count; i++)
        {
            int rowLength = data[i].length;

            for(int j = 0; j < rowLength; j++)
            {
                System.out.printf("%5d ", data[i][j]);
            }
            System.out.println();
        }
    }

    public static void printInstructions() {
        System.out.println("================================================================================================================");
        System.out.println("Usage:");
        System.out.println("java -jar Sketch.jar Input_File=input.txt Count_Sketch=table.txt accuracy=10 probability=10");
        System.out.println("================================================================================================================");
        System.out.println("");
        System.out.println("Where `Input_File` is absolute path to input file with new line separated words");
        System.out.println("Where `Count_Sketch` is absolute path to  ");
        System.out.println("Where `accuracy` – accuracy of the results ε");
        System.out.println("Where `probability` - and probability of bad estimate δ");
        System.out.println("");
        System.out.println("================================================================================================================");
        System.out.println("");
        System.out.println("");
    }
}