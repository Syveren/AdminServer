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
public class CreateContactDialog extends Dialog{

    TheaterItem.ContactData data;
 
    boolean validateField(TextField f){
        boolean res = f.getValue()!=null;
        if(!res){
            f.focus();
        }
        return res;
    }
    
    
    
    public CreateContactDialog(final int client_id) {
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
        setHeadingText("Добавить контакт");
        setPredefinedButtons(PredefinedButton.CANCEL,PredefinedButton.YES);
        final TextField field_post = new TextField();
        final TextField field_fio = new TextField();
        final TextField field_mail = new TextField();
        final TextField field_phone = new TextField();
        
 
        vert.add(new FieldLabel(field_post,"Должность"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(new FieldLabel(field_fio,"ФИО"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(new FieldLabel(field_mail,"e-mail"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(new FieldLabel(field_phone,"Телефон"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        
        getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                 data = null;
                 hide();
            }
        });
        getButton(PredefinedButton.YES).addSelectHandler(new SelectEvent.SelectHandler() {

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
 
            
              
                 final TheaterItem.ContactData contact = new TheaterItem.ContactData(0,field_fio.getValue(), field_post.getValue(), field_phone.getValue(), field_mail.getValue());
                 CallbackWithFailureDialog<Integer> callback = new CallbackWithFailureDialog<Integer>("Не удалось создать контакт") {

                     @Override
                     public void onFailure(Throwable caught) {
                         super.onFailure(caught); //To change body of generated methods, choose Tools | Templates.
                     }
                     
                     @Override
                     public void onSuccess(Integer result) {
                         data = contact;
                         data.id = result;
                         hide();
                     }
                 };
                 GWTServiceAsync.instance.addNewContact(client_id, contact, callback);
            }
        });
    }
    
    
}
