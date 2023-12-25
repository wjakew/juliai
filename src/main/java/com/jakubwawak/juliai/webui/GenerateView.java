/**
 * by Jakub Wawak
 * all rights reserved
 * kubawawak@gmail.com
 */
package com.jakubwawak.juliai.webui;

import com.jakubwawak.juliai.JuliaiApplication;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.Lumo;
import org.json.JSONObject;

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

    /**
     * generate_button action
     * @param ex
     */
    private void setGenerate_button(ClickEvent ex){
        String prompt = generate_area.getValue();
        if ( !prompt.isEmpty() ){
            generate_area.setReadOnly(true);
            generate_area.setTitle(generate_area.getValue());
            StringBuilder data = new StringBuilder();
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(JuliaiApplication.oacs.requestUrl+"/api/generate").openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                connection.getOutputStream().write(JuliaiApplication.oacs.prepareRequestPayload("llama2",prompt).getBytes());

                Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));

                boolean isFirst = true;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(line);
                        data.append(jsonObject.getString("response"));
                        generate_area.setValue(data.toString());
                        isFirst = false;
                    }
                    System.out.println(line);
                }
                if (!isFirst) {
                    System.out.println("-- End of JSON stream --");
                }
                generate_area.setReadOnly(false);
            }catch(Exception e){
                Notification.show(e.toString());
            }
        }
        else{
            Notification.show("Prompt is empty!");
        }
    }
}