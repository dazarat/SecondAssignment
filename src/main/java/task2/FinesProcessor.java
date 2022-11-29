package task2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import task2.entity.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.List;
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
        try {
            for (final File fileEntry : folder.listFiles()) {

                if (fileEntry.isDirectory()) {
                    getFilesListFromFolder(fileEntry);
                }
                else {
                    filesInFolder.add(new File(folder.getPath() + "\\" + fileEntry.getName()));
                }

            }
            return filesInFolder;

        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
            System.out.println("Folder is empty!");
        }
        return filesInFolder;
    }

    //returns list with all fines from single file
    //boolean variable is used to choose option: read full fine info including fields that aren't used in counting stat. or read only violation type and fine amount
    //reading only violation type and fine amount can save memory while working with big amount of fines
    private static List<Fine> getFineListFromFile(File file, boolean readFullFineInfo){
        List<Fine> resultList = new ArrayList<>();

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
                    resultList.add(Fine.fromJson(OBJECT_MAPPER_DEFAULT, jsonFineString, readFullFineInfo));
                    stringBuilder = new StringBuilder();
                }
                currentLine = reader.readLine();
            }
        } catch (IOException |ParseException exception){
            exception.printStackTrace();
        }
        return resultList;
    }

    //returns list with all fines from every file in folder
    //boolean variable is used to choose option: read full fine info including fields that aren't used in counting stat. or read only violation type and fine amount
    //reading only violation type and fine amount can save memory while working with big amount of fines
    private static List<Fine> getFineListFromFolder(List<File> files, boolean readFullFineInfo){
        List<Fine> resultList = new ArrayList<>();
        for (File file : files) {
            resultList.addAll(getFineListFromFile(file, readFullFineInfo));
        }
        return resultList;
    }

    //counting stat. using map where key = violationType, value = sum of fine amount by this violationType
    private static Map<DrivingViolationType, Double> getMapWithTotalSumByEveryType(List<Fine> listWithFines){

        Map<DrivingViolationType,Double> resultMap = new HashMap<>();

        for (Fine currentFine : listWithFines){
            if (!resultMap.containsKey(currentFine.getViolationType()))
                resultMap.put(currentFine.getViolationType(), currentFine.getFineAmount());
            else
                resultMap.put(currentFine.getViolationType(), resultMap.get(currentFine.getViolationType()) + currentFine.getFineAmount());
        }
        return resultMap;
    }

    //returns sorted list from map with stat. (some parsers do not serializing maps, but every parser can serialize list)
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
    private static void writeToXMLFileWithParser(String outputFilePath, List<Fine> listWithSums){

            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath)))) {
                String xmlList = XML_MAPPER.writeValueAsString(listWithSums).replaceAll("ArrayList", "ViolationStatistics");;
                bufferedWriter.write(xmlList);
                bufferedWriter.flush();
            } catch (IOException e) {

                e.printStackTrace();
            }

    }

    //main method to run the program
    //boolean variable is used to choose option: read full fine info including fields that aren't used in counting stat. or read only violation type and fine amount
    //reading only violation type and fine amount can save memory while working with big amount of fines
    public static void getFinesStatistics(String inputFolderPath, String outputFilePath, boolean readFullFineInfo){

            writeToXMLFileWithParser(outputFilePath, getSortedListFromMap(
                    getMapWithTotalSumByEveryType(getFineListFromFolder(
                            getFilesListFromFolder(new File(inputFolderPath)), readFullFineInfo))));

    }

}
