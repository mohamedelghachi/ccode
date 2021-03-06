package fromctoxml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

/**
 * The main funciton of this class is to generate the XML structure of 
 * any script programmed in C programming language, So The input should be a 
 * .c file, and by running this class (main function) it will generate the 
 * corresponding XML file.
 * @author EL GHACHI MOHAMED
 */
public class FromCodetoXML{

    /**
     *is a list containing all primitive data types (byte, short, int, long ...)
     */
    public static List<String> primitiveDataTypes = new ArrayList<String>() {{
        add("byte");
        add("short");
        add("int");
        add("long");
        add("float");
        add("double");
        add("boolean");
        add("char");
        add("void");
    }};
    /**
     * calling of "parseToXml" function with a string in parameter which is the
     * name of the input file
     * @param args : not used
     * @throws FileNotFoundException for input file.
     * @throws IOException for I/O excepiton for I/O excepiton
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        parseToXml("simpleTest");
    }

    /**
     * This function do the followins steps:<br>
     * &emsp;1- create BuffredReader (br) and BuffredWriter (bw)
     *    br will read the input file
     *    bw will write in the output file<br>
     * &emsp;2- delete empty lines of the input file and also non significatif spaces by calling preTraitement(br,bw) function, the result will be will be stored in a tomporary file  xyz_temp,C<br>
     * &emsp;3- From the top of the input file, the loop search for lines containing
     *    one of the 'keyword' which needs brackets (like: if, for, while statement) (see: needBrackets(strLine))<br>
     * &emsp;4- when the function find line needing brackets, the "searchEndBracket" function search
     *    for the end bracket of the current line.<br>
     * @param inputFileName  is a string representing the name of the input file with extention (.c)
     * @return returns the corresponding xml file (file type)
     * @throws FileNotFoundException for input file.
     * @throws IOException for I/O excepiton
     */
    public static File parseToXml(String inputFileName) throws FileNotFoundException, IOException{
        String inputFilePath = "test\\Files\\input\\"+inputFileName+".c";
        FileInputStream fstream = new FileInputStream(inputFilePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String tempOutputFilePath = "test\\Files\\temp\\"+inputFileName+"_temp.c";
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(tempOutputFilePath)));
        
        //This function is used to delete empty lines and trim them
        preTraitement(br,bw);
        
        fstream = new FileInputStream(tempOutputFilePath);
        br = new BufferedReader(new InputStreamReader(fstream));
        String outputFilePath = "test\\Files\\output\\"+inputFileName+"_output.xml";
        File returnedFile = new File(outputFilePath);
        bw = new BufferedWriter(new FileWriter(returnedFile));

