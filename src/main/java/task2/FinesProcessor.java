package task2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import task2.entity.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this is main class, it has methods to read and parse .json files from input folder, methods to process parsed data, method to write .xml result file
 */
public class FinesProcessor {

    //regex for JSON object
    private static final String JSON_REGEX = " *\\{\n" +
            "* *(\"\\S*\" *: *\"\\S* \\S*\",*)\n" +
            "* *(\"\\S*\" *: *\"\\S*\",*)\n" +
            "* *(\"\\S*\" *: *\"\\S*\",*)\n" +
            "* *(\"\\S*\" *: *\"\\S*\",*)\n" +
            "* *(\"\\S*\" *: *\\d*.\\d\\d)\n" +
            "* *}";

    //mappers to read json and write xml
    private static final ObjectMapper OBJECT_MAPPER_DEFAULT;
    private static final XmlMapper XML_MAPPER;
    //initializing mappers
    static {
        OBJECT_MAPPER_DEFAULT = new ObjectMapper().setDateFormat(Fine.DATE_FORMAT);
        XML_MAPPER = new XmlMapper();
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    //returns all files in folder
    private static List<File> getFilesListFromFolder(final File folder) {
        ArrayList<File> filesInFolder = new ArrayList<>();

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {

            if (fileEntry.isDirectory()) {
                getFilesListFromFolder(fileEntry);
            } else {
                filesInFolder.add(new File(folder.getPath() + "\\" + fileEntry.getName()));
            }
        }
        return filesInFolder;
    }

    //returns map with statistic from file
    private static Map<DrivingViolationType, Double> getStatisticFromFile(File file) {
        Map<DrivingViolationType, Double> resultMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
        {
            StringBuilder stringBuilder = new StringBuilder();
            Pattern jsonFinePattern = Pattern.compile(JSON_REGEX);
            Matcher jsonFineMatcher;
            String currentLine = reader.readLine();
            while (currentLine!=null){
                stringBuilder.append(currentLine);
                jsonFineMatcher = jsonFinePattern.matcher(stringBuilder);
                if (jsonFineMatcher.find()){

                    String jsonFineString = jsonFineMatcher.group();

                    Fine currentFine = Fine.fromJson(OBJECT_MAPPER_DEFAULT, jsonFineString);

                    if (!resultMap.containsKey(currentFine.getViolationType()))
                        resultMap.put(currentFine.getViolationType(), currentFine.getFineAmount());
                    else
                        resultMap.put(currentFine.getViolationType(), resultMap.get(currentFine.getViolationType()) + currentFine.getFineAmount());
                    stringBuilder = new StringBuilder();
                }
                currentLine = reader.readLine();
            }
        } catch (IOException |ParseException exception){
            throw new RuntimeException(exception);
        }
        return resultMap;
    }

    //returns map with statistic from files in folder
    private static Map<DrivingViolationType, Double> getStatisticFromFolder(List<File> files) {
        Map<DrivingViolationType, Double> resultMap = new HashMap<>();
        Map<DrivingViolationType, Double> currentFileStatMap;

        for (File file : files) {
            currentFileStatMap = getStatisticFromFile(file);
            Iterator<Map.Entry<DrivingViolationType, Double>> mapIterator = currentFileStatMap.entrySet().iterator();
            while (mapIterator.hasNext()){
                Map.Entry<DrivingViolationType, Double> pair = mapIterator.next();

                if (!resultMap.containsKey(pair.getKey()))
                    resultMap.put(pair.getKey(), pair.getValue());
                else
                    resultMap.put(pair.getKey(), resultMap.get(pair.getKey()) + pair.getValue());

                mapIterator.remove();
            }
        }
        return resultMap;
    }

    //returns sorted list from map with stat. (some parsers do not serialize maps, but every parser can serialize list)
    private static List<Fine> getSortedListFromMap(Map<DrivingViolationType, Double> mapWithSummaryInfo){
        List<Fine> resultList = new ArrayList<>();
        Iterator<Map.Entry<DrivingViolationType, Double>> mapIterator = mapWithSummaryInfo.entrySet().iterator();
        while (mapIterator.hasNext()){
            Map.Entry<DrivingViolationType, Double> pair = mapIterator.next();
            resultList.add(new Fine( pair.getKey(), pair.getValue()));
            mapIterator.remove();
        }
        resultList.sort((fine1, fine2) -> (int)(fine2.getFineAmount() - fine1.getFineAmount()));
        return resultList;
    }

    //write stat. to XML-file using parser
    private static void writeToXMLFileWithParser(String outputFilePath, List<Fine> listWithSums) {

            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath)))) {
                String xmlList = XML_MAPPER.writeValueAsString(listWithSums).replaceAll("ArrayList", "ViolationStatistics");
                bufferedWriter.write(xmlList);
                bufferedWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

    //main method to run the program
    public static void getFinesStatistics(String inputFolderPath, String outputFilePath) {
            writeToXMLFileWithParser(outputFilePath, getSortedListFromMap(getStatisticFromFolder(getFilesListFromFolder(new File(inputFolderPath)))));
    }

}
