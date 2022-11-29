package task2;

public class Main {
    //variables below can be changed in order to read/write from/to file you need
    public static final String INPUT_FOLDER_PATH = "C:\\Users\\dazarat\\Desktop\\ProfITSoft\\SecondHomework\\SecondAssignment\\src\\main\\resources\\fineFileDirectory";
    public static final String OUTPUT_FILE_PATH = "C:\\Users\\dazarat\\Desktop\\ProfITSoft\\SecondHomework\\SecondAssignment\\src\\main\\resources\\fineStatistic.xml";

    public static void main(String[] args) {
        FinesProcessor.getFinesStatistics(INPUT_FOLDER_PATH, OUTPUT_FILE_PATH, true);
    }
}
