/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.juliai.ollama_connector;

import com.jakubwawak.juliai.webui.GenerateView;
import com.vaadin.flow.component.notification.Notification;
import okhttp3.FormBody;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.Stream;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.Response;

/**
 * Object for connecting to Ollama API - async version
 */
public class OllamaAPIConnectorASync {

    OllamaAPIConnectorSync apc;

    /**
     * Constructor
     * @param apc
     */
    public OllamaAPIConnectorASync(OllamaAPIConnectorSync apc){
        this.apc = apc;
        apc.streamFlag = 1;
    }

    /**
     * Function for sending POST request to Ollama API and getting async answer
     * @param model
     * @param prompt
     * @return String
     */
    public String askOllama(String model,String prompt){
        StringBuilder data = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apc.requestUrl+"/api/generate").openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            connection.getOutputStream().write(apc.prepareRequestPayload(model,prompt).getBytes());

            Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));

            boolean isFirst = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(line);
                    data.append(jsonObject.getString("response"));
                    isFirst = false;
                }
                System.out.println(line);
            }
            if (!isFirst) {
                System.out.println("-- End of JSON stream --");
            }
            return data.toString();
        }catch(Exception ex){
            System.out.println(ex.toString());
            return null;
        }
    }
}
