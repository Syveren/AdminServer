/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.structs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author kirio
 */

public class DataSourceHelper {
    

     final String host ;
  // static final String host = "136.243.0.230";
     final Integer port ;
     final String base ;
     final String user ;
     final String password ;
     
     
     
     
     static boolean  initDriver(){
         try {
            Class.forName("org.postgresql.Driver");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
     }
     static boolean driver_init = initDriver();

   

    public DataSourceHelper(String host, Integer port, String base, String user, String password) {
        this.host = host;
        this.port = port;
        this.base = base;
        this.user = user;
        this.password = password;
    }
     
     
     
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+base, user, password);
      }
}