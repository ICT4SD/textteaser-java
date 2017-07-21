package io.azrina.nlp.summarizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

/**
 * <h1>Helper</h1> This class contains util functions which used throughout the app.
 **/

public class Helper {

    public static String readFile(String filename){

        String line = "";
        String result = "";
        BufferedReader br = null;

        try {
            br = new BufferedReader (new FileReader(filename));
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e){
            System.out.println("ERROR : Problem in reading file! Please provide correct filepath.");
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                System.out.println("ERROR : Error while closing BufferedReader!");
                e.printStackTrace();
            }
         }

        return result;

    }

}