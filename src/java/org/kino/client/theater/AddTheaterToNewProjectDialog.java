/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client.theater;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.ComboBoxWithClearBut;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;
import static org.kino.client.theater.TheaterW.TheaterGrid.PAGE_SIZE;

/**
 *
 * @author kirio
 */
public class AddTheaterToNewProjectDialog extends Window{

    ComboBoxWithClearBut  combo_filter_countryes;
    StoreFilterField<TheaterItem>    field_filter;
    
    CardLayoutContainer cartLay = new CardLayoutContainer();
    @Override
    public void clear(){

        combo_filter_countryes.clear();

        field_filter.clear();

        grid.deselectAll();

    }
    TheaterGrid grid ;
    public AddTheaterToNewProjectDialog() {
            setModal(true);
            setSize("450px", "280px");
           // setAutoHide(true);
            setBodyStyle("backgroundColor:white");
           
            setHeadingText("Выбор");
            VerticalLayoutContainer vert = new VerticalLayoutContainer();
           
            
            grid = new TheaterGrid();
            combo_filter_countryes = new ComboBoxWithClearBut(){

                @Override
                public void onClear() {
                  
                    super.onClear();
                    grid.loadData(true);
                    // grid.grid.getStore().setEnableFilters(false);
                    // grid.grid.getStore().setEnableFilters(true);
            
                }
                    
            }; 

             field_filter = new StoreFilterField<TheaterItem> () {
            
                 @Override
                protected void onFilter() {
                    super.onFilter();
                    grid.loadData(true);
                }
                @Override
                protected boolean doSelect(Store<TheaterItem> store, TheaterItem parent, TheaterItem item, String filter) {
                    return true;

                }
            };
             field_filter.setValidationDelay(1500);
          field_filter.bind(grid.grid.getStore());
         
      
             
        
 
      
            combo_filter_countryes.combo.addSelectionHandler(new SelectionHandler<String>() {

                @Override
                public void onSelection(SelectionEvent<String> event) {

                    grid.loadData(true);
                     /// grid.grid.getStore().setEnableFilters(false);
                     // grid.grid.getStore().setEnableFilters(true);
                }
            });
 
             
         
            CallbackWithFailureDialog<ArrayList<ArrayList<String>>> asyncCallback = new CallbackWithFailureDialog<ArrayList<ArrayList<String>>>("Не удалось получить список стран") {

            @Override
            public void onSuccess(ArrayList<ArrayList<String>> result) {
                if(result.isEmpty())
                    combo_filter_countryes.combo.setEmptyText("список пуст");
                combo_filter_countryes.combo.getStore().replaceAll(result.get(0));
            }
        };
       
        GWTServiceAsync.instance.getCountriesAndCitys(asyncCallback);
        ToolBar butbar = new ToolBar();
        butbar.setSpacing(0);
      

        
        TextButton but_close = new TextButton("Закрыть");
        final TextButton but_select = new TextButton("Выделить все");
      
 
       
        butbar.add(new FillToolItem());
        butbar.add(but_close);
        //butbar.add(but_select);
        
        vert.add(new FieldLabel(combo_filter_countryes.asWidget(),"Страна"),new VerticalLayoutContainer.VerticalLayoutData(1,-1,new Margins(10, 10, 0,10)));
        vert.add(new FieldLabel(field_filter,"Фильтр [a,b,c,...]"),new VerticalLayoutContainer.VerticalLayoutData(1,-1,new Margins(10, 10, 10, 10)));    
        vert.add(grid,new VerticalLayoutContainer.VerticalLayoutData(1,1));
        vert.add(grid.pager,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
        vert.add(butbar,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
        
         
        but_close.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                      hide();
                }
            });
        but_select.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                     
                }
            });
        
        
        
      //  CenterLayoutContainer msg_bg = new CenterLayoutContainer();
      //  VerticalPanel panel = new VerticalPanel();
       // panel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
       // panel.add(new HTML("Инициализация..."));
       // panel.add(new Image(Resources.INSTANCE.loading_bar()));
       // msg_bg.add(panel, new MarginData(15));
        //    cartLay.add(msg_bg);
        cartLay.add(vert);
        setWidget(cartLay);
    }
 
    public HashMap<Integer,String> checkedItems = new HashMap<Integer,String>();
     class TheaterGrid implements IsWidget{
  
         
        @Override
        public Widget asWidget() {
             return grid;
        }
        Grid<TheaterItem> grid;
        boolean loaded = false;
     
        //int selected=0;
       ValueProvider<TheaterItem, Boolean> checkProvider ;
       void selectAll(){
            List<TheaterItem> all = grid.getStore().getAll();
           for(TheaterItem item : all){
               checkProvider.setValue(item, true);
           }
           grid.getStore().fireEvent(new StoreUpdateEvent<TheaterItem>(all));
           
       }
       
       void deselectAll(){
     
          List<TheaterItem> all = grid.getStore().getAll();
      
          checkedItems.clear();
           for(TheaterItem item : all){
               checkProvider.setValue(item, false);
           }
            
           grid.getStore().fireEvent(new StoreUpdateEvent<TheaterItem>(all));
        
       }
         
        public TheaterGrid() {
            
           
            final ListStore<TheaterItem> store = new ListStore<TheaterItem>(new ModelKeyProvider<TheaterItem>() {

                @Override
                public String getKey(TheaterItem item) {
                    return String.valueOf(item.id);
                }
            });
           
            ColumnConfig<TheaterItem,String> col_city = new ColumnConfig<TheaterItem, String>(new ValueProvider<TheaterItem,String>() {

                @Override
                public String getValue(TheaterItem object) {
                     return object.address.city;
                }

                @Override
                public void setValue(TheaterItem object, String value) {
                    
                }

                @Override
                public String getPath() {
                    return "city";
                }
            },100,"Город");


              ColumnConfig<TheaterItem,String> col_name = new ColumnConfig<TheaterItem, String>(new ValueProvider<TheaterItem,String>() {

                @Override
                public String getValue(TheaterItem object) {
                     return object.main.name;
                }

                @Override
                public void setValue(TheaterItem object, String value) {
                    
                }

                @Override
                public String getPath() {
                    return "name";
                }
            },100,"Имя");
                ColumnConfig<TheaterItem,Boolean> col_status = new ColumnConfig<TheaterItem, Boolean>(new ValueProvider<TheaterItem,Boolean>() {
                @Override
                public Boolean getValue(TheaterItem object) {
                     return object.status;
                }

                @Override
                public void setValue(TheaterItem object, Boolean value) {
                    
                }

                @Override
                public String getPath() {
                    return "time_last_active_sec";
                }
            },50,"Статус");
                    col_status.setCell(new AbstractCell<Boolean>() {
                
                @Override
                public void render(Cell.Context context, Boolean value, SafeHtmlBuilder sb) {
                    ImageResource res  = (value? Resources.INSTANCE.status_green():Resources.INSTANCE.status_red());
                    sb.appendHtmlConstant("<img src='"+res.getSafeUri().asString()+"' /> ");
                }
            });
              checkProvider = new ValueProvider<TheaterItem,Boolean>() {

                @Override
                public Boolean getValue(TheaterItem object) {
                     return checkedItems.containsKey(object.id);
                }

                @Override
                public void setValue(TheaterItem object, Boolean value) {
                    if(value)
                        checkedItems.put(object.id,object.main.uniqIdent);
                    else 
                        checkedItems.remove(object.id);
                }

                @Override
                public String getPath() {
                    return "checked";
                }
            };
            ColumnConfig<TheaterItem,Boolean> col_checked = new ColumnConfig<TheaterItem, Boolean>(checkProvider,100,"");  
            CheckboxCell checkboxCell = new CheckboxCell();
   
            col_checked.setCell(checkboxCell);
 
            List<ColumnConfig<TheaterItem,?>> list = new ArrayList<ColumnConfig<TheaterItem,?>>();
             list.add(col_status);
            list.add(col_city);
            list.add(col_name);
            list.add(col_checked);
             col_checked.setSortable(false);
          //  for(ColumnConfig conf:list)
           //     conf.setSortable(false);
            store.setAutoCommit(true); 
 
            ColumnModel<TheaterItem> cm = new ColumnModel<TheaterItem>(list);
            grid = new Grid<TheaterItem>(store,cm){

                @Override
                protected void onAfterFirstAttach() {
                    super.onAfterFirstAttach(); //To change body of generated methods, choose Tools | Templates.
                    loadData(true);

                }
                
                
            };
           // grid.setSelectionModel(selectionModelCheck);
            
            RpcProxy<MyLoadConfig, PagingLoadResult<TheaterItem>> proxy =   new RpcProxy<MyLoadConfig, PagingLoadResult<TheaterItem>>() {
                @Override
                public void load(MyLoadConfig loadConfig, AsyncCallback<PagingLoadResult<TheaterItem>> callback) {
                    if(grid_data_request!=null)
                        grid_data_request.cancel();
                   //  System.out.println(field_filter.getText());
                     loadConfig.setFilter("country", combo_filter_countryes.getCurrentValue());
                     loadConfig.setFilter("complex", field_filter.getText());
                    
                     grid_data_request = GWTServiceAsync.instance.getTheaterGridItems(totalSize,loadConfig,callback);
                    
                }
            };
     
            loader  = new PagingLoader<MyLoadConfig, PagingLoadResult<TheaterItem>>(proxy) 
            {

                 @Override
                 protected void onLoadSuccess(MyLoadConfig loadConfig, PagingLoadResult<TheaterItem> result) {
                     super.onLoadSuccess(loadConfig, result);

                     if(totalSize!=result.getTotalLength())
                        totalSize = result.getTotalLength();

                 }


            };
            loader.addLoadHandler(new LoadResultListStoreBinding<MyLoadConfig,TheaterItem, PagingLoadResult<TheaterItem>>(store){

                @Override
                public void onLoad(LoadEvent<MyLoadConfig, PagingLoadResult<TheaterItem>> event) {
     
                    super.onLoad(event); //To change body of generated methods, choose Tools | Templates.grid.getStore()\
                    List<TheaterItem> all = grid.getStore().getAll();
                    for(TheaterItem item:all)
                        if(checkedItems.containsKey(item.id)){
                            checkedItems.put(item.id, item.main.uniqIdent);//update malo li chto
                            checkProvider.setValue(item, true);
                             
                        }
                    grid.getStore().fireEvent(new StoreUpdateEvent<TheaterItem>(all));
                }
                
            });
        
            pager = new PagingToolBar(PAGE_SIZE){
                        @Override
                        public void refresh() {  
                            loadData(true);
                        }
            };
          
            pager.bind(loader);
             loader.setRemoteSort(true);
            grid.setLoader(loader);
            loader.useLoadConfig(new MyLoadConfig(0,PAGE_SIZE));
 
           
         
            
            
            
            
            
        }
        
        int totalSize = -1;
        PagingToolBar pager;
        PagingLoader<MyLoadConfig, PagingLoadResult<TheaterItem>> loader;
        void loadData(boolean refreshTotalSize){
            

                if(grid_data_request!=null)
                    grid_data_request.cancel();
                 if(refreshTotalSize==true)
                    totalSize=-1;
     
            
                 
                loader.load();
                
                /*final CallbackWithFailureDialog<ArrayList<TheaterW.Item>> asyncCallback = new CallbackWithFailureDialog<ArrayList<TheaterW.Item>>("Не удалось получить информацию о кинотеатрах") {
                @Override
                public void onSuccess(ArrayList<TheaterW.Item> result) {
                     grid.getStore().replaceAll(result);
                    }
                };
                GWTServiceAsync.instance.getTheaterGridItems(null,null,asyncCallback);*/
                
        }

        Request grid_data_request;
    }
    
}
