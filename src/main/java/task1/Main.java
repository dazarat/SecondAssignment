package task1;

import java.io.IOException;

public class Main {

    public static final String INPUT = "C:\\Users\\dazarat\\Desktop\\ProfITSoft\\SecondHomework\\SecondAssignment\\src\\main\\resources\\input.xml";
    public static final  String OUTPUT = "C:\\Users\\dazarat\\Desktop\\ProfITSoft\\SecondHomework\\SecondAssignment\\src\\main\\resources\\output.xml";

    public static void main(String[] args) throws IOException {

        PersonParser.processXMLFile(INPUT, OUTPUT);
    }
}
