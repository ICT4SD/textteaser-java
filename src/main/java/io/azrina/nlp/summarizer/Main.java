package io.azrina.nlp.summarizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class Main
{

    public static void main(String[] args) throws Exception {

        Options options = new Options();

        // define arguments
        Option text_opt = new Option("t", "text", true, "text content.");
        Option file_opt = new Option("f", "file", true, "input text file. will be ignored if the text is provided using '-t' argument.");
        Option lang_opt = new Option("l", "lang", true, "text language. defaults to en. supported languages : en, es, fr, ar, ru, zh-cn");
        Option title_opt = new Option("i", "title", true, "text title. defaults to empty string.");
        Option delimiter_opt = new Option("d", "delimiter", true, "delimiter between sentences. defaults to ' (...) '.");
        Option num_opt = new Option("n", "num", true, "max number of sentences. defaults to 3.");

        options.addOption(text_opt);
        options.addOption(file_opt);
        options.addOption(lang_opt);
        options.addOption(title_opt);
        options.addOption(delimiter_opt);
        options.addOption(num_opt);

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;

        try {

            cmd = parser.parse(options, args);

        } catch (ParseException e) {

            System.out.println(e.getMessage());
            formatter.printHelp("textteaser-java", options);
            System.exit(1);
            return;
        }

        String text = cmd.getOptionValue("t");
        String filename = cmd.getOptionValue("f");
        String title = cmd.getOptionValue("i");
        String lang = cmd.getOptionValue("l");
        String delimiter = cmd.getOptionValue("d");
        String num_str = cmd.getOptionValue("n");
        Integer num;

        try {

            // Set defaults
            title = (title == null) ? "" : title;
            lang = (lang == null) ? "en" : lang;
            delimiter = (delimiter == null) ? " (...) " : delimiter;
            num = (num_str == null) ? 3 : Integer.parseInt(num_str);

            // Get text
            if ((text == null) && (filename == null)){
                System.out.println("ERROR : Please provide text using -t or -f argument. See -help for more instruction.");
                System.exit(1);
                return;
            } else if (text == null){ // obtain text from file
                text = Helper.readFile(filename);
            }

            // Summarizer
            Summarizer summarizer = new Summarizer(lang); // initialize summarizer with specific language
            String result = summarizer.summarize(title, text, num, delimiter);
            System.out.println(result);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
