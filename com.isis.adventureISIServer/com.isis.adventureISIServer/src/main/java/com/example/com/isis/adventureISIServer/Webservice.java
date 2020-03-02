/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.com.isis.adventureISIServer;

import static javax.swing.text.html.FormSubmitEvent.MethodType.GET;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ejaffre
 */
public class Webservice {
    
    Services services;
    
    public Webservice() {
        services = new Services();
    }
    
    @GET
    @Path("world")
    @Produces(MediaType.APPLICATION_XML)
    public Response getWorld(){
        return Response.ok(services.getWorld()).build();
    }
}