        String strLine;
        //Read File Line By Line
        try {
            while ((strLine = br.readLine()) != null)   {
                //check if the current needs brackets
                String instruction;
                instruction = needBrackets(strLine);
                if(instruction != null){
                    int tab = -1;
                    searchEndBracket(strLine,instruction,br,bw,tab);
                }
            }
        }catch(IOException e){
               
        }finally {
            //Close the input and stream
            br.close();
            bw.close();
        }
        return returnedFile;
    }
    
    /**
    * delete empty lines of the input file and also non significatif spaces (trim function)
    * @param br : buffredReader of the input file.
    * @param bw : bufferedWriter of the output file.
    * @throws IOException for I/O excepiton 
    */
    private static void preTraitement(BufferedReader br, BufferedWriter bw) throws IOException {
        String strLine;
        //Read File Line By Line
        try {
            while ((strLine = br.readLine()) != null)   {
                //remove spaces from a code line
                strLine = strLine.trim();
                //check just not empty line
                if(!strLine.equals("")){
                    strLine = strLine.replace("<", "&lt");
                    strLine = strLine.replace(">", "&gt");
                    bw.write(strLine,0,strLine.length());
                    bw.newLine();
                }
            }
        }catch(IOException e){

        }finally {
            //Close the input and stream
            br.close();
            bw.close();
        }
    }
    /**
     * check if the parameter (which represents the current line) is the begining of a function definition, 
     * if statement, for loop by calling a boolean function of the specific kind of statement
     * @param strLine is a string corresponding to the statement type
     * @return string describing the kind of line  if it needs brackets, null if not.
     */
    private static String needBrackets(String strLine) {
        if(isFunctionDefinition(strLine)){
            return "function";
        }
        if(isIf_Statement(strLine)){
            return "if";
        }
        if(isElse_Statement(strLine)){
            return "else";
        }
        if(isFor_loop(strLine)){
            return "for";
        }
        if(isWhile_loop(strLine)){
            return "while";
        }
        return null;
    }
    /**
     * This is a recursive function<br>
     * steps:<br>
     * &emsp;1- print first tab of 'line' parameter by calling printAccordingFirstTag(line,bw,++tab) function<br>
     * &emsp;2- read a new ligne and should be '{'<br>
     * &emsp;3- read next line, this new line can be in one of the current cases:<br>
     *    &emsp;&emsp;case 1: the line does not need brackets (e.g sample statement) in that case we just print this line in the output file<br>
     *    &emsp;&emsp;case 2: the line needs a btackets and in this case we call the same function (recursivity)<br>
     *    &emsp;&emsp;case 3: if it is a '}' print end tag (by calling printAccordingEndTag(line,bw,++tab) function) and return<br>
     * @param line string corresponding to the current line
     * @param br BufferedReader of the input file
     * @param bw BufferedWriter of the ouput file
     * @param tab int for printing tabulation in the output file (root tab should hab 0 in this param, first level should be 1 and so on)
     * @throws IOException for I/O excepiton 
     */
    private static void searchEndBracket(String line, String instruction, BufferedReader br, BufferedWriter bw,int tab) throws IOException {
        //print first tab of line
        //read new ligne and should be '{'
        //read another new line
        //if it is a '}' print end tag and return
        //else check if the new line needs a btackets and call the same function
        String instructionOriginale = instruction;
        printAccordingFirstTag(line,instructionOriginale,bw,++tab);
        br.readLine();//strLine should be '{'
        String strLine;
        while ((strLine = br.readLine()) != null){
            if(strLine.equals("}")){
                printAccordingEndTag(line,instructionOriginale,bw,++tab);
                tab--;
                return;
            }
            //check if the current needs brackets
            instruction = needBrackets(strLine);
            if(instruction != null){
                searchEndBracket(strLine,instruction,br,bw,++tab);
                tab--;
            }else{
                printXML_("<instruction>"+strLine+"</instruction>",bw,tab+=2);
                tab-=2;
            }
        }
        printAccordingEndTag(line,instructionOriginale,bw,++tab);
        tab--;
    }
    /**
     * This fucntion is used to check whether if the current code line is a function definition, if statement
     * for loop ,,, then call according function to print According tag
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printAccordingFirstTag(String line, String instruction, BufferedWriter bw, int tab) throws IOException {
        if(instruction.equals("function")){
            printXMl_functionDefinition(line,bw,tab);
            return;
        }
        if(instruction.equals("if")){
            printXMl_if(line,bw,tab);
            return;
        }
        if(instruction.equals("else")){
            printXMl_else(line,bw,tab);
            return;
        }
        if(instruction.equals("for")){
            printXMl_for(line,bw,tab);
            return;
        }
        if(instruction.equals("while")){
            printXMl_while(line,bw,tab);
            return;
        }
    }
    /**
     * This function checks if the current line is a function definition or not by spliting (by space) the line and check if it contains:<br>
     * &emsp;- data type in the first part<br>
     * &emsp;- the second part should contain open and close parentheses
     * @param line string containing current code line
     * @return true if the current line is a function definition, false otherwise
     */
    private static boolean isFunctionDefinition(String line) {
        //in a function definition we should have at least two parts separated by a space caracter
        String[] parts = line.split(" ");
        if(parts.length <=1){ return false; }
        //check if its not a function call, so it should contains a primitive data type in the beginning
        if(!primitiveDataTypes.contains(parts[0])){ return false; }
        //get rest of the string without return type
        line = line.substring(parts[0].length()+1, line.length());//+1 for first space character ' '
        //The second part of the header should contains brackets '(' and ')'
        if(!line.contains("(") || !line.contains(")")){
            return false;
        } 
        return true;
    }
    /**
     * This function checks if the current line is an if statement or not by spliting (by open parenthese) the line and check if it contains:<br>
     * &emsp;- the keyword 'if' and open parenthese in the first part<br>
     * &emsp;- the second part should contain close parenthese
     * @param line string containing current code line
     * @return true if the current line is an if statement, false otherwise
     */
    private static boolean isIf_Statement(String line) {
        //The line should begin with if and à '('
        String[] parts = line.split("\\(");
        if(!parts[0].equals("if")){ return false; }
        //get rest of the string without return type
        line = line.substring(parts[0].length(), line.length());
        //The second part of the header should contains bracket ')'
        if(!line.contains(")")){
            return false;
        }
        return true;
    }
    /**
     * This function checks if the current line is an else statement or not by spliting (by open parenthese) the line and check if it contains:<br>
     * &emsp;- the keyword 'else' and open parenthese in the first part<br>
     * &emsp;- the second part should contain close parenthese
     * @param line string containing current code line
     * @return true if the current line is an if statement, false otherwise
     */
    private static boolean isElse_Statement(String line) {
        //line should contain only 'line' word
        if(!line.equals("else")){ return false; }
        return true;
    }
    /**
     * This function checks if the current line is a for loop or not by spliting (by open parenthese) the line and check if it contains:<br>
     * &emsp;- the keyword 'for' and open parenthese in the first part<br>
     * &emsp;- the second part should contain close parenthese<br>
     * &emsp;- the third part should containing exatly two semicolons ';'
     * @param line string containing current code line
     * @return true if the current line is a for loop, false otherwise
     */
    private static boolean isFor_loop(String line) {
        //The line should begin with if and à '('
        String[] parts = line.split("\\(");
        if(!parts[0].equals("for")){ return false; }
        //get rest of the string without return type
        line = line.substring(parts[0].length(), line.length());//+1 for first space character ' '
        //The second part of the header should contains bracket ')'
        if(line.indexOf(")") != line.length()-1){
            return false;
        }
        line = line.substring(0, line.length()-1);
        String[] forElements  = line.split("\\;");
        if(forElements.length != 3)
            return false;
        return true;
    }
    /**
     * This function checks if the current line is a while loop or not by spliting (by open parenthese) the line and check if it contains:<br>
     * &emsp;- the keyword 'while' and open parenthese in the first part<br>
     * &emsp;- the second part should containing exatly two semicolons ';'<br>
     * &emsp;- the third part should contain close parenthese
     * @param line string containing current code line
     * @return true if the current line is a for loop, false otherwise
     */
    private static boolean isWhile_loop(String line) {
        //The line should begin with if and à '('
        String[] parts = line.split("\\(");
        if(!parts[0].equals("while")){ return false; }
        //get rest of the string without return type
        line = line.substring(parts[0].length(), line.length());//+1 for first space character ' '
        //The second part of the header should contains bracket ')'
        if(line.indexOf(")") != line.length()-1){
            return false;
        }
        return true;
    }
    /**
     * This function is used to print (by calling printXML_ function) the XML tag of the function definition containing in the current line<br>
     * Function definition is characterized by:<br>
     * &emsp;- function tag with name attribute<br>
     * &emsp;- returntype tag<br>
     * &emsp;- arguments tag<br>
     * &emsp;- body tag<br>
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printXMl_functionDefinition(String line,BufferedWriter bw,int tab) throws IOException {
        // La fonction commence par le type de retour puis le nom de la fonction 
        //puis une liste de paramètres entre parenthèses séparés par des virgules
        //in a function definition we should have at least two parts separated by a space caracter
        String[] parts = line.split(" ");
        String returnType = parts[0];
        //get rest of the string without return type
        line = line.substring(parts[0].length()+1, line.length());//+1 for first bracket ' '
        
        //The second part of header of a function definifion is its name and the arguments between brackets
        parts = null;
        parts = line.split("\\(");
        String functionName = parts[0];
        //print xml of the current function
        //print function tag with name attribute
        printXML_("<function name='"+functionName+"'>",bw,tab);
        //print tag of the returned type function
        printXML_("<returntype>"+returnType+"</returntype>",bw,tab+1);
        //get rest of the string without return type, function name and first bracket
        line = line.substring(parts[0].length()+1, line.length()-1);//+1 for first bracket '(' ||| -1 to skipe the second brackt
        if(!line.equals("")){
            //print argumets tag
            printXML_("<arguments>",bw,tab+1);
            String[] paramsWithType = line.split(",");
            int i=1;
            for(String params : paramsWithType){
                params = params.trim();//remove space characters
                //separate type and variable name of the current parameter
                String[] partsOfParameter = params.split(" ");
                String params_type = partsOfParameter[0];
                String params_variableName = partsOfParameter[1];
                //print parameters tag
                printXML_("<argument"+i+">",bw,tab+2);
                //print type of the current argument tag
                printXML_("<type>"+params_type+"</type>",bw,tab+3);
                //print variable name of the current argument tag
                printXML_("<name>"+params_variableName+"</name>",bw,tab+3);
                //print parameters end tag
                printXML_("</argument"+i+">",bw,tab+2);
                i++;
            }
            //print arguments end tag
            printXML_("</arguments>",bw,tab+1);
            //print parameters end tag
            printXML_("<body>",bw,tab+1);
        }
        /*System.out.println("</function>");*/
        
    }
    /**
     * This function is used to print (by calling printXML_ function) the XML tag of the if statement containing in the current line<br>
     * if statement is characterized by:<br>
     * &emsp;- if tag<br>
     * &emsp;- condition tag<br>
     * &emsp;- body tag<br>
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printXMl_if(String line, BufferedWriter bw,int tab) throws IOException {
        //print if tag
        int tabulation = tab;
        printXML_("<if>",bw,tabulation);
        String[] parts = line.split("\\(");
        parts = parts[1].split("\\)");
        //print condition tag of the if statement
        printXML_("<condition>"+parts[0]+"</condition>",bw,++tabulation);
        //print parameters end tag
        printXML_("<body>",bw,tabulation);
    }
    /**
     * This function is used to print (by calling printXML_ function) the XML tag of the else statement containing in the current line<br>
     * if statement is characterized by:<br>
     * &emsp;- else tag<br>
     * &emsp;- condition tag<br>
     * &emsp;- body tag<br>
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printXMl_else(String line, BufferedWriter bw,int tab) throws IOException {
        //print if tag
        int tabulation = tab;
        printXML_("<else>",bw,tabulation);
        printXML_("<body>",bw,++tabulation);
    }
    /**
     * This function is used to print (by calling printXML_ function) the XML tag of the for loop containing in the current line<br>
     * for loop is characterized by:<br>
     * &emsp;- for tag<br>
     * &emsp;- elements tag<br>
     * &emsp;- body tag<br>
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printXMl_for(String line, BufferedWriter bw,int tab) throws IOException {
        int tabulation = tab;
        printXML_("<for>",bw,tabulation);
        String[] parts = line.split("\\(");
        parts = parts[1].split("\\)");
        String forElements[] = parts[0].split("\\;");
        //print intialisation, condition and accrementation tags of the for loop
        printXML_("<elements>",bw,++tabulation);
        printXML_("<intialization>"+forElements[0]+"</intialization>",bw,++tabulation);
        printXML_("<condition>"+forElements[1]+"</condition>",bw,tabulation);
        printXML_("<increment>"+forElements[2]+"</increment>",bw,tabulation);
        printXML_("</elements>",bw,--tabulation);
        //print parameters end tag
        printXML_("<body>",bw,tabulation);
    }
    /**
     * This function is used to print (by calling printXML_ function) the XML tag of the while loop containing in the current line<br>
     * while loop is characterized by:<br>
     * &emsp;- while tag<br>
     * &emsp;- condition tag<br>
     * &emsp;- body tag<br>
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printXMl_while(String line, BufferedWriter bw, int tab) throws IOException {
        int tabulation = tab;
        printXML_("<while>",bw,tabulation);
        String[] condition = line.split("\\(");
        condition = condition[1].split("\\)");
        //print condition tag
        printXML_("<condition>"+condition[0]+"</condition>",bw,++tabulation);
        //print parameters end tag
        printXML_("<body>",bw,tabulation);
    }
    /**
     * This function is used to print the XML line (containing in 'tags' argument) into the output file<br>
     * it stats by printing tabulations ( \t ) numberOfTabulation times<br>
     * at the end, the bw shoult point into the next line.
     * @param tags XML line to print
     * @param bw BufferedWriter to write the begening of the according tag
     * @param numberOfTabulation number of tabulations to print
     * @throws IOException for I/O excepiton
     */
    private static void printXML_(String tags,BufferedWriter bw, int numberOfTabulation) throws IOException {
        for(int i = 1;i <= numberOfTabulation; i++){
            tags = "\t"+tags;
            System.out.print("\t");
        }
        bw.write(tags,0,tags.length());
        bw.newLine();
        System.out.println(tags);
    }
    /**
     * This fucntion is used to check whether if the current code line is a function definition, if statement
     * for loop ,,, then print the according end tag
     * @param line string containing current code line
     * @param bw BufferedWriter to write the begening of the according tag
     * @param tab used for formating tabulations in the output file
     * @throws IOException for I/O excepiton 
     */
    private static void printAccordingEndTag(String line, String instruction, BufferedWriter bw,int tab) throws IOException {
        if(instruction.equals("function")){
            printXML_("</body>",bw,tab);
            printXML_("</function>",bw,--tab);
            return;
        }
        if(instruction.equals("if")){
            printXML_("</body>",bw,tab);
            printXML_("</if>",bw,--tab);
            return;
        }
        if(instruction.equals("else")){
            printXML_("</body>",bw,tab);
            printXML_("</else>",bw,--tab);
            return;
        }
        if(instruction.equals("for")){
            printXML_("</body>",bw,tab);
            printXML_("</for>",bw,--tab);
            return;
        }
        if(instruction.equals("while")){
            printXML_("</body>",bw,tab);
            printXML_("</while>",bw,--tab);
            return;
        }
    }

    
}