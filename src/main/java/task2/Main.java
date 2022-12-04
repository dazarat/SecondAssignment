package task2;

import java.io.IOException;

public class Main {
    public static final String INPUT = "C:\\Users\\dazarat\\Desktop\\ProfITSoft\\SecondHomework\\SecondAssignment\\src\\main\\resources\\fineFileDirectory";
    public static final  String OUTPUT = "C:\\Users\\dazarat\\Desktop\\ProfITSoft\\SecondHomework\\SecondAssignment\\src\\main\\resources\\fineStatistic.xml";

    public static void main(String[] args) throws IOException {
        FinesProcessor.getFinesStatistics(INPUT, OUTPUT);
    }
}
