/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.juliai;

import com.jakubwawak.juliai.ollama_connector.OllamaAPIConnectorSync;

/**
 * Object for creating tests
 */
public class JuliaiTest {

    /**
     * Constructor
     */
    public JuliaiTest(){
        System.out.println("Runing tests...");
        run();
    }

    /**
     * Function for running tests
     */
    void run(){
        try{
            OllamaAPIConnectorSync apc = new OllamaAPIConnectorSync("http://localhost:11434");
            System.out.println(apc.getModels().get_array("models").get(0));
        }catch(Exception ex){
            System.out.println(ex.toString());
        }

    }
}
