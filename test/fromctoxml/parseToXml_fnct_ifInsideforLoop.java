/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fromctoxml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author THINKPAD W540
 */
public class parseToXml_fnct_ifInsideforLoop {
    
    public parseToXml_fnct_ifInsideforLoop() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class FromCodetoXML.
     */
    @Test
    public void testMain() throws Exception {
        String inputFileName = "fnct_ifInsideForLoop";
        String outputFilePath = "test\\Files\\expected\\"+inputFileName+"_exp.xml";
        final FileInputStream expected = new FileInputStream(outputFilePath);
        final File output = FromCodetoXML.parseToXml(inputFileName);
        assertTrue(equalFiles(expected, output)) ;
    }
    
   
    private static boolean equalFiles(FileInputStream expectedFileInputStream,
        File outputFile) {
        boolean equal;
        BufferedReader bExp;
        BufferedReader bRes;
        String expLine ;
        String resLine ;

        equal = false;
        bExp = null ;
        bRes = null ;

        try {
            bExp = new BufferedReader(new InputStreamReader(expectedFileInputStream));
            bRes = new BufferedReader(new FileReader(outputFile));

            if ((bExp != null) && (bRes != null)) {
                expLine = bExp.readLine() ;
                resLine = bRes.readLine() ;

                equal = ((expLine == null) && (resLine == null)) || ((expLine != null) && expLine.equals(resLine)) ;

                while(equal && expLine != null)
                {
                    expLine = bExp.readLine() ;
                    resLine = bRes.readLine() ; 
                    equal = expLine.trim().equals(resLine.trim());
                }
                System.out.println(expLine);
                System.out.println(resLine);
            }
        } catch (Exception e) {

        } finally {
            try {
                if (bExp != null) {
                    bExp.close();
                }
                if (bRes != null) {
                    bRes.close();
                }
            } catch (Exception e) {
            }

        }

        return equal;

    }
    
}
