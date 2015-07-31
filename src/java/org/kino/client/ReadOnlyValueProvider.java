/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client;

import com.sencha.gxt.core.client.ValueProvider;

/**
 *
 * @author kirio
 */
abstract public class ReadOnlyValueProvider<T, V> implements ValueProvider<T, V>{

    String path;
    public ReadOnlyValueProvider(String path) {
        this.path = path;
    }
 
    @Override
    public void setValue(T object, V value) {
    }

    @Override
    public String getPath() {
       return path;
    }
    
}
