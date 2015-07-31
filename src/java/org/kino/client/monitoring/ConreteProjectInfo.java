/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client.monitoring;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.CellColumnResizer;
import org.kino.client.ReadOnlyValueProvider;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;

/**
 *
 * @author kirio
 */
public class ConreteProjectInfo implements IsWidget{

    CardLayoutContainer     cartLay = new CardLayoutContainer();
    BorderLayoutContainer border = new BorderLayoutContainer();
    VerticalLayoutContainer vert = new VerticalLayoutContainer();
    @Override
    public Widget asWidget() {
         return cartLay;
    }

    Integer current_id;
    
    
    public ConreteProjectInfo(String project_id) {
    
        try {
            int id = Integer.parseInt(project_id);
            current_id = id;
        }
        catch(NumberFormatException e){
            CenterLayoutContainer msg_bg = new CenterLayoutContainer();
            HTML html = new HTML("<font color='red'>Неверный идентификатор проекта</font>");
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
        hor.add(new HTML("Детальная иформация о проекте"));
     
        hor.getElement().getStyle().setBackgroundColor("#DFD");
        
        
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
        
        GWTServiceAsync.instance.getProject(current_id, new CallbackWithFailureDialog<ConreteProjectInfo.ConcreteInfo>("Не удалось получить детальную информацию") {
 
            @Override
            public void onSuccess(ConreteProjectInfo.ConcreteInfo result) {
                if(result!=null) {
     
                        init(result);
                        cartLay.setActiveWidget(border);
                        cartLay.forceLayout();
                }
                else{
                    
                    CenterLayoutContainer msg_bg = new CenterLayoutContainer();
                    HTML html = new HTML("<font color='red'>Проект не найден</font>");
                    msg_bg.add(html);
                    cartLay.add(msg_bg);
                    cartLay.setActiveWidget(msg_bg);
                
                }
                
            }
        });
    
    }
    
    
    void setAllCount(int i){
        lab_all_count.setText("Кол-во кинотеатров: "+i);
 
    }
    void setDoneCount(int i){
        lab_done_count.setText("Отправлено: "+i);
 
    }
     void setErrorCount(int i){
        lab_error_count.setText("Ошибок: "+i);
     }
      
    Label lab_all_count = new Label(); 
    Label lab_done_count = new Label(); 
    Label lab_error_count = new Label(); 
    
    
    void init(ConreteProjectInfo.ConcreteInfo proj){
        
        vert.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        ToolBar bar = new ToolBar();
        vert.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        
        vert.add(new Label("Название: "+proj.name),new VerticalLayoutContainer.VerticalLayoutData(1, -1, new Margins(10)));
      
        HBoxLayoutContainer hbox = new HBoxLayoutContainer();
        
        vert.add(hbox,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        
        setAllCount(proj.count_total);
        setDoneCount(proj.count_done);
        setErrorCount(proj.error_count);
        hbox.add(lab_all_count,new BoxLayoutContainer.BoxLayoutData(new Margins(10)));
        hbox.add(lab_done_count,new BoxLayoutContainer.BoxLayoutData(new Margins(10)));
        hbox.add(lab_error_count,new BoxLayoutContainer.BoxLayoutData(new Margins(10)));
        

        final ConreteProjectGrid conreteProjectGrid = new ConreteProjectGrid(proj.count_total);
        conreteProjectGrid.load(true);
        vert.add(conreteProjectGrid,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
        vert.add(conreteProjectGrid.pager,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        
        TextButton but_refresh = new TextButton("Обновить", Resources.INSTANCE.refresh());
        final TextButton but_play = new TextButton("Запуск", Resources.INSTANCE.control_play());
        final TextButton but_pause = new TextButton("Пауза", Resources.INSTANCE.control_pause());
        but_play.setEnabled(false);
        but_pause.setEnabled(false);
        bar.add(but_refresh);
        bar.add(but_play);
        bar.add(but_pause);
        conreteProjectGrid.grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        conreteProjectGrid.grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<ConcreteProject>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<ConcreteProject> event) {
                  ConcreteProject item = conreteProjectGrid.grid.getSelectionModel().getSelectedItem();
                  but_play.setEnabled(item!=null && !item.download_server_command);
                  but_pause.setEnabled(item!=null && item.download_server_command);
            }
        });
        but_refresh.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                //conreteProjectGrid.load(true);
                Window.Location.reload();
                
            }
        });
        but_play.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final ConcreteProject item = conreteProjectGrid.grid.getSelectionModel().getSelectedItem();
                GWTServiceAsync.instance.setClientProjectDownloadStatus(item.id, true, new CallbackWithFailureDialog<Void>("Произошла ошибка") {
                    @Override
                    public void onSuccess(Void result) {              
                        item.download_server_command = true;
                        conreteProjectGrid.grid.getStore().update(item);
                        but_play.setEnabled(false);
                        but_pause.setEnabled(true);
                        
                    }
                });
            }
        });
        
        but_pause.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final ConcreteProject item = conreteProjectGrid.grid.getSelectionModel().getSelectedItem();
                GWTServiceAsync.instance.setClientProjectDownloadStatus(item.id, false, new CallbackWithFailureDialog<Void>("Произошла ошибка") {
                    @Override
                    public void onSuccess(Void result) {              
   
                        item.download_server_command = false;
                        conreteProjectGrid.grid.getStore().update(item);
                        but_play.setEnabled(true);
                        but_pause.setEnabled(false);
                    }
                });
            }
        });
        
    }
    public static class ConcreteInfo implements IsSerializable{
        public  String name;
        int count_done;
        int count_total;
        int error_count;
        public ConcreteInfo() {
        }

        public ConcreteInfo(String name, int count_total, int count_done,int error_count) {
            this.name = name;
              this.count_total = count_total;
            this.count_done = count_done;
            this.error_count = error_count;
          
        }
        

    }
    public static class ConcreteProject implements IsSerializable{
        public  int id;
        public  String name;
        public String country;
        public String city;
        public long speed;
        public long avg_speed;
        public long time_accept;
        public long time_left;
     
        public String status;
        public boolean download_server_command;
        public boolean download_client_responce;
        public double percent;
        public String error;
        public boolean is_active;
        public long time_done;
        public ConcreteProject() {
        }

        public ConcreteProject(int id, String name,String country,String city, long speed,long avg_speed, long time_accept, long time_left, String status, boolean download_server_command, boolean download_client_responce,double percent,boolean is_active,long time_done) {
            this.id = id;
            this.name = name;
            this.country = country;
            this.city = city;
            this.speed = speed;
            this.avg_speed = avg_speed;
            this.time_accept = time_accept;
            this.time_left = time_left;
            this.status = status;
            this.download_server_command = download_server_command;
            this.download_client_responce = download_client_responce;
            this.percent = percent;
            this.is_active = is_active;
            this.time_done = time_done;
        }

       

        
    }
       
  
    class ConreteProjectGrid implements IsWidget{
         PagingToolBar pager;
        Grid<ConcreteProject> grid;
        @Override
        public Widget asWidget() {
             return grid;
        }
        int totalSize ;
        Request last_request;
        static final int PAGE_SIZE = 20;
        public ConreteProjectGrid(int total_size) {
            this.totalSize = total_size;
            
             final ColumnConfig<ConcreteProject, String> columnName = new ColumnConfig<ConcreteProject, String>(new ReadOnlyValueProvider<ConcreteProject, String>("c.name") {

                 @Override
                 public String getValue(ConcreteProject object) {
                     return object.name;
                 }
             }, 200, "Название");
             
               final ColumnConfig<ConcreteProject, String> columnCountry = new ColumnConfig<ConcreteProject, String>(new ReadOnlyValueProvider<ConcreteProject, String>("c.country") {

                 @Override
                 public String getValue(ConcreteProject object) {
                     return object.country;
                 }
             }, 200, "Страна");
                final ColumnConfig<ConcreteProject, String> columnCity = new ColumnConfig<ConcreteProject, String>(new ReadOnlyValueProvider<ConcreteProject, String>("c.city") {

                 @Override
                 public String getValue(ConcreteProject object) {
                     return object.city;
                 }
             }, 200, "Город");
       
             
            
             final ColumnConfig<ConcreteProject, Double> col_percent = new ColumnConfig<ConcreteProject, Double>(new ReadOnlyValueProvider<ConcreteProject, Double>("a.percent") {

                 @Override
                 public Double getValue(ConcreteProject object) {
                     return (object.status.equals("done")?1.0:object.percent);
                 }
             }, 100, "Прогресс");
             final ColumnConfig<ConcreteProject, ConcreteProject> col_speed = new ColumnConfig<ConcreteProject, ConcreteProject>(new ReadOnlyValueProvider<ConcreteProject, ConcreteProject>("a.speed") {

                 @Override
                 public ConcreteProject getValue(ConcreteProject object) {
                     return object;
                 }
             }, 100, "Cкорость");
             
               final ColumnConfig<ConcreteProject, Long> col_avg_speed = new ColumnConfig<ConcreteProject, Long>(new ReadOnlyValueProvider<ConcreteProject, Long>("a.avg_speed") {

                 @Override
                 public Long getValue(ConcreteProject object) {
                     return object.avg_speed;
                 }
             }, 100, "Ср.скорость");
               
             final ColumnConfig<ConcreteProject, ConcreteProject> col_time_left = new ColumnConfig<ConcreteProject, ConcreteProject>(new ReadOnlyValueProvider<ConcreteProject, ConcreteProject>("a.time_left") {

                 @Override
                 public ConcreteProject getValue(ConcreteProject object) {
                     return object ;
                 }
             }, 100, "Осталось");
            
             final ColumnConfig<ConcreteProject, ConcreteProject> col_status = new ColumnConfig<ConcreteProject, ConcreteProject>(new ReadOnlyValueProvider<ConcreteProject, ConcreteProject>("a.status") {

                 @Override
                 public ConcreteProject getValue(ConcreteProject object) {
                     return object;
                 }
             }, 100, "Статус");
             
            final ColumnConfig<ConcreteProject, Date> col_date_accept = new ColumnConfig<ConcreteProject, Date>(new ReadOnlyValueProvider<ConcreteProject, Date>("a.time_accept") {

                 @Override
                 public Date getValue(ConcreteProject object) {
                     return new  Date(object.time_accept);
                 }
             }, 100, "Добавлено");
               
             final ColumnConfig<ConcreteProject, Date> col_date_done = new ColumnConfig<ConcreteProject, Date>(new ReadOnlyValueProvider<ConcreteProject, Date>("a.time_done") {

                 @Override
                 public Date getValue(ConcreteProject object) {
                     if(object.time_done==-1)return null;
                     else 
                         
                     return new  Date(object.time_done);
                 }
             }, 100, "Выполнено");
               
            List<ColumnConfig<ConcreteProject, ?>> listColumns = new ArrayList<ColumnConfig<ConcreteProject, ?>>();
             listColumns.add(columnCountry);
            listColumns.add(columnCity);
            listColumns.add(columnName);
        
            
            listColumns.add(col_percent);
            col_time_left.setSortable(false);
            
            
             final ProgressBarCell progress = new ProgressBarCell() {
                @Override
                public boolean handlesSelection() {   return true;  }
              };
            progress.setWidth(100-10);
            progress.setProgressText("{0} %"); 
            col_percent.setCell(progress);
        
            listColumns.add(col_date_accept);
            listColumns.add(col_date_done);
            listColumns.add(col_speed);
            listColumns.add(col_avg_speed);
            
            listColumns.add(col_time_left);
            listColumns.add(col_status);
             col_avg_speed.setCell(new AbstractCell<Long>() {
                 NumberFormat decimalFormat = NumberFormat.getFormat(".##");
                @Override
                public void render(Cell.Context context, Long value, SafeHtmlBuilder sb) {
                    
                    if(value==0)
                         sb.appendHtmlConstant("0");
                    else if(value<1024)
                         sb.appendHtmlConstant(value +" Байт/c");
                    else if(value<(1024*1024))
                        sb.appendHtmlConstant(decimalFormat.format(((double)value)/1024) +" Кбайт/c"); 
                    else  
                        sb.appendHtmlConstant(decimalFormat.format(((double)value)/(1024*1024))+" Мбайт/c"); 
                 
                   
                   
                }
            });
            col_speed.setCell(new AbstractCell<ConcreteProject>() {
                 NumberFormat decimalFormat = NumberFormat.getFormat(".##");
                @Override
                public void render(Cell.Context context, ConcreteProject value, SafeHtmlBuilder sb) {
                    if(value.status.equals("error") || value.status.equals("ready") || value.status.equals("done"))
                    {
                        sb.appendEscaped("0");
                        return;
                    }
                    if(value.download_server_command==false)
                    {
                        sb.appendEscaped("0");
                        
                        return;
                    }
                    
                    if(value.speed==0)
                         sb.appendHtmlConstant("0");
                    else if(value.speed<1024)
                         sb.appendHtmlConstant(value.speed +" Байт/c");
                    else if(value.speed<(1024*1024))
                        sb.appendHtmlConstant(decimalFormat.format(((double)value.speed)/1024) +" Кбайт/c"); 
                    else  
                        sb.appendHtmlConstant(decimalFormat.format(((double)value.speed)/(1024*1024))+" Мбайт/c"); 
                 
                   
                   
                }
            });
            col_time_left.setCell(new AbstractCell<ConcreteProject>() {
                
                @Override
                public void render(Cell.Context context, ConcreteProject ob, SafeHtmlBuilder sb) {
                    if(ob.status.equals("error") || ob.status.equals("done"))
                    {
                        sb.appendEscaped("0");
                        return;
                    }
                    Long value = ob.time_left;
                    if(value<0)
                       sb.appendHtmlConstant("<font size='4'>&#8734;</font>");
                    else  {
                        if(value==0)
                            sb.appendEscaped("0");
                        else if(value<60)
                            sb.appendEscaped(value.toString()+ " с");
                        else if(value<3600)
                            sb.appendEscaped((value/60) + " м");
                        else if(value<86400)
                            sb.appendEscaped((value/(60*60)) + " ч");
                        else if(value<2592000)
                            sb.appendEscaped((value/(60*60*24)) + " д");
                        else  
                            sb.appendEscaped("больше месяца");   
                    
                    
                    } 
                                
                }
            });
            
            
            col_date_accept.setCell(new DateCell(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss")){

                @Override
                public void render(Cell.Context context, Date value, SafeHtmlBuilder sb) {
                    if(value.getTime()==0)
                        sb.appendEscaped("-");
                    else 
                        super.render(context, value, sb); //To change body of generated methods, choose Tools | Templates.
                }
            });
            col_date_done.setCell(new DateCell(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss")){

                @Override
                public void render(Cell.Context context, Date value, SafeHtmlBuilder sb) {
                    if(value.getTime()==0)
                        sb.appendEscaped("-");
                    else 
                        super.render(context, value, sb); //To change body of generated methods, choose Tools | Templates.
                }
            });
            col_status.setCell(new AbstractCell<ConcreteProject>() {
                
                @Override
                public void render(Cell.Context context, ConcreteProject value, SafeHtmlBuilder sb) {
 
                     // switch not work in gwt 
                     if("ready".equals(value.status))
                             sb.appendHtmlConstant("<div>Ожидание</div>");
                     else if("accept".equals(value.status))   
                             sb.appendHtmlConstant("<div>"+(value.download_server_command?"Передача":"Пауза")
                                +(value.download_client_responce!=value.download_server_command?" (*)":"")
                                +(value.is_active?"":" (offline)")
                                 +"</div>");
                     else if("done".equals(value.status))   
                         sb.appendHtmlConstant("<div>Выполнено</div>");
                     else if("error".equals(value.status))   
                         sb.appendHtmlConstant("<font color = 'red'>Ошибка: </font>").appendEscaped(value.error) ;
                     else if("removing".equals(value.status))   
                          sb.appendHtmlConstant("<div>Удален (*)</div>");
                     else if("removed".equals(value.status))
                          sb.appendHtmlConstant("<div>Удален</div>");
                     else sb.appendHtmlConstant("<div>").appendEscaped(value.status).appendHtmlConstant("</div>") ;  
                       
               
                    
              
                     
                }
            });
            
            
            for (ColumnConfig<ConcreteProject, ?> columnConfig : listColumns) {
               // columnConfig.setSortable(false);
                columnConfig.setMenuDisabled(true);
            }
            ColumnModel<ConcreteProject> cm = new ColumnModel<ConcreteProject>(listColumns);
            
            final ListStore<ConcreteProject> store = new ListStore <ConcreteProject>(new ModelKeyProvider<ConcreteProject>() {

                 @Override
                 public String getKey(ConcreteProject item) {
                      return String.valueOf(item.id);
                 }
             });
            
           
            grid = new Grid<ConcreteProject>(store, cm);
            cm.addColumnWidthChangeHandler(new CellColumnResizer(grid, col_percent, progress));
            grid.getView().setEmptyText("Пусто");
            RpcProxy proxy =   new RpcProxy<PagingLoadConfig, PagingLoadResult<ConcreteProject>>() {
           
          
                @Override
                public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<ConcreteProject>> callback) {
                    if(last_request!=null)
                        last_request.cancel();
                     
                     last_request = GWTServiceAsync.instance.getConcreteProjects(current_id, totalSize,loadConfig  ,callback);
                }
        };
       
        PagingLoader loader  = new PagingLoader<PagingLoadConfig, PagingLoadResult<ConcreteProject>>(proxy) 
        {

             @Override
             protected void onLoadSuccess(PagingLoadConfig loadConfig, PagingLoadResult<ConcreteProject> result) {
                 grid.unmask();
                 super.onLoadSuccess(loadConfig, result);
                 
                 if(totalSize!=result.getTotalLength())
                 {
                     
                     totalSize = result.getTotalLength();
                     //setAllCount(totalSize);
                 }
             }


        };
        loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig,ConcreteProject, PagingLoadResult<ConcreteProject>>(store));
            pager = new PagingToolBar(PAGE_SIZE){
                        @Override
                        public void refresh() {  
                            load(true);
                        }
            };
            pager.bind(loader);
            loader.setRemoteSort(true);
            grid.setLoader(loader);
            
        
            
        
        }
        
        
        private void load(boolean refresh_total_size)
        {
            if(refresh_total_size)
                totalSize = -1;
            grid.mask("Загрузка...");
            grid.getLoader().load();
 

        }
    }
    
    
}
