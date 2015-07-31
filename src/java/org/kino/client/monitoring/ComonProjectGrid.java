/*
 * To change this license header, choose License Headers in CommonProjectInfo Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client.monitoring;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.button.TextButton;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.CellColumnResizer;
import org.kino.client.ReadOnlyValueProvider;
import org.kino.client.Resources;
import org.kino.client.monitoring.MonitoringW.CommonProjectInfo;
import org.kino.client.rpc.GWTServiceAsync;
import org.kino.client.theater.AddTheaterToExistedProjectDialog;
import org.kino.client.theater.AddTheaterToNewProjectDialog;
 
 


/**
 *
 * @author kirio
 */
 
class ComonProjectGrid implements IsWidget{ 
    
        boolean update_timer_enable = false;
       // Logger logger = Logger.getLogger("kino");
        VerticalLayoutContainer vert = new VerticalLayoutContainer();
        Grid<MonitoringW.CommonProjectInfo> grid;
        @Override
        public Widget asWidget() {
              return vert;
        }
        void onVisibileChanged(boolean isVisible){
            if(isVisible)
            {
                        if(loaded==false)
                        {
                            loaded = true;
                            loadData();
                        }
                        else {
                            if(!update_timer_enable)
                                return;
                            if(status_request!=null)
                                status_request.cancel();
                            updateStatusTimer.cancel();
                            updateStatusTimer.schedule(MonitoringW.TIMER_REFRESH_TIMEOUT);
                        }
            }
            else {
            
                            if(status_request!=null)
                                status_request.cancel();
                            updateStatusTimer.cancel();

            }
            
        }
        
        
        Timer updateStatusTimer;
        public void loadData()
        {
      
              //   logger.log(Level.INFO, "load projects");
              System.out.println("load_projects");
                if(status_request!=null)
                    status_request.cancel();
                updateStatusTimer.cancel();
                if(grid_data_request!=null)
                    grid_data_request.cancel();

                final CallbackWithFailureDialog<ArrayList<CommonProjectInfo>> asyncCallback = new CallbackWithFailureDialog<ArrayList<CommonProjectInfo>>("Не удалось получить информацию о проектах") {

                    @Override
                    public void onSuccess(ArrayList<CommonProjectInfo> result) {
                          grid.unmask();
                      
                            
                             grid.getStore().replaceAll(result);
                          
                          
                           if(update_timer_enable)
                            updateStatusTimer.schedule(MonitoringW.TIMER_REFRESH_TIMEOUT);
                    }
                
                 
                };
                grid.mask("Загрузка...");
                GWTServiceAsync.instance.getCommonProjectInfo(asyncCallback);
                
        }
        Request status_request;
        Request grid_data_request;
        
        
        
