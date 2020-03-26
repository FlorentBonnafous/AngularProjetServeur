/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.com.isis.adventureISIServer;

import generated.ProductType;
import generated.World;
import generated.PallierType;
import generated.ProductsType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Flore
 */
public class Services {

    InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
    //int compteur = 0;

    //int premiercoup = 0;
    //Nous retourne le monde en fonction du nom d'utilisateur
    public World readWorldFromXml(String username) throws JAXBException {
        try {
            File file = new File(username + "-world.xml");
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u = cont.createUnmarshaller();
            World world = (World) u.unmarshal(file);
            return world;
        } catch (Exception e) {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u = cont.createUnmarshaller();
            World world = (World) u.unmarshal(input);

            return world;
        }

    }

    //On sauve le monde en cours avec son nom d'utilisateur
    public void saveWorldToXml(World world, String username) throws FileNotFoundException, JAXBException, IOException {

        OutputStream output = new FileOutputStream(username + "-" + "world.xml");
        JAXBContext cont = JAXBContext.newInstance(World.class);
        Marshaller m = cont.createMarshaller();
        m.marshal(world, output);
        output.close();
    }

    World getWorld(String username) throws JAXBException, IOException {
        //On récupère le monde 
        World world = readWorldFromXml(username);
        //System.out.println("money avant majmonde : "+world.getMoney());
        //appel de notre mise à jour du monde
        //System.out.println("Màj du monde de : "+username);
        majMonde(world);
        //System.out.println("money après majmonde : "+world.getMoney());
        saveWorldToXml(world, username);
        return world;
    }

    public World majMonde(World world) {
        //On calcule le temps qui s'ecoule depuis la dernière màj du monde
       // System.out.println("Argent du joueur  : " + world.getMoney());
        long timespend = System.currentTimeMillis() - world.getLastupdate();
        //On fait une boucle qui nous permet d'accéder à tous les produits
        ProductsType ps = world.getProducts();
        for (ProductType p : ps.getProduct()) {
            System.out.println("timeleft : "+p.getTimeleft());
            //Si le joueur possède le produit
            if (p.getTimeleft() > 0) {
                //Si l'on a le manager :
                System.out.println(" maj monde : Manager de " + p.getName() + " : " + p.isManagerUnlocked());
                if (p.isManagerUnlocked()) {
                    //On compte le nombre de produits total
                    int nbproduits = (int) ((timespend - p.getTimeleft()) / p.getVitesse());
                    //màj de notre argent total
                    world.setMoney(arrondi(world.getMoney()) + (nbproduits) * arrondi(p.getRevenu()));
                    //màj timeleft
                    p.setTimeleft(p.getVitesse() - timespend % p.getVitesse());
                    System.out.println("money après calcul : " + world.getMoney());
                    //Si l'on a pas le manageur :    
                } else {
                    if (p.getTimeleft() != 0 && p.getTimeleft() < timespend) {
                        //System.out.println("timespend : "+timespend);
                        //ajout d'un seul produit
                        world.setMoney(arrondi(world.getMoney()) + p.getRevenu());
                        p.setTimeleft(0);
                    } else {
                        //màj de timeleft si la création du produit n'est pas terminée
                        p.setTimeleft(p.getTimeleft() - timespend);
                    }
                }

            }
            //repositionnement du lastUpdate sur l'instant courant
            world.setLastupdate(System.currentTimeMillis());
        }
        return world;
    }

    public ProductType findProductByID(World world, int id) {
        ProductType pt = null;
        for (ProductType a : world.getProducts().getProduct()) {
            if (id == a.getId()) {
                pt = a;
            }
        }
        return pt;
    }

    public PallierType findManagerByName(World world, String name) {
        PallierType manager = null;
        for (PallierType a : world.getManagers().getPallier()) {
            if (name.equals(a.getName())) {
                manager = a;
            }
        }
        return manager;
    }

    // prend en paramètre le pseudo du joueur et le produit
    // sur lequel une action a eu lieu (lancement manuel de production ou achat d’une certaine quantité de produit)
    // renvoie false si l’action n’a pas pu être traitée
    public Boolean updateProduct(String username, ProductType newproduct) throws JAXBException, IOException {
        //System.out.println("verif update product");
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le produit équivalent à celui passé// en paramètre
        System.out.println("money update product : "+world.getMoney());
        ProductType product = findProductByID(world, newproduct.getId());
        if (product == null) {
            System.out.println("pas produit");
            return false;
        }
        System.out.println(" Update product: Manager de " + product.getName() + " : " + product.isManagerUnlocked());
        // calculer la variation de quantité. Si elle est positive c'est
        // que le joueur a acheté une certaine quantité de ce produit
        // sinon c’est qu’il s’agit d’un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            // soustraire del'argent du joueur le cout de la quantité
            // achetée et mettre à jour la quantité de product
            double croiss = product.getCroissance();
            double cost = product.getCout();
            double money = world.getMoney();
            double cout1 = 0;
            for (int i = 0; i < product.getQuantite(); i++) {
                cout1 = (double) (cout1 + cost * Math.pow(croiss, i));
            }
            double cout2 = 0;
            cout2 = cout1;
            for (int y = product.getQuantite(); y < qtchange + product.getQuantite(); y++) {
                cout2 = (double) (cout2 + cost * Math.pow(croiss, y));
            }
            double emissionCout = 0;
            emissionCout = cout2 - cout1;
            double finalmoney = 0;
            finalmoney = money - emissionCout;
            world.setMoney(arrondi(finalmoney));

            //System.out.println("money : " + finalmoney);
            product.setQuantite(newproduct.getQuantite());
            //System.out.println("qtité : "+newproduct.getQuantite());
        } else {
            // initialiser product.timeleft à product.vitesse pour lancer la production
            product.setTimeleft(product.getVitesse());
        }
        // sauvegarder les changements du monde
        saveWorldToXml(world, username);
        // System.out.println("money apres prod : "+world.getMoney());
        return true;
    }

    // prend en paramètre le pseudo du joueur et le manager acheté.
    // renvoie false si l’action n’a pas pu être traitée
    public boolean updateManager(String username, PallierType newmanager) throws JAXBException, IOException {
        // aller chercher le monde qui correspond au joueur 
        System.out.println("Achat d un manager");
        World world = getWorld(username);
        // trouver dans ce monde, le manager équivalent à celui passé en paramètre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        // débloquer ce manager
        manager.setUnlocked(true);
        //trouver le produit correspondant au manager
        ProductType product = findProductByID(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        // débloquer le manager de ce produit
        product.setManagerUnlocked(true);
        product.setTimeleft(product.getVitesse());
        //System.out.println("Déblocage du manager !!!! " + product.isManagerUnlocked());
        // soustraire de l'argent du joueur le cout du manager
        world.setMoney(arrondi(world.getMoney()) - manager.getSeuil());
        // sauvegarder les changements au monde
        saveWorldToXml(world, username);
        return true;
    }

    double arrondi(double nombre) {
        if (nombre < 1000) {
            nombre = (double) Math.round(nombre * 100) / 100;
        } else {
            nombre = (double) Math.round(nombre);
        }
        return nombre;
    }

}
