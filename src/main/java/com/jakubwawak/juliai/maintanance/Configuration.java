/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.juliai.maintanance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Object for reading and storing configuration file
 */
public class Configuration {

    String configurationPath = "juliai.properties";
    String filenamesrc;
    File dir;

    public boolean configurationExists;
    public boolean error, newfile;

    public String requestURL;
    public String uiColor1,uiColor2;

    /**
     * Constructor
     */
    public Configuration(){
        // checking for file
        dir = new File(".");
        File[] directoryListening = dir.listFiles();
        for(File child : directoryListening){
            if ( child.getName().contains(configurationPath) ){
                filenamesrc = child.getAbsolutePath();
                break;
            }
        }
        if (filenamesrc!= null){
            System.out.println("Loading configuration file - found file!");
            // properties file found
            configurationExists = true;
            // load configuration file
            newfile = false;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filenamesrc));
                String line = reader.readLine();
                while ( line!= null ){
                    if (line.startsWith("#")){
                        switch(line.split("%")[0]){
                            case "#requestURL":
                            {
                                requestURL = line.split("%")[1];
                                break;
                            }
                            case "#uiColor1":
                            {
                                uiColor1 = line.split("%")[1];
                                break;
                            }
                            case "#uiColor2":
                            {
                                uiColor2 = line.split("%")[1];
                                break;
                            }
                        }
                    }
                    line = reader.readLine();
                }
            }catch (Exception ex){
                error = true;
                System.out.println("Failed reading configuration ("+ex.toString()+")");
            }
        }
        else{
            System.out.println("Loading default configuration...");
            configurationExists = false;
            // setting default values
            requestURL = "http://localhost:11434";
            uiColor1 = "######";
            uiColor2 = "000000";
            // load configuration to file
            try{
                System.out.println("Saving configuration to file...");
                File file = new File(filenamesrc);
                FileWriter fw = new FileWriter(filenamesrc);
                fw.write("juliai configuration file!\n");
                fw.write("by Jakub Wawak\n");
                fw.write("#requestURL%" + requestURL + "\n");
                fw.write("#uiColor1%" + uiColor1 + "\n");
                fw.write("#uiColor2%" + uiColor2 + "\n");
                fw.close();
                newfile = true;
                error = false;
                System.out.println("Configuration was saved!");
            }catch(Exception ex){
                error = true;
                System.out.println("Error writing config file ("+ex.toString()+")");
            }
        }
    }

    /**
     * Function for loading configuration file
     */
    void loadConfiguration(){

    }

}
