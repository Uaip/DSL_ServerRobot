package mysocketRobot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class parser {
    Map<String, List<String>> step = new HashMap<>();
    Map<String, List<String>> answer = new HashMap<>();
    String curStep = "";
    String entry = "";
    user A = null;
    String file = "";

    public parser() {

    }

    /**
     * 
     * @param FileName
     * @param u
     */
    void parseFile(String FileName, user u) {
        file = FileName;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName), "UTF-8"))) {
            // uses UTF-8 encoding format
            A = u;
            String line = null;
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                line = line.trim();
                if (!line.equals("") && line.charAt(0) != '#') {
                    parseLine(line);
                }
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param line
     */
    void parseLine(String line) {
        String[] curLine = line.split(" ");
        if (curLine[0].equalsIgnoreCase("step")) {
            if (entry.equals(""))
                entry = curLine[1]; // store the entry step
            curStep = curLine[1];
            step.put(curLine[1], new ArrayList<String>());
            answer.put(curLine[1], new ArrayList<String>());
        } else {
            step.get(curStep).add(curLine[0]); // store steps in a state
            switch (curLine[0]) {
                case "Speak":
                    answer.get(curStep).add(speakProcess(curLine));
                    break;
                case "Listen":
                case "Branch":
                case "Silence":
                case "Default":
                    answer.get(curStep).add(nonSpeakProcess(curLine));
                    break;
                case "Exit":
                    answer.get(curStep).add("exit");
                    break;
                default:
                    System.out.println("ERROR: The parser FAILED to parse the file " + file);
                    System.out.println("Infomation:\t" + curLine[0]);
                    return;
            }
        }
    }

    /**
     * 
     * @param curLine
     * @return
     */

    String nonSpeakProcess(String[] curLine) { // 非Speak处理程序
        String line = "";
        for (int i = 1; i < curLine.length; i++) {
            line += curLine[i];
        }
        return line;
    }

    /**
     * 
     * @param curLine
     * @return
     */
    String speakProcess(String curLine[]) {
        String line = "";
        for (int i = 1; i < curLine.length; i++) {
            if (curLine[i].charAt(0) != '$')
                line += curLine[i];
            else {
                // getName or getAmount
                // user info is used in speak action
                if (curLine[i].substring(1, curLine[i].length()).equalsIgnoreCase("name")) {// ignorecase of name
                    line += A.getName();
                } else if (curLine[i].substring(1, curLine[i].length()).equalsIgnoreCase("amount")) {// ignorecase of
                                                                                                     // amount
                    line += A.getAmount();
                }
            }
        }
        line = line.replace("+", " ");
        line = line.replace("\"", " ");
        return line;
    }

    void showparser() {
        for (Map.Entry<String, List<String>> entry : step.entrySet()) {
            System.out.println("step " + entry.getKey());
            System.out.println("part " + entry.getValue());
        }
        for (Map.Entry<String, List<String>> entry : answer.entrySet()) {
            System.out.println("step " + entry.getKey());
            System.out.println("answer " + entry.getValue());
            System.out.println("---------------------------------------------------");
        }

    }
}