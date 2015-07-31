/*
 * To change this license header, choose License Headers in CommonProjectInfo Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.monitoring;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import org.kino.client.broadcast.NewSendW;

/**
 *
 * @author kirio
 */
public class MonitoringW implements IsWidget{
   static public final int TIMER_REFRESH_TIMEOUT = 3000;
    TabPanel tab = new TabPanel();
    
    @Override 
    public Widget asWidget() {
        return tab;
        //return transerGrid.asWidget();
    }
    ComonProjectGrid transerGrid = new ComonProjectGrid();
    CommonErrorGrid errorGrid = new CommonErrorGrid();
    public MonitoringW() {
         
        
         
         tab.add(transerGrid.asWidget(), "Передача");
         tab.add(errorGrid.asWidget(), "Ошибка");
         
         tab.addSelectionHandler(new SelectionHandler<Widget>() {

             @Override
             public void onSelection(SelectionEvent<Widget> event) {
           
                  if(event.getSelectedItem()==transerGrid.asWidget()){
                       errorGrid.onVisibileChanged(false);
                     transerGrid.onVisibileChanged(true);
                  }
                 else if(event.getSelectedItem()==errorGrid.asWidget()){
                      transerGrid.onVisibileChanged(false);
                     errorGrid.onVisibileChanged(true);
                 
                 }
                     
             }
         });
         
         tab.addShowHandler(new ShowEvent.ShowHandler() {

             @Override
             public void onShow(ShowEvent event) {
                if(!tab.isAttached())
                     return;
                System.out.println("show");
                 if(tab.getActiveWidget()==transerGrid.asWidget())
                     transerGrid.onVisibileChanged(true);
                 else if(tab.getActiveWidget()==errorGrid.asWidget())
                     errorGrid.onVisibileChanged(true);
             }
         });
         tab.addHideHandler(new HideEvent.HideHandler() {
             @Override
             public void onHide(HideEvent event) {
                 if(!tab.isAttached())
                     return;
                 transerGrid.onVisibileChanged(false);
                 errorGrid.onVisibileChanged(false);
                System.out.println("hideeeeeeee");
             }
         });
         
         
        
    }
    void projectAdd(NewSendW.NewProject prog){
        //tab.setActiveWidget(transerGrid);
    }
    
    static public   class CommonErrorInfo implements IsSerializable{
         public int id;
         public String rus;
         public String eng;
         public String folder;
         public int client_count;
         public String error;
         public int type;
         
         static final int type_init = 0;
         static final int type_client = 1;

        public CommonErrorInfo() {
        }
        
        

        public CommonErrorInfo(int id, String rus,String eng,String folder, int client_count, String error) {
            this.id = id;
            this.rus = rus;
            this.eng = eng;
            this.folder = folder;
            this.client_count = client_count;
            this.error = error;
        }
         
    }
  
    static public   class CommonProjectInfo implements IsSerializable{
        public int id;
        public String name;
        public String rus;
        public double percent;
        public int client_count;
        public long time_left;
        public long time_create;
        public String error;
        public String status;
        public CommonProjectInfo() {
        }

        /*public CommonProjectInfo(int id, String name, int client_count, String error) {
            this.id = id;
            this.name = name;
            this.client_count = client_count;
            this.error = error;
        }

        public CommonProjectInfo(int id, String name, String error, String status) {
            this.id = id;
            this.name = name;
            this.error = error;
            this.status = status;
        }*/

        

        public CommonProjectInfo(int id, String name,String rus, double percent, int client_count, long time_left, long time_create, String status) {
            this.id = id;
            this.name = name;
            this.rus = rus;
            this.percent = percent;
            this.client_count = client_count;
            this.time_left = time_left;
            this.time_create = time_create;
            
            this.status = status;
        }
        
        
    }
    

   
}
