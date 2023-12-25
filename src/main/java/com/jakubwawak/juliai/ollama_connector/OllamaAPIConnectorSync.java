/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.juliai.ollama_connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.config.RequestConfig;

import java.util.ArrayList;

/**
 * Object for generating requests to Ollama API - sync version
 */
public class OllamaAPIConnectorSync {

    public ArrayList<String> apiConnectorRequestHistory; // collection for storing request history
    public int streamFlag;

    String payload="{\"model\":\">\",\"prompt\":\"<\",\"stream\":?}";
    public String requestUrl;

    /**
     * Constructor
     */
    public OllamaAPIConnectorSync(String requestUrl){
        this.requestUrl = requestUrl;
        apiConnectorRequestHistory = new ArrayList<>();
    }

    /**
     * Main function for loading Ollama response
     * @param model
     * @param prompt
     * @param stream
     * @return Parser
     */
    public JSONParser askOllama(String model, String prompt, boolean stream){
        try{
            if ( stream )
                streamFlag = 1;
            else
                streamFlag = 0;

            JsonElement response = commit(prepareRequestPayload(model,prompt));
            return new JSONParser(response);
        }catch(Exception ex){
            System.out.println(ex.toString());
            return null;
        }
    }

    /**
     * Function for loading list of models
     * @return Parser
     */
    public JSONParser getModels(){
        try{
            HttpResponse<JsonNode> response = response_creator(requestUrl+"/api/tags");
            System.out.println("Trying to commit url:"+requestUrl+"/api/tags");
            return new JSONParser(parse_response(response));
        }catch(Exception ex){
            System.out.println(ex.toString());
            return null;
        }
    }

    /**
     * Function for creating response
     * @param url
     * @return String
     * @throws UnirestException
     */
    HttpResponse<JsonNode> response_creator(String url) throws UnirestException{
        try{
            System.out.println("Creating response ("+url_builder(url)+")");
            return Unirest.get(url_builder(url)).asJson();
        }catch(UnirestException e){
            System.out.println("Failed to create response ("+e.toString());
            return null;
        }
    }

    /**
     * Function for resetting
     * @param url
     * @return String
     */
    public String url_builder(String url){
        //return "http://"+server_ip+":8080"+url;
        return url;
    }

    /**
     * Function for preparing prompt data
     * @param model
     * @param prompt
     * @return
     */
    public String prepareRequestPayload(String model, String prompt){
        String payload = this.payload;
        payload = payload.replace(">",model);
        payload = payload.replace("<",prompt);

        if ( streamFlag == 1){
            payload = payload.replace("?","true");
        }
        else{
            payload = payload.replace("?","false");
        }
        return payload;
    }

    /**
     * Function for sending post requests
     * @param payload
     * @return
     */
    /**
     * Function for preparing raw JSON
     * @return JsonElement
     * @throws UnirestException
     */
    JsonElement commit(String payload) {
        try{
        // Create a new Unirest instance
        Unirest unirest = new Unirest();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();

        // Create a POST request with payload
        HttpResponse<JsonNode> response = unirest.post(requestUrl+"/api/generate")
                .header("Content-Type", "application/json")
                .body(payload)
                .asJson();
        System.out.println("Trying to commit to: " + requestUrl + "with payload: "+payload);
        JSONParser JSONParser = new JSONParser(parse_response(response));

            if ( JSONParser == null ){
                System.out.println("Connection error...");
                return null;
            }
            else{
                apiConnectorRequestHistory.add(new JSONParser(parse_response(response)).raw_data);
                return parse_response(response);
            }
        }catch(Exception e){
            System.out.println("CONNECTOR ERROR: "+e.toString());
            return null;
        }
    }

    /**
     * Function for parasing response for data
     * @param data
     * @return JsonElement
     */
    JsonElement parse_response(HttpResponse<JsonNode> data){
        System.out.println("Parasing response for data");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        return jp.parse(data.getBody().toString());
    }
}
