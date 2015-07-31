package org.kino.server.structs;

 

 

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kirio
 */
public interface DataSourceSettings {
 
 
       // Инициализируем переменные значениями из класса org.settings.PrivateData,
    // который в целях безопасности исключен из контроля версий
    
    public static final DataSourceHelper dataSource = new DataSourceHelper(PrivateData.db_host, PrivateData.db_port,PrivateData.db_base, PrivateData.db_user, PrivateData.db_password);





}

