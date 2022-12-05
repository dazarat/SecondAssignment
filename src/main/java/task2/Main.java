package task2;

import java.io.IOException;

public class Main {
    public static final String INPUT = "src/main/resources/fineFileDirectory";
    public static final  String OUTPUT = "src/main/resources/fineStatistic.xml";

    public static void main(String[] args) throws IOException {
        FinesProcessor.getFinesStatistics(INPUT, OUTPUT);
    }
}
