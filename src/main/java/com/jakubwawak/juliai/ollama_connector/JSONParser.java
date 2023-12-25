/*
Jakub Wawak
kubawawak@gmail.com
all rights reseved
 */
package com.jakubwawak.juliai.ollama_connector;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *Object for parsing json response
 * @author kubaw
 */
public class JSONParser {

    final int debug = 1;

    JsonElement raw_object;
    String raw_data;
    JSONObject obj;

    public boolean error;

    /**
     * Constructor
     * @param raw_object
     */
    public JSONParser(JsonElement raw_object){
        this.raw_object = raw_object;
        this.raw_data = "";
        if ( raw_object == null){
            error = true;
        }
        else
            load();
    }

    /**
     * Function for getting string values
     * @param key
     * @return String
     */
    public String get_string(String key){
        return obj.getString(key);
    }

    /**
     * Function for getting int values
     * @param key
     * @return Integer
     */
    public int get_int(String key){
        return obj.getInt(key);
    }

    /**
     * Function for getting array
     * @param key
     * @return JSONArray
     */
    public JSONArray get_array(String key){
        return obj.getJSONArray(key);
    }

    /**
     * Function for ArrayList
     * @param key
     * @return ArrayList
     */
    public ArrayList get_arraylist(String key){
        ArrayList<String> data = new ArrayList<>();
        JSONArray array = obj.getJSONArray(key);
        if ( array != null){
            for(int i = 0; i<array.length(); i++){
                data.add(array.get(i).toString());
            }
        }
        return data;
    }

    /**
     * Function for getting raw object data
     * @return String
     */
    public String get_raw(){
        return raw_object.toString();
    }

    /**
     * Function for loading data from Json object
     */
    void load(){
        raw_data = raw_object.toString();
        obj = new JSONObject(raw_data);
    }

}