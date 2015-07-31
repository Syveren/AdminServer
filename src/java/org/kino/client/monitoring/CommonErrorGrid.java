/*
 * To change this license header, choose License Headers in CommonProjectInfo Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client.monitoring;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
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
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;
 

/**
 *
 * @author kirio
 */
 
class CommonErrorGrid implements IsWidget{ 
      boolean update_timer_enable = false;
      VerticalLayoutContainer vert = new VerticalLayoutContainer();
      Grid<MonitoringW.CommonErrorInfo> grid;
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
            
           
            System.out.println("load_error");
                if(status_request!=null)
                    status_request.cancel();
                updateStatusTimer.cancel();
                if(grid_data_request!=null)
                    grid_data_request.cancel();

                final CallbackWithFailureDialog<ArrayList<MonitoringW.CommonErrorInfo>> asyncCallback = new CallbackWithFailureDialog<ArrayList<MonitoringW.CommonErrorInfo>>("Не удалось получить информацию об ошибках") {

                    @Override
                    public void onSuccess(ArrayList<MonitoringW.CommonErrorInfo> result) {
                          grid.unmask();  
                        grid.getStore().replaceAll(result);
                       
                         if(update_timer_enable)
                          updateStatusTimer.schedule(MonitoringW.TIMER_REFRESH_TIMEOUT);
                    }
                
                 
                };
                 grid.mask("Загрузка...");
                GWTServiceAsync.instance.getCommonErrorInfo(asyncCallback);
                
        }
        Request status_request;
        Request grid_data_request;
        
        
        
        public CommonErrorGrid() {
             
            final ListStore<MonitoringW.CommonErrorInfo> store = new ListStore<MonitoringW.CommonErrorInfo>(new ModelKeyProvider<MonitoringW.CommonErrorInfo>() {

                @Override
                public String getKey(MonitoringW.CommonErrorInfo item) {
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
            
            
            ColumnConfig<MonitoringW.CommonErrorInfo,MonitoringW.CommonErrorInfo> col_name = new ColumnConfig<MonitoringW.CommonErrorInfo,MonitoringW.CommonErrorInfo>(
                    new IdentityValueProvider<MonitoringW.CommonErrorInfo>("name"),250,"Название");
             
               col_name.setCell(new AbstractCell<MonitoringW.CommonErrorInfo>() {
                 @Override
               public void render(Cell.Context context, MonitoringW.CommonErrorInfo value, SafeHtmlBuilder sb) {
                     sb.appendHtmlConstant("<b>").appendEscaped(value.rus).appendHtmlConstant("</b><br>").appendEscaped(value.eng==null?"":value.eng);
               }
            });
            col_name.setComparator(new Comparator<MonitoringW.CommonErrorInfo>() {

                @Override
                public int compare(MonitoringW.CommonErrorInfo o1, MonitoringW.CommonErrorInfo o2) {
                   int res = o1.rus.compareTo(o2.rus);
                   return res;
                }
            });
            
            ColumnConfig<MonitoringW.CommonErrorInfo,String> col_folder = new ColumnConfig<MonitoringW.CommonErrorInfo,String>(
                    new ValueProvider<MonitoringW.CommonErrorInfo,String>() {

                @Override
                public String getValue(MonitoringW.CommonErrorInfo object) {
                    return object.folder;
                }

                @Override
                public void setValue(MonitoringW.CommonErrorInfo object, String value) {
                }

                @Override
                public String getPath() {
                    return "folder";
                }

                
            },250,"Директория");
             
         
            
               ColumnConfig<MonitoringW.CommonErrorInfo,String> col_error = new ColumnConfig<MonitoringW.CommonErrorInfo,String>(new ValueProvider<MonitoringW.CommonErrorInfo,String>() {

                @Override
                public String getValue(MonitoringW.CommonErrorInfo object) {
                     return object.error;
                }

                @Override
                public void setValue(MonitoringW.CommonErrorInfo object, String value) {
                    
                }

                @Override
                public String getPath() {
                    return "error";
                }
            },100,"Ошибка"); 

             
            ColumnConfig<MonitoringW.CommonErrorInfo,Integer> col_client_count = new ColumnConfig<MonitoringW.CommonErrorInfo, Integer>(new ValueProvider<MonitoringW.CommonErrorInfo,Integer>() {

                @Override
                public Integer getValue(MonitoringW.CommonErrorInfo object) {
                     return   object.client_count ;
                }

                @Override
                public void setValue(MonitoringW.CommonErrorInfo object, Integer value) {
                    
                }

                @Override
                public String getPath() {
                    return "client_count";
                }
            },50,"Кол-во к/т");
            
              
            List<ColumnConfig<MonitoringW.CommonErrorInfo,?>> list = new ArrayList<ColumnConfig<MonitoringW.CommonErrorInfo,?>>();
            list.add(new RowNumberer<MonitoringW.CommonErrorInfo>(new IdentityValueProvider<MonitoringW.CommonErrorInfo>()));
             //list.add(col_poster);
            list.add(col_name);
            list.add(col_folder);
          //  list.add(col_client_count);
            list.add(col_error);
           
            
           // list.add(col_error);
           // list.add(col_percent);
           // list.add(col_time_left);
           // list.add(col_time_total);
            /*AbstractCell<MonitoringW.CommonProjectInfo> imgCell = new AbstractCell<MonitoringW.CommonProjectInfo>(){

                @Override
                public void render(Cell.Context context, MonitoringW.CommonProjectInfo value, SafeHtmlBuilder sb) {
                    sb.appendHtmlConstant("<img src='image_servlet?project_id="+ value.id+"' /> ");
                    //super.render(context, value, sb);  .
                }

                 
                
            };*/
            //new DateCell(DateTimeFormat.getFormat("MM/dd/yyyy")
          /*  TextButtonCell button = new TextButtonCell();
            button.setIcon(Resources.INSTANCE.search_48());
            final ProgressBarCell progress = new ProgressBarCell() {
                @Override
                public boolean handlesSelection() {
                  return true;
                }
              };
            progress.setWidth(100-10);
            progress.setProgressText("{0}%");*/
            //col_poster.setCell(imgCell);
            //col_percent.setCell(progress);
          
    
           
            ColumnModel<MonitoringW.CommonErrorInfo> cm = new ColumnModel<MonitoringW.CommonErrorInfo>(list);
             
            grid = new Grid<MonitoringW.CommonErrorInfo>(store,cm  );
         
                
               final CallbackWithFailureDialog<ArrayList<MonitoringW.CommonProjectInfo>> callback = new CallbackWithFailureDialog<ArrayList<MonitoringW.CommonProjectInfo>>("Не удалось обновить таблицу") {
                    @Override
                    public void onSuccess(ArrayList<MonitoringW.CommonProjectInfo> result) {
                          
              
                        
                            
                         updateStatusTimer.schedule(MonitoringW.TIMER_REFRESH_TIMEOUT);

                    }
                };
            
                updateStatusTimer = new Timer() {
                    @Override
                    public void run() {
                        
                          System.out.println("update_error");
                         status_request = GWTServiceAsync.instance.getCommonProjectInfo(callback);

                }
                };
                
                
                
                
                
           /*     
                 grid.addShowHandler(new ShowEvent.ShowHandler() {
                @Override
                public void onShow(ShowEvent event) {
                    if(grid.isVisible() && grid.isVisible()){
                         
                        
                        if(loaded==false)
                        {
                            loaded = true;
                            loadData();
                        }
                        else {
                            
                            if(status_request!=null)
                                status_request.cancel();
                            updateStatusTimer.cancel();
                            updateStatusTimer.schedule(TIMER_REFRESH_TIMEOUT);
                        }
                    }
                   
                }});   */ 
          
                ToolBar bar = new ToolBar();
         
                TextButton but_refresh = new TextButton("Обновить", Resources.INSTANCE.refresh());
                bar.add(but_refresh);
                but_refresh.addSelectHandler(new SelectEvent.SelectHandler() {

                       @Override
                       public void onSelect(SelectEvent event) {
                             loadData();
                       }
                   });
         
           /* final TextButton but_show_detail = new TextButton("Подробно", Resources.INSTANCE.search_16());
            bar.add(but_show_detail);
            but_show_detail.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    MonitoringW.CommonErrorInfo item = grid.getSelectionModel().getSelectedItem();
                    if(item==null)
                        return;
                      UrlBuilder urlb = Window.Location.createUrlBuilder();
                      urlb.setParameter("project_error", item.id+"");
                      Window.open(urlb.buildString(),"_blank","");
                }
            });
            
            
              but_show_detail.setEnabled(false);
             grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<MonitoringW.CommonErrorInfo>() {
                @Override
                public void onSelectionChanged(SelectionChangedEvent<MonitoringW.CommonErrorInfo> event) {
                    but_show_detail.setEnabled(grid.getSelectionModel().getSelectedItem()!=null);
                }
            });*/
         
     
            vert.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
            vert.add(grid,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
            grid.getView().setEmptyText("Нет записей");
        }
        
   boolean loaded = false;
        
        
         
     
 
        
   
   }