        public ComonProjectGrid() {
             
            final ListStore<MonitoringW.CommonProjectInfo> store = new ListStore<MonitoringW.CommonProjectInfo>(new ModelKeyProvider<MonitoringW.CommonProjectInfo>() {

                @Override
                public String getKey(MonitoringW.CommonProjectInfo item) {
                    return String.valueOf(item.id);
                }
            });
    
            
          /* ColumnConfig<MonitoringW.CommonProjectInfo,MonitoringW.CommonProjectInfo> col_poster = new ColumnConfig<MonitoringW.CommonProjectInfo,MonitoringW.CommonProjectInfo>(new ValueProvider<MonitoringW.CommonProjectInfo,MonitoringW.CommonProjectInfo>() {
                @Override
                public MonitoringW.CommonProjectInfo getValue(MonitoringW.CommonProjectInfo object) {
                     return object; 
                }

                @Override
                public void setValue(MonitoringW.CommonProjectInfo object, MonitoringW.CommonProjectInfo value) {  }

                @Override
                public String getPath() {
                    return "poster";
                }
            },100,"Постер");*/
            
            
            
            ColumnConfig<MonitoringW.CommonProjectInfo,MonitoringW.CommonProjectInfo> col_name = new ColumnConfig<MonitoringW.CommonProjectInfo,MonitoringW.CommonProjectInfo>(
                    new IdentityValueProvider<CommonProjectInfo>("name"),250,"Название");
              final ColumnConfig<MonitoringW.CommonProjectInfo,String> col_status = new ColumnConfig<MonitoringW.CommonProjectInfo,String>(new ReadOnlyValueProvider<MonitoringW.CommonProjectInfo,String>("status") {

                    @Override
                    public String getValue(MonitoringW.CommonProjectInfo object) {
                         return object.status;
                    }
            },100,"Статус");
              
                

              ColumnConfig<MonitoringW.CommonProjectInfo,Double> col_percent = new ColumnConfig<MonitoringW.CommonProjectInfo, Double>(new ReadOnlyValueProvider<MonitoringW.CommonProjectInfo,Double>("percent") {

                @Override
                public Double getValue(MonitoringW.CommonProjectInfo object) {
                     return object.percent;
                }
 
            },100,"процент");
              
              
            ColumnConfig<MonitoringW.CommonProjectInfo,Integer> col_client_count = new ColumnConfig<MonitoringW.CommonProjectInfo, Integer>(new ReadOnlyValueProvider<MonitoringW.CommonProjectInfo,Integer>("client_count"){

                @Override
                public Integer getValue(CommonProjectInfo object) {
                    return   object.client_count ; //To change body of generated methods, choose Tools | Templates.
                }
            }, 50,"Кол-во к/т");
              
        
           /* ColumnConfig<MonitoringW.CommonProjectInfo,Integer> col_time_left = new ColumnConfig<MonitoringW.CommonProjectInfo, Integer>(new ValueProvider<MonitoringW.CommonProjectInfo,Integer>() {

                @Override
                public Integer getValue(MonitoringW.CommonProjectInfo object) {
                     return  (Integer)object.time_left ;
                }

                @Override
                public void setValue(MonitoringW.CommonProjectInfo object, Integer value) {
                    
                }

                @Override
                public String getPath() {
                    return "time_left";
                }
            },50,"Время окончания");*/
             ColumnConfig<MonitoringW.CommonProjectInfo,Date> col_time_create = new ColumnConfig<MonitoringW.CommonProjectInfo, Date>(new ValueProvider<MonitoringW.CommonProjectInfo,Date>() {

                @Override
                public Date getValue(MonitoringW.CommonProjectInfo object) {
                     return  new Date(object.time_create) ;
                }

                @Override
                public void setValue(MonitoringW.CommonProjectInfo object, Date value) {
                    
                }

                @Override
                public String getPath() {
                    return "time_total";
                }
            },150,"Дата создания");
              
            List<ColumnConfig<MonitoringW.CommonProjectInfo,?>> list = new ArrayList<ColumnConfig<MonitoringW.CommonProjectInfo,?>>();
            list.add(new RowNumberer<MonitoringW.CommonProjectInfo>(new IdentityValueProvider<MonitoringW.CommonProjectInfo>()));
           //  list.add(col_poster);
            list.add(col_name);
            list.add(col_client_count);
            list.add(col_percent);
            list.add(col_status);
            list.add(col_time_create);
            col_name.setCell(new AbstractCell<CommonProjectInfo>() {
                 @Override
               public void render(Cell.Context context, CommonProjectInfo value, SafeHtmlBuilder sb) {
                    sb.appendHtmlConstant("<b>").appendEscaped(value.rus).appendHtmlConstant("</b><br>").appendEscaped(value.name==null?"":value.name);
               }
            });
            col_name.setComparator(new Comparator<CommonProjectInfo>() {

                @Override
                public int compare(CommonProjectInfo o1, CommonProjectInfo o2) {
                    
                   int res = o1.rus.compareTo(o2.rus);
                   return res;
                }
            });
           col_status.setCell(new AbstractCell<String>() {
               
               @Override
               public void render(Cell.Context context, String value, SafeHtmlBuilder sb) {
                  String text;
                  if(value.equals("init"))
                          text  = "Инициализация"; 
                  else if(value.equals("check")) 
                      text = "Проверка";
                  else if(value.equals("ready"))
                        text = "Ок";
                  else if(value.equals("error"))
                        text = "Ошибка";
                  else text="Не известный статус '"+value+"'";
 
                  sb.appendEscaped(text);
               }
           });
           
           col_time_create.setCell(new DateCell(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss")));
               
           
      
           // list.add(col_error);
        
           // list.add(col_time_left);
           // list.add(col_time_total);
           /*AbstractCell<MonitoringW.CommonProjectInfo> imgCell = new AbstractCell<MonitoringW.CommonProjectInfo>(){

                @Override
                public void render(Cell.Context context, MonitoringW.CommonProjectInfo value, SafeHtmlBuilder sb) {
                    sb.appendHtmlConstant("<img src='image_servlet?project_id="+ value.id+"&w=30&h=30' /> ");
                    //super.render(context, value, sb);  .
                }

                 
                
            }; 
            col_poster.setCell(imgCell);*/
            
            ProgressBarCell progress = new ProgressBarCell();
             progress.setWidth(100-10);
              progress.setProgressText("{0} %"); 
                col_percent.setCell(progress);
            //new DateCell(DateTimeFormat.getFormat("MM/dd/yyyy")
          /*  TextButtonCell button = new TextButtonCell();
            button.setIcon(Resources.INSTANCE.search_48());

            col_poster.setCell(imgCell);
            col_percent.setCell(progress);
          
    */
           
            ColumnModel<MonitoringW.CommonProjectInfo> cm = new ColumnModel<MonitoringW.CommonProjectInfo>(list);
             
            grid = new Grid<MonitoringW.CommonProjectInfo>(store,cm  );
            /*grid.addCellClickHandler(new CellClickEvent.CellClickHandler() {

                @Override
                public void onCellClick(CellClickEvent event) {
                    int cellIndex = event.getCellIndex();
                    if(!grid.getColumnModel().getColumn(cellIndex).equals(col_status)){
                        return;
                    }
                    CommonProjectInfo selectedItem = grid.getSelectionModel().getSelectedItem();
                     final PromptMessageBox box = new PromptMessageBox("Name", "Please enter your name:");
                     box.getTextField().setReadOnly(true);
                     box.getTextField().setValue(selectedItem.status);
                     box.show();
                    //Window.alert(selectedItem.status);
                }
            });*/
            
            grid.addDomHandler(new DoubleClickHandler() {

                @Override
                public void onDoubleClick(DoubleClickEvent event) {
                    CommonProjectInfo item = grid.getSelectionModel().getSelectedItem();
                    if(item==null)
                       return;
                    UrlBuilder urlb = Window.Location.createUrlBuilder();
                    urlb.setParameter("project", item.id+"");
                    Window.open(urlb.buildString(),"_blank","");
                }
            }, DoubleClickEvent.getType());
          cm.addColumnWidthChangeHandler(new CellColumnResizer (grid, col_percent, progress));
             
           /* CallbackWithFailureDialog<ArrayList<MonitoringW.CommonProjectInfo>> asyncCallback = new CallbackWithFailureDialog<ArrayList<MonitoringW.CommonProjectInfo>>("Не удалось получить информацию о передачах") {
                @Override
                public void onSuccess(ArrayList<MonitoringW.CommonProjectInfo> result) {
                     store.replaceAll(result);
                }
            };*/
          //  GWTServiceAsync.instance.getCommonProjectInfo(asyncCallback);
            
            
           // System.out.println("visible grid:"+grid.isVisible());
           // System.out.println("visible grid deep:"+grid.isVisible(true));
           
                
               final CallbackWithFailureDialog<ArrayList<CommonProjectInfo>> callback = new CallbackWithFailureDialog<ArrayList<CommonProjectInfo>>("Не удалось обновить таблицу") {
                    @Override
                    public void onSuccess(ArrayList<CommonProjectInfo> result) {
                        
                        
                         for(MonitoringW.CommonProjectInfo item:result){
                             MonitoringW.CommonProjectInfo findModelWithKey = store.findModelWithKey(String.valueOf(item.id));
                             if(findModelWithKey==null)
                                 continue;
                             findModelWithKey.percent = item.percent;
                             findModelWithKey.time_left = item.time_left;
                             findModelWithKey.time_create = item.time_create;
                             findModelWithKey.status = item.status;
                             if(item.status.equals("error"))
                             {
                                 Info.display("Внимание","<font color='red'>В проекте "
                                         +SafeHtmlUtils.htmlEscape(findModelWithKey.name)+" произошла ошибка</font>");
                                 grid.getStore().remove(findModelWithKey);
                             }
                             
                        }
                        grid.getStore().fireEvent(new StoreUpdateEvent<MonitoringW.CommonProjectInfo>(grid.getStore().getAll()));

                       // if(grid.isVisible())
                        
                            updateStatusTimer.schedule(MonitoringW.TIMER_REFRESH_TIMEOUT);

                    }
                };
            
                updateStatusTimer = new Timer() {
                    @Override
                    public void run() {
                        
                    //      logger.log(Level.INFO, "update_projects");
                       
                        status_request = GWTServiceAsync.instance.getCommonProjectInfo(callback);

                }
                };
                
                
                   
         ToolBar bar = new ToolBar();
         
         TextButton but_refresh = new TextButton("Обновить", Resources.INSTANCE.refresh());
         bar.add(but_refresh);
         but_refresh.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                      loadData();
                }
            });
         final TextButton but_add = new TextButton("Добавить кинотеатры",Resources.INSTANCE.plus_16());
         but_add.setEnabled(false);
         bar.add(but_add);
         but_add.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    
                    final CommonProjectInfo item = grid.getSelectionModel().getSelectedItem();
                    if(item==null)
                       return;
                   
