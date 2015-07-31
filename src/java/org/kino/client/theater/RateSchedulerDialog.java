/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.theater;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;
 

/**
 *
 * @author kirio
 */
public final class RateSchedulerDialog extends  Window{

    CardLayoutContainer card = new CardLayoutContainer();
     CenterLayoutContainer center = new CenterLayoutContainer();
    ComboBox<Day> comboDay;
    Grid<SpeedLimit> grid;
    public RateSchedulerDialog(final String client_uniq_ident) {
       
       
     
        setBodyStyle("backgroundColor:white;");
        center.add(new Image(Resources.INSTANCE.loading_bar()));
        card.add(center);
        setWidget(card);
        
          final ListStore<SpeedLimit> store = new ListStore<SpeedLimit>(new ModelKeyProvider<SpeedLimit>() {

                @Override
                public String getKey(SpeedLimit item) {
                    return String.valueOf(item.day+"-"+item.hour);
                }
            });
           ColumnConfig<SpeedLimit,String> col_hour = new ColumnConfig<SpeedLimit,String>(new ValueProvider<SpeedLimit,String>() {

                @Override
                public String getValue(SpeedLimit object) {
                     return object.hour+":00"+" - "+(object.hour+1)+":00";
                }

                @Override
                public void setValue(SpeedLimit o, String value) {
                    
                }

                @Override
                public String getPath() {
                    return "name";
                }
            },150,"Время");
 
               ColumnConfig<SpeedLimit,String> col_download = new ColumnConfig<SpeedLimit,String>(new ValueProvider<SpeedLimit,String>() {

                @Override
                public String getValue(SpeedLimit object) {
                     
                     return object.download<0?"-":String.valueOf(object.download)+" Кб/с";
                }

                @Override
                public void setValue(SpeedLimit object, String value) {
                    
                }

                @Override
                public String getPath() {
                    return "download";
                }
            },150,"Загрузка"); 
         
      
              ColumnConfig<SpeedLimit,String> col_upload = new ColumnConfig<SpeedLimit,String>(new ValueProvider<SpeedLimit,String>() {

                @Override
                public String getValue(SpeedLimit object) {
                     return object.upload<0?"-":String.valueOf(object.upload)+" Кб/с";
                }

                @Override
                public void setValue(SpeedLimit object, String value) {
                    
                }

                @Override
                public String getPath() {
                    return "upload";
                }
            },150,"Отдача"); 
               
              
             
               
               
              
         
               
        List<ColumnConfig<SpeedLimit,?>> list = new ArrayList<ColumnConfig<SpeedLimit,?>>();
 
        list.add(col_hour);
        list.add(col_download);
        list.add(col_upload);
        col_hour.setSortable(false);
        col_download.setSortable(false);
        col_upload.setSortable(false);
        col_hour.setMenuDisabled(true);
        col_upload.setMenuDisabled(true);
        col_download.setMenuDisabled(true);
             
        grid = new Grid<SpeedLimit>(store, new ColumnModel<SpeedLimit>(list));
 
        ListStore<Day> view_store =new ListStore<Day>(new ModelKeyProvider<Day>() {

             @Override
             public String getKey(Day item) {
                 return item.ordinal()+"";
             }
         });
    
    
            view_store.addAll(Arrays.asList(Day.values()));
        
         comboDay = new ComboBox<Day>(view_store,new LabelProvider<Day>() {
                @Override
                public String getLabel(Day item) {
                     return item.getText();
                }
             });
        
        comboDay.addSelectionHandler(new SelectionHandler<Day>() {

             @Override
             public void onSelection(SelectionEvent<Day> event) {
                
                 store.replaceAll(mapSchedulerRate.get(comboDay.getCurrentValue()));
             }

             
         });
 
        comboDay.setTriggerAction(ComboBoxCell.TriggerAction.ALL);
     
        
        final TextButton but_download =  new TextButton("Задать ограничение загрузки");
        final TextButton but_upload =  new TextButton("Задать ограничение отдачи");  
        final TextButton but_apply =  new TextButton("Применить");  
 
        but_apply.setEnabled(false);
        but_download.setEnabled(false);
        but_upload.setEnabled(false);
        
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<SpeedLimit>() {

             @Override
             public void onSelectionChanged(SelectionChangedEvent<SpeedLimit> event) {
                 but_download.setEnabled(!event.getSelection().isEmpty());
                 but_upload.setEnabled(!event.getSelection().isEmpty());
             }
         });
        
        
       
        
        SelectEvent.SelectHandler butclickHandler= new SelectEvent.SelectHandler() {

             @Override
             public void onSelect(SelectEvent event) {
                  
                 final List<SpeedLimit> selectedItems = grid.getSelectionModel().getSelectedItems();
                 if(selectedItems.isEmpty())
                     return;
                
                 final int type = event.getSource()==but_download?  DownloadUploadLimitDialog.type_download: DownloadUploadLimitDialog.type_upload;
                 
                 final DownloadUploadLimitDialog dlg =  new  DownloadUploadLimitDialog(type) ;
                 dlg.show();
                 dlg.addHideHandler(new HideEvent.HideHandler() {

                     @Override
                     public void onHide(HideEvent hevent) {
                         
                        if(dlg.value<0)
                             return;
                        for(SpeedLimit data:selectedItems)
                        {
                            if(type== DownloadUploadLimitDialog.type_download)
                                data.download = dlg.value;
                            else 
                                data.upload =  dlg.value;

                        }
                        store.fireEvent(new StoreUpdateEvent<SpeedLimit>(selectedItems));
                        
                        but_apply.setEnabled(smthChanged());
                        
                        
                     }
                 });
                 
                 
                 
                 
                 
             }

              
         };
        
