/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client.clean;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.ReadOnlyValueProvider;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;
import org.kino.client.theater.TheaterW;

/**
 *
 * @author kirio
 */
 
public class CleantProjectsWidget implements IsWidget{

    CardLayoutContainer     cartLay = new CardLayoutContainer();
    BorderLayoutContainer border = new BorderLayoutContainer();
    VerticalLayoutContainer vert = new VerticalLayoutContainer();
    @Override
    public Widget asWidget() {
         return cartLay;
    }

    Integer current_id;
 
    
    public CleantProjectsWidget(String clinet_id) {
    
        try {
            int id = Integer.parseInt(clinet_id);
            current_id = id;
        }
        catch(NumberFormatException e){
            CenterLayoutContainer msg_bg = new CenterLayoutContainer();
            HTML html = new HTML("<font color='red'>Неверный идентификатор пользователя</font>");
            msg_bg.add(html);
            cartLay.add(msg_bg);
            cartLay.setActiveWidget(msg_bg);
            return;
        } 
        
        
        CenterLayoutContainer msg_bg = new CenterLayoutContainer();
        VerticalPanel panel = new VerticalPanel();
        panel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        panel.add(new HTML("Инициализация..."));
        panel.add(new Image(Resources.INSTANCE.loading_bar()));
        msg_bg.add(panel, new MarginData(15));
 
        cartLay.add(msg_bg);
        cartLay.add(border);
        
        HorizontalPanel hor = new HorizontalPanel();
        hor.setHeight("40px");
         hor.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        hor.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
        hor.add(new HTML("Очистка диска"));
     
        hor.getElement().getStyle().setBackgroundColor("#FDD");
        
        
        border.setNorthWidget(hor,new BorderLayoutContainer.BorderLayoutData(25));
        final ContentPanel cenPanel = new ContentPanel();
        cenPanel.setHeaderVisible(false);
       
        cenPanel.add(vert);
        cenPanel.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                vert.forceLayout();
            }
        });
        
        border.setCenterWidget(cenPanel);
        
        GWTServiceAsync.instance.getTheaterMainData(current_id, new CallbackWithFailureDialog<TheaterW.TheaterMainData>("Не удалось получить детальную информацию") {
 
            @Override
            public void onSuccess(TheaterW.TheaterMainData result) {
                if(result!=null) {
     
                        init(result);
                        cartLay.setActiveWidget(border);
                        cartLay.forceLayout();
                }
                else{
                    
                    CenterLayoutContainer msg_bg = new CenterLayoutContainer();
                    HTML html = new HTML("<font color='red'>Кинотеатр не найден</font>");
                    msg_bg.add(html);
                    cartLay.add(msg_bg);
                    cartLay.setActiveWidget(msg_bg);
                
                }
                
            }
        });
 
    }
    
    
     
      static public class Item implements IsSerializable{
            public int id;
            public String name;
            public long date_done;
            public long size;
            public String status;

            public Item(int id, String name, long date_done, long size, String status) {
                this.id = id;
                this.name = name;
                this.date_done = date_done;
                this.size = size;
                this.status = status;
            }


            public Item() {
            }
            
          
      
      
      }
    
    
    
    void init(TheaterW.TheaterMainData proj){
        
        vert.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        ToolBar bar = new ToolBar();
        vert.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        
       // vert.add(new Label("Название: "+proj.name),new VerticalLayoutContainer.VerticalLayoutData(1, -1, new Margins(10)));
      
        //HBoxLayoutContainer hbox = new HBoxLayoutContainer();
        
      //  vert.add(hbox,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        Label lab_county = new Label(proj.city); 
        Label lab_city = new Label(proj.county); 
        Label lab_name = new Label(proj.name); 
        
        vert.add(new FieldLabel(lab_county, "Страна"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10, 10, 0, 10)));
        vert.add(new FieldLabel(lab_city, "Город"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10, 10, 0, 10)));
        vert.add(new FieldLabel(lab_name, "Название"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10, 10, 0, 10)));
