/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client.theater;

import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import java.util.HashMap;

/**
 *
 * @author kirio
 */
public class MyLoadConfig extends PagingLoadConfigBean {

    public MyLoadConfig() {
    }

    public MyLoadConfig(int offset, int limit) {
        super(offset, limit);
    }

    public String getFilter(String key){ return filters.get(key);}
    public void setFilter(String key,String value){  
        if(value.isEmpty())
            filters.remove(key);
        else {
            filters.put(key,value);
        }
    
    }
    public  HashMap<String, String> filters = new HashMap<String, String>();
    
}