        but_apply.addSelectHandler(new SelectEvent.SelectHandler() {

             @Override
             public void onSelect(SelectEvent event) {
                  setEnabled(false);
                  GWTServiceAsync.instance.setRateLimitData(client_uniq_ident,mapSchedulerRate,
                          new    CallbackWithFailureDialog <Void>("Не удалось применить мзменения") {

                      @Override
                      public void onFailure(Throwable caught) {
                         super.onFailure(caught);
                         setEnabled(true);
                            deepCopyMap(false);
                          but_apply.setEnabled(false);
                          store.replaceAll(mapSchedulerRate.get(comboDay.getCurrentValue()));
                      }

                      @Override
                      public void onSuccess(Void result) {
                          setEnabled(true);
                              deepCopyMap(true);
                              but_apply.setEnabled(false);
                      }
                  });
                         
  
          
                
                 
             }
         });
        but_download.addSelectHandler(butclickHandler);
        but_upload.addSelectHandler(butclickHandler);
        
 
        
         setHeadingText("Расписание ограничения загрузки/отдачи");
         VerticalLayoutContainer vert = new VerticalLayoutContainer();
         vert.add(new FieldLabel(comboDay, "День недели"), new VerticalLayoutContainer.VerticalLayoutData(1,-1));
         HBoxLayoutContainer hor_top = new HBoxLayoutContainer();
         hor_top.setEnableOverflow(false);
      
         hor_top.add(but_download,new BoxLayoutContainer.BoxLayoutData(new Margins(5,2,2,0)));
         hor_top.add(but_upload,new BoxLayoutContainer.BoxLayoutData(new Margins(5,2,2,2)));
     
        
        vert.add(hor_top, new VerticalLayoutContainer.VerticalLayoutData(1,-1));
        vert.add(grid, new VerticalLayoutContainer.VerticalLayoutData(1,1));
        
        HBoxLayoutContainer hor_bot = new HBoxLayoutContainer();
        vert.add(hor_bot, new VerticalLayoutContainer.VerticalLayoutData(1,-1));
        
        TextButton but_cancel = new TextButton("Отмена");
        hor_bot.setPack(BoxLayoutContainer.BoxLayoutPack.END);
        hor_bot.add(but_cancel,new BoxLayoutContainer.BoxLayoutData(new Margins(2,10,2,0)));
        hor_bot.add(but_apply,new BoxLayoutContainer.BoxLayoutData(new Margins(2,0,2,0)));
        