                     AddTheaterToExistedProjectDialog dlg = new AddTheaterToExistedProjectDialog(item.id){

                        @Override
                        public void onAdd(int count) {
                            if(count==0)
                                return;
                            
                            // UPDATE CLIENT COUNT AND COMMON PERCENT!
                            super.onAdd(count); //To change body of generated methods, choose Tools | Templates.
                            int count_old = item.client_count;
                            double percent_old = item.percent;
                            
                            item.client_count+=count;
                            item.percent = (percent_old * count_old)/(count+count_old);
                            grid.getStore().update(item);
                        }
                     
                       
                     };
                     dlg.show();
                }
            });
         final TextButton but_show_detail = new TextButton("Подробно", Resources.INSTANCE.search_16());
         bar.add(but_show_detail);
         but_show_detail.setEnabled(false);
         grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<CommonProjectInfo>() {
                @Override
                public void onSelectionChanged(SelectionChangedEvent<CommonProjectInfo> event) {
                    but_show_detail.setEnabled(grid.getSelectionModel().getSelectedItem()!=null);
                    but_add.setEnabled(grid.getSelectionModel().getSelectedItem()!=null);
                }
            });
         
         but_show_detail.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    CommonProjectInfo item = grid.getSelectionModel().getSelectedItem();
                    if(item==null)
                        return;
                      UrlBuilder urlb = Window.Location.createUrlBuilder();
                      urlb.setParameter("project", item.id+"");
                      Window.open(urlb.buildString(),"_blank","");
                }
            });
         vert.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
         vert.add(grid,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
         grid.getView().setEmptyText("Нет записей");    
                
                
          
        
        }
        
    boolean loaded = false;
        
        
        
        
         
 
   
        
   
   }