/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kirio
 */
public class ComboBoxWithClearBut implements IsWidget{

    public ComboBoxWithClearBut() {
       this(new ArrayList<String>());
    }
    HBoxLayoutContainer cont ;
    public ComboBox<String> combo;
    InputElement ie_ell;
    
    public void onClear(){};
    public ComboBoxWithClearBut(List<String> items) {
 
                ListStore<String> store = new  ListStore<String>(new ModelKeyProvider<String>() {
                   @Override
                    public String getKey(String item) {
                         return item;
                    }
                });
                store.addAll(items);
            
              
                ComboBoxCell<String> cell = new ComboBoxCell<String>(store, new StringLabelProvider<String>())
                {
                     @Override
                     protected String selectByValue(String value) {
                          String item =  super.selectByValue(value);
                          if(item!=null)
                              return item;
                          return value;
                     }
                    
                 };
         
                 combo = new ComboBox<String>(cell){
 

                    @Override
                    protected void onAfterFirstAttach() {
                        super.onAfterFirstAttach();    
                        InputElement ie = this.getInputEl().cast();
                        ie_ell = ie;
                        ie.setMaxLength(30);
                    }
                     
                 
                 };
                 combo.setQueryDelay(1000);
        
                 combo.setTriggerAction(ComboBoxCell.TriggerAction.ALL);//disable annoying filter
                 cont = new HBoxLayoutContainer();
                  BoxLayoutContainer.BoxLayoutData flex = new BoxLayoutContainer.BoxLayoutData();
                  flex.setFlex(1);
                 cont.add(combo,flex);
                 TextButton but_clear =  new TextButton("",Resources.INSTANCE.cross_white());
                 but_clear.setToolTip("Очистить поле");
                 but_clear.setIconAlign(ButtonCell.IconAlign.RIGHT);
                 cont.add(but_clear);
                 
                 but_clear.addSelectHandler(new SelectEvent.SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                         /// Combobox clear bug worckaround!!!!   
                        clear();
                         
                         
                    }
                });
    }

    public void clear(){
         
         combo.clear(); 
             
         if(ie_ell!=null){
           
            ie_ell.focus();
        
            ie_ell.setValue(null);
   
         }
        combo.setValue(null, true, true) ;
     
        onClear();
    }
    @Override
    public Widget asWidget() {
         return cont;
    }
    
    public void   setCurrentValue(String val){  
        combo.setValue(val,true,true);
    }
    public String getCurrentValue(){return (combo.getCurrentValue()==null?"":combo.getCurrentValue().trim());}
     
}
