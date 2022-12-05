package task1;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for processing XML-file.
 *
 * Reads .xml file without loading full file in memory, makes changes inside tag <person../>,
 * writes result data into new .xml file, saves input formatting.
 */
public class PersonParser {

    /**
     * Variables with regular expressions used to process the .xml file
     *
     * <code>REGEX_FOR_PERSON</code> - pattern for full tag <person../person> with any formatting
     * <code>SURNAME_REGEX</code> - pattern for attribute surname with value ( surname="..." )
     * <code>ONLY_NAME_OR_SURNAME_REGEX</code> - pattern for name/surname value ( Тарас )
     */
    private static final String REGEX_FOR_TAG_PERSON = "[ \\n]*<person[\\S \\n]*\\/>[ \\n]*";
    private static final String REGEX_FOR_PERSON = "[ \\n]*<person[ \\n]*\\S*[ \\n]*=[ \\n]*\\S*[ \\n]*\\S*[ \\n]*=[ \\n]*\\S*[ \\n]*\\S*[ \\n]*=[ \\n]*\\S*[ \\n]*\\/>";
    private static final String SURNAME_REGEX = "\\bsurname *= *\"(.*?)\" ?";
    private static final String NAME_REGEX = "\\bname *= *\"(.*?)\" ?";
    private static final String ONLY_NAME_OR_SURNAME_REGEX = "\"[а-яА-ЯіІїЇЄєA-z]+\"";


    private static String replacingNameSurname(Pattern surnamePattern, Pattern onlyNameOrSurnamePattern, Pattern namePattern, String person){
        //finds surname="..." inside <person../person>
        Matcher surnameMatcher = surnamePattern.matcher(person);
        surnameMatcher.find();
        String surnameWithValue = surnameMatcher.group();

        //finds surname value from result above (Шевченко)
        Matcher onlyNameOrSurnameMatcher = onlyNameOrSurnamePattern.matcher(surnameWithValue);
        onlyNameOrSurnameMatcher.find();

        //saving surname value into variable
        String surname = onlyNameOrSurnameMatcher.group().replaceAll("\"","");

        //removes attribute surname with value from tag person
        String lineWithoutSurname = surnameMatcher.replaceAll("");

        //finds name value from tag person (it finds cyrillic symbols, surname already removed, it finds name only) (Тарас)
        Matcher nameMatcher = namePattern.matcher(lineWithoutSurname);
        nameMatcher.find();

        //saving  into variable name + surname
        String nameWithValue = nameMatcher.group();
        onlyNameOrSurnameMatcher = onlyNameOrSurnamePattern.matcher(nameWithValue);
        onlyNameOrSurnameMatcher.find();
        String name = onlyNameOrSurnameMatcher.group().replaceAll("\"","") + " " + surname;


        String fin = nameWithValue.replaceAll(ONLY_NAME_OR_SURNAME_REGEX, "\"" + name + "\"");

        nameMatcher = namePattern.matcher(lineWithoutSurname);

        //returns string with written to attribute name new value, new value is "name surname" (Тарас Шевченко)
        return nameMatcher.replaceAll(fin);
    }

    /**
     * Replaces attribute surname from tag <person.../person>, writes surname value into attribute name, saves input formatting
     *
     * @param personTag - full tag <person.../person> from input file
     * @return - reworked tag <person.../person> with input formatting
     */
    private static String findAndReplaceNameAndSurname(String personTag) {

        StringBuilder builder = new StringBuilder();

        //compiling patterns here, in order to do it before while loop
        Pattern regexForPerson = Pattern.compile(REGEX_FOR_PERSON);
        Pattern surnamePattern = Pattern.compile(SURNAME_REGEX);
        Pattern onlyNameOrSurnamePattern = Pattern.compile(ONLY_NAME_OR_SURNAME_REGEX);
        Pattern patternPersonTag = Pattern.compile(REGEX_FOR_TAG_PERSON);
        Pattern namePattern = Pattern.compile(NAME_REGEX);

        //Matchers for patterns above
        Matcher matcherForPerson = regexForPerson.matcher(personTag);
        Matcher surnameMatcher;
        Matcher matcherPersonTag = patternPersonTag.matcher(personTag);
        Matcher nameMatcher;
        Matcher matcherForPersonCheck = regexForPerson.matcher(personTag);

        // check if tag has not all the attributes
        if (matcherPersonTag.matches() && !matcherForPersonCheck.matches()){
            surnameMatcher = surnamePattern.matcher(personTag);
            nameMatcher = namePattern.matcher(personTag);
            //check if tag has attributes name and surname
            if (nameMatcher.find() && surnameMatcher.find()){
                return replacingNameSurname(surnamePattern, onlyNameOrSurnamePattern, namePattern, personTag);
            }
            return personTag;
        }

        //processing tag with all attributes
        while (matcherForPerson.find()) {
            String onePerson = matcherForPerson.group();
            builder.append(replacingNameSurname(surnamePattern, onlyNameOrSurnamePattern, namePattern, onePerson));
        }
        //returns new and ready to write string with input formatting
        return builder.toString();
    }

    /**
     * Processes xml file, writes new data to output xml file, does not load full file into memory, saves input formatting
     *
     * @param inputFileName - input file path
     * @param outputFileName - output file path
     */
    public static void processXMLFile(String inputFileName, String outputFileName) throws IOException{
        //initialising reader/writer using try-with-resources, no need to close it
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {

            //builder to work with string from file
            StringBuilder builder = new StringBuilder();
            String currentLine = reader.readLine();
            builder.append(currentLine).append("\n");



            while (currentLine != null) {

                //string with current builder value
                String lineToProcess = builder.toString();

                //looking for ">" in string
                if (lineToProcess.contains(">")) {

                    //if contains creates new string with first tag
                    String tag = builder.substring(0, builder.indexOf(">") + 1);
                    //if found start/end .xml file tag writing it to file saving the formatting
                    if (tag.contains("<persons>") || tag.contains("</persons>")) {
                        writer.write(tag);
                    } else {

                        //if found <person.../person> tag - process it with method
                        String res = findAndReplaceNameAndSurname(tag);
                        //writes result into file saving input formatting
                        writer.write(res);
                    }
                    //removing written into file tag from builder
                    builder.replace(0, builder.indexOf(">") + 1, "");
                } else {
                    //if line doesnt contain ">" adding next line to builder
                    currentLine = reader.readLine();
                    builder.append(currentLine).append("\n");
                }
            }

            //making sure writer finished writing output file
            writer.flush();
        } catch (IOException exception) {

            throw new IOException("problems with input/output file \n" + exception.getMessage());
        }
    }

}