/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author marti
 */
public class JpaUtil {
    private final static EntityManagerFactory emf= Persistence.createEntityManagerFactory("com.fpune_portal_jar_0.0.1-SNAPSHOTPU");

    private JpaUtil() {
    }
    
    public static EntityManagerFactory getEntityManagerFactory(){
        return emf;
    }
    
    
}
