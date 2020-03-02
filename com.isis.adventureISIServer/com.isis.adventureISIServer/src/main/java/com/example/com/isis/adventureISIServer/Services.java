/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.com.isis.adventureISIServer;

import generated.World;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author psandre
 */
public class Services {
    
   public World readWorldFromXml(){
      InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
      return null;
       
   }
           
   public void saveWorldToXml(World world){
       try {
           OutputStream output = new FileOutputStream("newWorld.xml");
       } catch (FileNotFoundException ex) {
           Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
            
    
}
