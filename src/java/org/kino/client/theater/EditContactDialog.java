/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.theater;

import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.rpc.GWTServiceAsync;

/**
 *
 * @author kirio
 */
public class EditContactDialog extends Dialog{

    
 
    boolean validateField(TextField f){
        boolean res = f.getValue()!=null;
        if(!res){
            f.focus();
        }
        return res;
    }
    
   boolean edited = false;
    
    public EditContactDialog(final TheaterItem.ContactData dataForUpdate) {
        setModal(true);
        setSize("400px", "200px");
        setBodyStyle("backgroundColor:white");
        final VerticalLayoutContainer vert = new VerticalLayoutContainer();
      
        ContentPanel bg = new ContentPanel();
        bg.setHeaderVisible(false);
        setWidget(bg);
            
      
        vert.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        vert.setAdjustForScroll(true);
        bg.add(vert,new MarginData(10, 0, 0, 10));   
        setHeadingText("Редактировать контакт");
        setPredefinedButtons(Dialog.PredefinedButton.CANCEL,Dialog.PredefinedButton.YES);
        final TextField field_post = new TextField();
        final TextField field_fio = new TextField();
        final TextField field_mail = new TextField();
        final TextField field_phone = new TextField();
        
        field_post.setValue(dataForUpdate.post);
        field_fio.setValue(dataForUpdate.fio);
        field_mail.setValue(dataForUpdate.email);
        field_phone.setValue(dataForUpdate.phone);
 
        vert.add(new FieldLabel(field_post,"Должность"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(new FieldLabel(field_fio,"ФИО"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(new FieldLabel(field_mail,"e-mail"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(new FieldLabel(field_phone,"Телефон"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        
        getButton(Dialog.PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                 edited = false;
                 hide();
            }
        });
        getButton(Dialog.PredefinedButton.YES).addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if(!validateField(field_post) 
                        || !validateField(field_fio) 
                        || !validateField(field_mail)
                        || !validateField(field_phone))
                {
                        Info.display("Внимание","<font color='red'>Не все поля заполнены</font>");
                        return;
                }
 
            
              
                 
               final  TheaterItem.ContactData newValueClone = new TheaterItem.ContactData(dataForUpdate.id, 
                        field_fio.getCurrentValue(), 
                        field_post.getCurrentValue(), 
                        field_phone.getCurrentValue(), 
                        field_mail.getCurrentValue());
                        
                                 
             
                CallbackWithFailureDialog<Void> callback = new CallbackWithFailureDialog<Void>("Не удалось изменить данные") {

                     @Override
                     public void onFailure(Throwable caught) {
                         super.onFailure(caught); //To change body of generated methods, choose Tools | Templates.
                     }
                     
                     @Override
                     public void onSuccess(Void result) {
                         
                         dataForUpdate.fio = newValueClone.fio;
                         dataForUpdate.post = newValueClone.post;
                         dataForUpdate.email = newValueClone.email;
                         dataForUpdate.phone = newValueClone.phone;
  
                         edited = true;
                         hide();
                     }
                 };
                 GWTServiceAsync.instance.updateContact(newValueClone, callback);
            }
        });
    }
    
    
}
