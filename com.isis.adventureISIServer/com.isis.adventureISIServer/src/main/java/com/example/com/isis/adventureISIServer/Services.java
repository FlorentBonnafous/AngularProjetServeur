/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.com.isis.adventureISIServer;

import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author psandre
 */
public class Services {

    public World readWorldFromXml() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u = cont.createUnmarshaller();
            World world = (World) u.unmarshal(input);
            return world;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveWorldToXml(World world) {
        try {
            OutputStream output = new FileOutputStream("newWorld.xml");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public World getWorld(World World) { // Classe créée, à modifier ou à supprimer
   //     return saveWorldToXml(world); //faux
    }

}
