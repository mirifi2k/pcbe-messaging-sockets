package upt.pcbe.messaging.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageTest {

    @BeforeEach
    void setUp() throws Exception {
        Process theProcess = null;
        BufferedReader inStream = null;
   
        System.out.println("Server.main() invoked");
   
        // call the Server class
        try
        {
            theProcess = Runtime.getRuntime().exec("java QIBMHello");
        }
        catch(IOException e)
        {
           System.err.println("Error on exec() method");
           e.printStackTrace();  
        }
          
        // read from the called program's standard output stream
        try
        {
           inStream = new BufferedReader(
                                  new InputStreamReader( theProcess.getInputStream() ));  
           System.out.println(inStream.readLine());
        }
        catch(IOException e)
        {
           System.err.println("Error on inStream.readLine()");
           e.printStackTrace();  
        }
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void test() {
        
    }

}