// 
//        hbox.add(lab_all_count,new BoxLayoutContainer.BoxLayoutData(new Margins(10)));
//        hbox.add(lab_done_count,new BoxLayoutContainer.BoxLayoutData(new Margins(10)));
//        hbox.add(lab_error_count,new BoxLayoutContainer.BoxLayoutData(new Margins(10)));
//        

        final ProjectsGrid projectGrid = new ProjectsGrid(proj.uniqIdent);
       
        vert.add(projectGrid,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
          projectGrid.load();
        
        TextButton but_refresh = new TextButton("Обновить", Resources.INSTANCE.refresh());
        final TextButton but_remove = new TextButton("Удалить", Resources.INSTANCE.cross_16());
     //   final TextButton but_play = new TextButton("Запуск", Resources.INSTANCE.control_play());
      //  final TextButton but_pause = new TextButton("Пауза", Resources.INSTANCE.control_pause());
     //   but_play.setEnabled(false);
      //  but_pause.setEnabled(false);
        bar.add(but_refresh);
         bar.add(but_remove);
    //    bar.add(but_play);
    //    bar.add(but_pause);
        projectGrid.grid.getSelectionModel().setSelectionMode(Style.SelectionMode.MULTI);
        projectGrid.grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<Item>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<Item> event) {
                  but_remove.setEnabled(!projectGrid.grid.getSelectionModel().getSelectedItems().isEmpty());
                 
            }
        });
        but_refresh.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                //conreteProjectGrid.load(true);
                Window.Location.reload();
                
            }
        });
        but_remove.setEnabled(false);
        but_remove.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final List<Item> selectedItems = projectGrid.grid.getSelectionModel().getSelectedItems();
                if(selectedItems.isEmpty())
                    return;
                ArrayList<Integer> idList = new ArrayList<>(selectedItems.size());
                 for(Item item:selectedItems){
                     idList.add(item.id);
                   //  item.status = "removing";
                 }
                //projectGrid.grid.getStore().fireEvent(new StoreUpdateEvent<Item>(selectedItems)); 
                 if(idList.isEmpty())
                     return;
                GWTServiceAsync.instance.markForRemove(idList, new CallbackWithFailureDialog<Void>("Произошла ошибка") {
                    @Override
                    public void onSuccess(Void result) {              
                        //Window.alert("Элементы помечены на удаление");
                         for(Item item:selectedItems){
                             projectGrid.grid.getStore().remove(item);
                         }
                        
                         Info.display("Внимание", "Проэкты помечены на удаление!");
                        
                    }
                });
            }
        });
    
        
    }
    
    
       
                
    class ProjectsGrid implements IsWidget{
      
        Grid<Item> grid;
        @Override
        public Widget asWidget() {
             return grid;
        }
 
        Request last_request;
        static final int PAGE_SIZE = 20;
        public ProjectsGrid(String client_uniq_ident) {
            this.client_uniq_ident = client_uniq_ident;
             final ColumnConfig<Item, String> col_name = new ColumnConfig<Item, String>(new ReadOnlyValueProvider<Item, String>("name") {

                 @Override
                 public String getValue(Item object) {
                     return object.name;
                 }
             }, 200, "Название");
             
              final ColumnConfig<Item, Long> col_size = new ColumnConfig<Item, Long>(new ReadOnlyValueProvider<Item, Long>("name") {

                 @Override
                 public Long getValue(Item object) {
                     return object.size;
                 }
             }, 200, "Объем");
  
             
            final ColumnConfig<Item, Date> col_date_done = new ColumnConfig<Item, Date>(new ReadOnlyValueProvider<Item, Date>("date_accept") {

                 @Override
                 public Date getValue(Item object) {
                     return new  Date(object.date_done);
                 }
             }, 100, "Дата загрузки");
              final ColumnConfig<Item, Boolean> col_remove = new ColumnConfig<Item, Boolean>(new ReadOnlyValueProvider<Item, Boolean>("remove_tag") {

                 @Override
                 public Boolean getValue(Item object) {
                     return (!object.status.equals("done"));
                 }
             }, 100, "Статус");
              
               
            CheckBoxSelectionModel<Item> checkSelectionModel = new CheckBoxSelectionModel<Item>();
            List<ColumnConfig<Item, ?>> listColumns = new ArrayList<ColumnConfig<Item, ?>>();
            listColumns.add(col_name);
            listColumns.add(col_size);
            listColumns.add(col_date_done);
            listColumns.add(col_remove);
            
            col_size.setCell(new AbstractCell<Long>() {
                  com.google.gwt.i18n.client.NumberFormat decimalFormat = com.google.gwt.i18n.client.NumberFormat.getFormat(".##");
                 
                @Override
                public void render(Cell.Context context, Long value, SafeHtmlBuilder sb) {
                      
                         double val;
                          String suffix;
                           if(value==0){
                              val = 0;
                              suffix = "";
                          }
                          else if(value<1024){
                              val = value;
                              suffix = " Байт";
                            
                          }
                          else if(value<(1024*1024)){
                              val = ((double)value)/(1024);
                              suffix = " КБайт";
                          }
                          else if(value<(1024*1024*1024)){
                              val = ((double)value)/(1024*1024);
                              suffix = " MБайт";
                          }
                          else {
                              val = ((double)value)/(1024*1024*1024);
                              suffix = " ГБайт";
     
                          }
                          sb.appendHtmlConstant(decimalFormat.format(val) +  suffix);  
   
                }
            });
             listColumns.add(checkSelectionModel.getColumn());
            col_date_done.setCell(new DateCell(DateTimeFormat.getFormat("MM/dd/yyyy hh:mm:ss")){

                @Override
                public void render(Cell.Context context, Date value, SafeHtmlBuilder sb) {
                    if(value.getTime()==0)
                        sb.appendEscaped("-");
                    else 
                        super.render(context, value, sb); //To change body of generated methods, choose Tools | Templates.
                }
                
            });
          
             col_remove.setCell(new AbstractCell<Boolean>(){

                @Override
                public void render(Cell.Context context, Boolean value, SafeHtmlBuilder sb) {
                        if(value==false)
                            return;
                        
                        sb.appendHtmlConstant("Ожидание подтверждения...");
                        
                }
                
            });
            
            for (ColumnConfig<Item, ?> columnConfig : listColumns) {
                columnConfig.setSortable(false);
                columnConfig.setMenuDisabled(true);
            }
            ColumnModel<Item> cm = new ColumnModel<Item>(listColumns);
            
            final ListStore<Item> store = new ListStore <Item>(new ModelKeyProvider<Item>() {

                 @Override
                 public String getKey(Item item) {
                      return String.valueOf(item.id);
                 }
             });
            
           
            grid = new Grid<Item>(store, cm);
            grid.setSelectionModel(checkSelectionModel);
            
            grid.getView().setEmptyText("Пусто");
       
     

        
            
        
        }
        
        String client_uniq_ident;
        private void load()
        {
            
           //last_request = 
          
            grid.mask("Загрузка...");
            if(last_request!=null)
                    last_request.cancel();
            last_request = GWTServiceAsync.instance.getClientsNotRemovedProjects(client_uniq_ident, new CallbackWithFailureDialog<ArrayList<Item>>("Не удалось загрузить данные") {
                
                @Override
                public void onSuccess(ArrayList<Item> result) {
                       grid.unmask();
                      grid.getStore().replaceAll(result);
                   
                }
            }); 

        }
    }
    
    
}
