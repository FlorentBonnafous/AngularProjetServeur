/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.com.isis.adventureISIServer;

import javax.ws.rs.ApplicationPath;
import org.springframework.stereotype.Component;

/**
 *
 * @author ejaffre
 */

@Component
@ApplicationPath("/adventureisis")
public class JerseyConfig extends RessourceConfig{
    
    public JerseyConfig()  {
        
        register(Webservice.class);
        
    }
}
