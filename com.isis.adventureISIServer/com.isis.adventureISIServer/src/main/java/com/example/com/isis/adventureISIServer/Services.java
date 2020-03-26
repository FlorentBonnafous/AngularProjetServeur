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
        //On r�cup�re le monde 
        World world = readWorldFromXml(username);
        //System.out.println("money avant majmonde : "+world.getMoney());
        //appel de notre mise � jour du monde
        System.out.println("M�j du monde de : "+username);
        majMonde(world);
        //System.out.println("money apr�s majmonde : "+world.getMoney());
        saveWorldToXml(world, username);
        return world;
    }

    public World majMonde(World world) {
        
        //On calcule le temps qui s'ecoule depuis la derni�re m�j du monde
        long timespend = System.currentTimeMillis() - world.getLastupdate();
        //On fait une boucle qui nous permet d'acc�der � tous les produits
        ProductsType ps = world.getProducts();
        for (ProductType p : ps.getProduct()) {
            //Si le joueur poss�de le produit
            if (p.getTimeleft() > 0) {
                //Si le temps restant pour l'achat du produit est �coul�
                if (p.getTimeleft() < timespend) {
                    //Si l'on a le manager :
                    System.out.println("Manager de " + p.getName() + " : " + p.isManagerUnlocked());
                    if (p.isManagerUnlocked()) {
                        //System.out.println("Oui oui uoi manager de : " + p.getName());
                        //On compte le nombre de produits total
                        int nbproduits = (int) ((timespend - p.getTimeleft()) / p.getVitesse());
                        //m�j de notre argent total
                        world.setMoney(arrondi(world.getMoney()) + (nbproduits) * arrondi(p.getRevenu()));
                        //m�j timeleft
                        p.setTimeleft(p.getVitesse() - timespend % p.getVitesse());
                        //System.out.println("money avec manaj : " + world.getMoney());
                        //Si l'on a pas le manageur :    
                    } else {
                        if (p.getTimeleft() != 0 && p.getTimeleft() < timespend) {
                            //ajout d'un seul produit
                            world.setMoney(arrondi(world.getMoney()) + p.getRevenu());
                            world.setScore(arrondi(world.getScore()) + p.getRevenu());
                        }
                        else{
                            //m�j de timeleft si la cr�ation du produit n'est pas termin�e
                            p.setTimeleft(p.getTimeleft() - timespend);
                        }                
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

    // prend en param�tre le pseudo du joueur et le produit
    // sur lequel une action a eu lieu (lancement manuel de production ou achat d�une certaine quantit� de produit)
    // renvoie false si l�action n�a pas pu �tre trait�e
    public Boolean updateProduct(String username, ProductType newproduct) throws JAXBException, IOException {
        //System.out.println("verif update product");
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le produit �quivalent � celui pass�// en param�tre
        ProductType product = findProductByID(world, newproduct.getId());
        if (product == null) {
            return false;
        }
        // calculer la variation de quantit�. Si elle est positive c'est
        // que le joueur a achet� une certaine quantit� de ce produit
        // sinon c�est qu�il s�agit d�un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            // soustraire del'argent du joueur le cout de la quantit�
            // achet�e et mettre � jour la quantit� de product
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
            //System.out.println("cout2 : " + cout2);
            //System.out.println("cout1 : " + cout1);
            //System.out.println("emissionCout : " + emissionCout);
            double finalmoney = 0;
            //System.out.println("money avant calcul : " + money);
            finalmoney = money - emissionCout;
            world.setMoney(arrondi(finalmoney));

            //System.out.println("money : " + finalmoney);
            product.setQuantite(newproduct.getQuantite());
            //System.out.println("qtit� : "+newproduct.getQuantite());
        } else {
            // initialiser product.timeleft � product.vitesse pour lancer la production
            product.setTimeleft(product.getVitesse());
            double money = world.getMoney();
            world.setMoney(arrondi(money) + arrondi(product.getRevenu()));

        }
        // sauvegarder les changements du monde
        saveWorldToXml(world, username);
        // System.out.println("money apres prod : "+world.getMoney());
        return true;
    }

    // prend en param�tre le pseudo du joueur et le manager achet�.
    // renvoie false si l�action n�a pas pu �tre trait�e
    public boolean updateManager(String username, PallierType newmanager) throws JAXBException, IOException {
        //System.out.println("verif update manager");
        // aller chercher le monde qui correspond au joueur   
        //System.out.println("Nom du joueur : "+username);
        World world = getWorld(username);
        // trouver dans ce monde, le manager �quivalent � celui pass� en param�tre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        // d�bloquer ce manager
        manager.setUnlocked(true);
        //System.out.println("Manager : " + manager.getName());
        //trouver le produit correspondant au manager
        ProductType product = findProductByID(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        // d�bloquer le manager de ce produit
        product.setManagerUnlocked(true);
        System.out.println("D�blocage du manager !!!! " + product.isManagerUnlocked());
        // soustraire de l'argent du joueur le cout du manager
        //System.out.println("apr�s avant manager : "+world.getMoney());
        world.setMoney(arrondi(world.getMoney()) - manager.getSeuil());
        //System.out.println("apr�s achat manager : "+world.getMoney());
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
