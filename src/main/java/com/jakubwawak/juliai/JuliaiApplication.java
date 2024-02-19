/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.juliai;

import com.jakubwawak.juliai.maintanance.Configuration;
import com.jakubwawak.juliai.ollama_connector.OllamaAPIConnectorASync;
import com.jakubwawak.juliai.ollama_connector.OllamaAPIConnectorSync;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main app class
 */
@SpringBootApplication
@EnableVaadin({"com.jakubwawak"})
public class JuliaiApplication {

	public static String version = "v1.0.0";
	public static String build = "jai120124REV01";
	public static int debug = 0;
	public static Configuration configuration;
	public static OllamaAPIConnectorSync oacs;
	public static OllamaAPIConnectorASync oacas;

	/**
	 * Main app function
	 * @param args
	 */
	public static void main(String[] args) {
		showHeader();
		configuration = new Configuration();
		if  (debug == 1){
			// start tests...
			JuliaiTest jut = new JuliaiTest();
		}
		else{
			oacs = new OllamaAPIConnectorSync(configuration.requestURL);
			oacas = new OllamaAPIConnectorASync(oacs);
			// run web UI
			SpringApplication.run(JuliaiApplication.class, args);
		}

	}

	/**
	 * Function for showing header data
	 */
	public static void showHeader(){
		String header = "     _ _   _ _     ___      _ \n" +
				"    | | | | | |   |_ _|__ _(_)\n" +
				" _  | | | | | |    | |/ _` | |\n" +
				"| |_| | |_| | |___ | | (_| | |\n" +
				" \\___/ \\___/|_____|___\\__,_|_|\n";

		header = header + version+"/"+build;
		System.out.println(header);
	}

}