        but_cancel.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                 hide();
            }
        });
        setModal(true);
        setSize("500px", "680px");
        loadData(client_uniq_ident);
        card.add(vert);
    }
    
    
    boolean smthChanged(){
        for (Map.Entry<Day, ArrayList<SpeedLimit>> entrySet : mapSchedulerRate.entrySet()) {
            Day key = entrySet.getKey();
            ArrayList<SpeedLimit> value = entrySet.getValue();
            ArrayList<SpeedLimit> origValue = mapSchedulerRateOriginal.get(key);
            if(!value.equals(origValue))
                return true;
        }
        
        return false;
    }
    
    Map<Day, ArrayList<SpeedLimit>> mapSchedulerRate =  new HashMap<Day, ArrayList<SpeedLimit>>();
    
    Map<Day, ArrayList<SpeedLimit>> mapSchedulerRateOriginal =  new HashMap<Day, ArrayList<SpeedLimit>>();
    
    
    void deepCopyMap(boolean direct){
        
        Map<Day, ArrayList<SpeedLimit>> dst  =  (direct==true?mapSchedulerRateOriginal:mapSchedulerRate);
        Map<Day, ArrayList<SpeedLimit>> src  =  (direct==true?mapSchedulerRate :mapSchedulerRateOriginal);;
        
        dst.clear();
        for (Map.Entry<Day, ArrayList<SpeedLimit>> entrySet : src.entrySet()) {
            Day key = entrySet.getKey();
            ArrayList<SpeedLimit> value = entrySet.getValue();
            ArrayList<SpeedLimit> value_copy  = new ArrayList<SpeedLimit>(value.size());
            for(SpeedLimit d:value)
                value_copy.add(new SpeedLimit(d));
            dst.put(key, value_copy);

        }
    
    
    }
    
   static public enum Day implements IsSerializable,Serializable{
      
        MONDAY("Понедельник"),
        TUESDAY("Вторник"), 
        WEDNESDAY("Среда"),
        THURSDAY("Четверг"), 
        FRIDAY("Пятница"), 
        SATURDAY("Суббота"),
        SUNDAY("Воскресение") ;
        
        public String getText(){return text;}
        private String  text = ""; 
      
        Day(){}
        Day(String text){
            this.text = text;
        }
    }
    public static class SpeedLimit implements IsSerializable,Serializable{
        
        public int hour;
        public int day;
        public long upload   = -1;
        public long download = -1;

        public SpeedLimit(SpeedLimit other) {
           this.hour = other.hour;
           this.day = other.day;
           this.upload = other.upload;
           this.download = other.download;
            
        }

        
        public SpeedLimit(int day,int hour) {
            this.day = day;
            this.hour  = hour;
        }

        public SpeedLimit() {
        }

        public SpeedLimit(int day,int hour,  long download ,long upload) {
            this.hour = hour;
            this.day = day;
            this.upload = upload;
            this.download = download;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj==null || !(obj instanceof SpeedLimit))
                return false;
            SpeedLimit other = (SpeedLimit)obj;
            
            return day==other.day && hour==other.hour && upload==other.upload && download==other.download;
        }
        
        
        
         static public ArrayList<RateSchedulerDialog.SpeedLimit> arrayToList(RateSchedulerDialog.Day day,Long[] array){
        if(array==null || array.length!=48)
            throw new IllegalArgumentException();
       // Long[] downloadRatePerDay   =  Arrays.copyOfRange(array,0,24);
       // Long[] uploadRatePerDay   =  Arrays.copyOfRange(array,24,48);
         ArrayList<RateSchedulerDialog.SpeedLimit> result = new ArrayList<>();
        for(int i=0;i<24;++i)
        {
            result.add(new RateSchedulerDialog.SpeedLimit(day.ordinal(), i, array[i],  array[i+24]));
        }
        return result;
        
    }
    
       static  public Long[]   listToArray(ArrayList<RateSchedulerDialog.SpeedLimit> list){
        if(list==null || list.size()!=24)
            throw new IllegalArgumentException();
        Long[] result = new Long[48];
        for(int i=0;i<24;++i)
        {
            result[i]=list.get(i).download;
            result[i+24]=list.get(i).upload;
        
        }
      
        return result;
        
    }
        
    }
    
    
    
    
    void loadData(String client_uniq_ident){
        
        GWTServiceAsync.instance.getRateLimitData(client_uniq_ident,new  AsyncCallback<Map<Day, ArrayList<SpeedLimit>>>() {

            @Override
            public void onFailure(Throwable caught) {
                center.setWidget(new HTML("Ошибка: "+caught.getLocalizedMessage())); 
                card.setActiveWidget(center);
               // Info.display("ERROR:",caught.getLocalizedMessage());
               
            }

            @Override
            public void onSuccess(Map<Day, ArrayList<SpeedLimit>> result) {
                mapSchedulerRateOriginal = result;
                deepCopyMap(false);
                comboDay.setValue(Day.MONDAY);
                grid.getStore().replaceAll(mapSchedulerRate.get(comboDay.getCurrentValue()));
                card.setActiveWidget(card.getWidget(1));
            }
        });
  
    }
    
    
     class DownloadUploadLimitDialog extends Dialog{

        static public final int type_upload = 0;
        static public final int type_download = 1;
        
        Long value = -1l;
        public DownloadUploadLimitDialog(int type) {
            setModal(true);
            setHeadingText("Ограничение "+ (type==type_download?"загрузки":"отдачи"));
            setSize("300px", "200px");
            ContentPanel cont = new ContentPanel();
            cont.setHeaderVisible(false);
            VerticalLayoutContainer vert = new VerticalLayoutContainer();
            cont.add(vert,new MarginData(10));
            HTML html_title = new HTML("Укажите максимальную скорость " +(type==type_download?"загрузки":"отдачи")+" в Кб<br><i>(0 - нет ограничений)</i>");
            final NumberField<Long> field_rate = new NumberField<Long>(new NumberPropertyEditor.LongPropertyEditor());
            vert.add(html_title,new VerticalLayoutContainer.VerticalLayoutData(1,-1,new Margins(0, 0, 10, 0)));
            vert.add(field_rate,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
            field_rate.setValue(0l);
            setWidget(cont);
            setPredefinedButtons(Dialog.PredefinedButton.CANCEL,Dialog.PredefinedButton.YES);
            setFocusWidget(field_rate);
            getButton(Dialog.PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    
                     hide();
                }
            });
        
             getButton(Dialog.PredefinedButton.YES).addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                     value = field_rate.getCurrentValue();
                     if(value==null || value<0)
                     {
                         Info.display("Внимание","Не корректное значение");
                         return;
                     }
                     hide();
                }
            });
        }
        
    
    }
    
}
