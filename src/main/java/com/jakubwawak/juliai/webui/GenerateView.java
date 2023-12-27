/**
 * by Jakub Wawak
 * all rights reserved
 * kubawawak@gmail.com
 */
package com.jakubwawak.juliai.webui;

import com.jakubwawak.juliai.JuliaiApplication;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.compress.archivers.sevenz.CLI;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Object for showing welcome view
 */
@PageTitle("juliai by Jakub Wawak")
@Route(value = "/")
@RouteAlias(value = "/generate")
public class GenerateView extends VerticalLayout {

    TextArea generate_area;
    Button generate_button;

    VerticalLayout generate_layout;

    /**
     * Constructor
     */
    public GenerateView(){
        this.getElement().setAttribute("theme", Lumo.DARK);
        prepare_view();

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("--lumo-font-family","Monospace");
    }

    /**
     * Function for preparing components
     */
    void prepare_components(){

        generate_layout = new VerticalLayout();
        generate_layout.setSizeFull();
        generate_layout.setJustifyContentMode(JustifyContentMode.CENTER);
        generate_layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        generate_layout.getStyle().set("text-align", "center");
        generate_layout.getStyle().set("--lumo-font-family","Monospace");
        generate_layout.getStyle().set("background-image","radial-gradient("+JuliaiApplication.configuration.uiColor1+","+JuliaiApplication.configuration.uiColor2+")");
        generate_layout.getStyle().set("border-radius","25px");
        generate_layout.setWidth("50%"); generate_layout.setHeight("50%");

        generate_area = new TextArea("Ask me anything...");
        generate_area.setSizeFull();
        generate_area.setPlaceholder("type your prompt here");
        generate_button = new Button("Generate!", VaadinIcon.ACADEMY_CAP.create(),this::setGenerate_button);
        generate_button.setWidth("50%");
        generate_button.addThemeVariants(ButtonVariant.LUMO_CONTRAST,ButtonVariant.LUMO_PRIMARY);

        generate_layout.add(generate_area);
    }

    /**
     * Function for preparing view and components
     */
    void prepare_view(){
        prepare_components();
        add(new H6("juliai"));
        add(generate_layout);
        add(generate_button);
    }

    //--button action functions
    @Async
    private void setGenerate_button(ClickEvent ex){
        String prompt = generate_area.getValue();// Lock the Vaadin session for thread-safe access
        UI ui = UI.getCurrent();
        if (!prompt.isEmpty()) {
            generate_area.setValue(prompt); // Set the initial prompt text

            // Create a separate thread to update the TextArea's content
            Thread updateThread = new Thread(() -> {
                StringBuilder data = new StringBuilder();

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(JuliaiApplication.oacs.requestUrl + "/api/generate").openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    connection.getOutputStream().write(JuliaiApplication.oacs.prepareRequestPayload("llama2", prompt).getBytes());

                    Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        JSONObject jsonObject = new JSONObject(line);
                        data.append(jsonObject.getString("response")); // Accumulate the response in the string builder

                        // Update the text area asynchronously on the main UI thre
                        ui.access(()->{
                            generate_area.setValue(data.toString());
                        });

                        System.out.println(data.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            try{
                VaadinSession.getCurrent().lock();
                updateThread.start();
            }finally {
                // Lock the Vaadin session for thread-safe access
                VaadinSession.getCurrent().unlock();
            }
        } else {
            Notification.show("Prompt is empty!");
        }
    }

    private void setGenerate_button2(ClickEvent ex){
        String prompt = generate_area.getValue();// Lock the Vaadin session for thread-safe access
        if (!prompt.isEmpty()) {
            StringBuilder data = new StringBuilder();
            generate_area.setValue(data.toString());
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(JuliaiApplication.oacs.requestUrl + "/api/generate").openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                connection.getOutputStream().write(JuliaiApplication.oacs.prepareRequestPayload("llama2", prompt).getBytes());

                Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    JSONObject jsonObject = new JSONObject(line);
                    data.append(jsonObject.getString("response")); // Accumulate the response in the string builder
                    generate_area.setValue(data.toString());
                    System.out.println(data.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Notification.show("Prompt is empty!");
        }
    }
}