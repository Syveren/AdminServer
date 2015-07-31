/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.monitoring;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.http.client.Request;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.ReadOnlyValueProvider;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;

/**
 *
 * @author kirio
 */
public class CurrentProjectsGrid implements IsWidget{
   
    static public class ProjectItem implements IsSerializable{
        public int id;
        public String rus_name;
        public String status;
        public long date_begin;
        public long time_left;
        public long speed;
        boolean is_downloading;

        public ProjectItem(int id, String rus_name, String status, long date_begin, long time_left, long speed, boolean is_downloading) {
            this.id = id;
            this.rus_name = rus_name;
            this.status = status;
            this.date_begin = date_begin;
            this.time_left = time_left;
            this.speed = speed;
            this.is_downloading = is_downloading;
        }
       

        public ProjectItem() {
        }
        
    }
    VerticalLayoutContainer vert = new VerticalLayoutContainer();
    Grid<ProjectItem> grid;
    @Override
    public Widget asWidget() {
         return vert;
    }

    public CurrentProjectsGrid() {

        ListStore<ProjectItem> store = new ListStore<ProjectItem>(new ModelKeyProvider<ProjectItem>() {

                @Override
                public String getKey(ProjectItem item) {
                    return String.valueOf(item.id);
                }
            });

           final ColumnConfig<ProjectItem,String> col_name = new ColumnConfig<ProjectItem,String>(new ReadOnlyValueProvider<ProjectItem,String>("rus_title") {

                    @Override
                    public String getValue(ProjectItem object) {
                         return object.rus_name;
                    }
            },100,"Название");
              
                

            ColumnConfig<ProjectItem,Date> col_date_accept = new ColumnConfig<ProjectItem, Date>(new ReadOnlyValueProvider<ProjectItem,Date>("date_accept") {

                @Override
                public Date getValue(ProjectItem object) {
                     return new  Date(object.date_begin);
                }
 
            },100,"Дата добавления");
             col_date_accept.setCell(new DateCell(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss")){

                @Override
                public void render(Cell.Context context, Date value, SafeHtmlBuilder sb) {
                    if(value.getTime()==0)
                        sb.appendEscaped("-");
                    else 
                        super.render(context, value, sb); //To change body of generated methods, choose Tools | Templates.
                }
            });  
                final ColumnConfig<ProjectItem, ProjectItem> col_time_left = new ColumnConfig<ProjectItem, ProjectItem>(new ReadOnlyValueProvider<ProjectItem, ProjectItem>("time_left") {

                 @Override
                 public ProjectItem getValue(ProjectItem object) {
                     return object ;
                 }
             }, 100, "Осталось"); 
                
               col_time_left.setCell(new AbstractCell<ProjectItem>() {
                
                @Override
                public void render(Cell.Context context, ProjectItem ob, SafeHtmlBuilder sb) {
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
                        else if(value<86400) //60*60*24
                            sb.appendEscaped((value/(60*60)) + " ч");
                        else if(value<2592000)
                            sb.appendEscaped((value/(60*60*24)) + " д");
                        else  
                            sb.appendEscaped("больше месяца");   
                    
                    
                    } 
                                
                }
            });  
               
            final ColumnConfig<ProjectItem, ProjectItem> col_speed = new ColumnConfig<ProjectItem, ProjectItem>(new ReadOnlyValueProvider<ProjectItem, ProjectItem>("speed") {

                 @Override
                 public ProjectItem getValue(ProjectItem object) {
                     return object;
                 }
             }, 100, "Cкорость");
              col_speed.setCell(new AbstractCell<ProjectItem>() {
                 NumberFormat decimalFormat = NumberFormat.getFormat(".##");
                @Override
                public void render(Cell.Context context,ProjectItem value, SafeHtmlBuilder sb) {
                    if(value.status.equals("error") || value.status.equals("ready") || value.status.equals("done"))
                    {
                        sb.appendEscaped("0");
                        return;
                    }
                    if(value.is_downloading==false)
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
             List<ColumnConfig<ProjectItem,?>> list = new ArrayList<ColumnConfig<ProjectItem,?>>();
            list.add(new RowNumberer<ProjectItem>(new IdentityValueProvider<ProjectItem>()));
             //list.add(col_poster);
            list.add(col_name);
            list.add(col_date_accept);
          //  list.add(col_client_count);
            list.add(col_time_left);
           list.add(col_speed);
            
           //  
           
            ColumnModel<ProjectItem> cm = new ColumnModel<ProjectItem>(list);
             
           grid = new Grid<ProjectItem>(store,cm  );
           ToolBar bar = new ToolBar();
           TextButton but_refresh = new TextButton("",Resources.INSTANCE.refresh());
           bar.add(but_refresh);
           vert.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1, -1));
           vert.add(grid,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
           grid.getView().setEmptyText("Нет записей");      
            
           
           but_refresh.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {

                loadData();
            }
        });
    
    }
    
      public void loadData()
    {

 
            if(grid_data_request!=null)
                grid_data_request.cancel();
            if(current_user_uniq==null)
                return;
            final CallbackWithFailureDialog<ArrayList<ProjectItem>> asyncCallback = new CallbackWithFailureDialog<ArrayList<ProjectItem>>("Не удалось получить информацию о проектах") {

                @Override
                public void onSuccess(ArrayList<ProjectItem> result) {
                       grid.unmask();
                       grid.getStore().replaceAll(result);
                       grid.getView().layout();
                }


            };
            grid.mask("Загрузка...");
            GWTServiceAsync.instance.getCurrentProjectForTheater(current_user_uniq,asyncCallback);

    }
     
    Request grid_data_request;    
    
    String current_user_uniq;
    
    public void setCurrentUser(String current_user_uniq){
        this.current_user_uniq = current_user_uniq;
        loadData();
    }
}
