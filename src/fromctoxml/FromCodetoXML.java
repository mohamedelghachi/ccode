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
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        parseToXml("simpleTest");
    }

    /**
     * This function do the followins steps:<br>
     * 1- create BuffredReader (br) and BuffredWriter (bw)
     *    br will read the input file
     *    bw will write in the output file<br>
     * 2- delete empty lines of the input file and also non significatif spaces (see: preTraitement(br,bw);)<br>
     * 3- From the top of the input file, the loop search for lines containing
     *    one of the 'keyword' which needs brackets (like: if, for, while statement) (see: needBrackets(strLine))<br>
     * 4- whene the function find line needing brackets, the "searchEndBracket" function search
     *    for the end bracket of the current line.<br>
     * @param inputFileName  is a string representing the name of the input file with extention (.c)
     * @return returns the corresponding xml file (file type)
     * @throws FileNotFoundException
     * @throws IOException
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
                    searchEndBracket(strLine,br,bw,tab);
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
    * @throws IOException 
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
     * @return 
     */
    private static String needBrackets(String strLine) {
        if(isFunctionDefinition(strLine)){
            return "function";
        }
        if(isIf_Statement(strLine)){
                return "if";
        }
        if(isFor_loop(strLine)){
                return "for";
        }
        return null;
    }
    /**
     * This is a recursive function<br>
     * steps:<br>
     * 1- print first tab of 'line' parameter by calling printAccordingFirstTag(line,bw,++tab) function<br>
     * 2- read a new ligne and should be '{'<br>
     * 3- read next line, this new line can be in one of the current cases:<br>
     *    case 1: the line does not need brackets (e.g sample statement) in that case we just print this line in the output file<br>
     *    case 2: the line needs a btackets and in this case we call the same function (recursivity)
     *    case 3: if it is a '}' print end tag (by calling printAccordingEndTag(line,bw,++tab) function) and return<br>
     * @param line string corresponding to the current line
     * @param br BufferedReader of the input file
     * @param bw BufferedWriter of the ouput file
     * @param tab int for printing tabulation in the output file (root tab should hab 0 in this param, first level should be 1 and so on)
     * @throws IOException 
     */
    private static void searchEndBracket(String line,BufferedReader br, BufferedWriter bw,int tab) throws IOException {
        //print first tab of line
        //read new ligne and should be '{'
        //read another new line
        //if it is a '}' print end tag and return
        //else check if the new line needs a btackets and call the same function
        printAccordingFirstTag(line,bw,++tab);
        br.readLine();//strLine should be '{'
        String strLine;
        while ((strLine = br.readLine()) != null){
            if(strLine.equals("}")){
                printAccordingEndTag(line,bw,++tab);
                tab--;
                return;
            }
            //check if the current needs brackets
            String instruction;
            instruction = needBrackets(strLine);
            if(instruction != null){
                searchEndBracket(strLine,br,bw,++tab);
                tab--;
            }else{
                printXML_("<instruction>"+strLine+"</instruction>",bw,tab+=2);
                tab-=2;
            }
        }
        printAccordingEndTag(line,bw,++tab);
        tab--;
    }
    private static void printAccordingFirstTag(String line, BufferedWriter bw, int tab) throws IOException {
        if(isFunctionDefinition(line)){
            printXMl_functionDefinition(line,bw,tab);
            return;
        }
        if(isIf_Statement(line)){
            printXMl_if(line,bw,tab);
            return;
        }
        if(isFor_loop(line)){
            printXMl_for(line,bw,tab);
            return;
        }
    }
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
    private static void printXMl_for(String line, BufferedWriter bw,int tab) throws IOException {
        //print if tag
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
    private static void printXML_(String tags,BufferedWriter bw, int numberOfTabulation) throws IOException {
        for(int i = 1;i <= numberOfTabulation; i++){
            tags = "\t"+tags;
            System.out.print("\t");
        }
        bw.write(tags,0,tags.length());
        bw.newLine();
        System.out.println(tags);
    }

    private static void printAccordingEndTag(String line, BufferedWriter bw,int tab) throws IOException {
        if(isFunctionDefinition(line)){
            printXML_("</body>",bw,tab);
            printXML_("</function>",bw,--tab);
            return;
        }
        if(isIf_Statement(line)){
            printXML_("</body>",bw,tab);
            printXML_("</if>",bw,--tab);
            return;
        }
        if(isFor_loop(line)){
            printXML_("</body>",bw,tab);
            printXML_("</for>",bw,--tab);
            return;
        }
    }

    
}