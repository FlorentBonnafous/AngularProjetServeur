package com.example.com.isis.adventureISIServer;

import generated.World;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		try {
                    JAXBContext cont = JAXBContext.newInstance(World.class);
                    Unmarshaller u = cont.createUnmarshaller();
                    World world = (World) u.unmarshal(new File(input));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
	}

}
