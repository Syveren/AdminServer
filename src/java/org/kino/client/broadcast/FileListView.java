/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.broadcast;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;

/**
 *
 * @author kirio
 */
public class FileListView implements  IsWidget{

   static class SelectFileDialog extends Window{
        FileListView listView ;
        FileItem item;
        String path;
        public SelectFileDialog(String title,final boolean  dir_only,final String ... filters) {
            setModal(true);
            setSize("450px", "280px");
            setBodyStyle("backgroundColor:white");
           
            setHeadingText(title);
            final CardLayoutContainer cardLay = new CardLayoutContainer();
            final CenterLayoutContainer center = new CenterLayoutContainer();
            center.add(new Image(Resources.INSTANCE.loading_bar()));

            final VerticalLayoutContainer vert = new VerticalLayoutContainer();
            setWidget(cardLay);
            cardLay.add(center);
            cardLay.add(vert);
            cardLay.setActiveWidget(center);
            listView =  new FileListView(dir_only, filters){

                @Override
                void onBeginLoading(FileItem item) {
                    cardLay.setActiveWidget(center);
                    cardLay.forceLayout();
                }

              
                @Override
                void onFinishLoading() {
                    cardLay.setActiveWidget(vert);
                    cardLay.forceLayout();
                }
                
            };
            vert.add(listView,new VerticalLayoutContainer.VerticalLayoutData(1,1));
            
            
             ToolBar butbar = new ToolBar();
            butbar.setSpacing(10);
            vert.add(butbar,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
            
            butbar.add(new FillToolItem());
            TextButton but_close = new TextButton("Отмена");
            final TextButton but_select = new TextButton("Выбрать");
            but_select.setEnabled(false);
           
            butbar.add(but_close);
            butbar.add(but_select);      
            
            
            butbar.add(but_select);    
            SelectEvent.SelectHandler selectHandler = new SelectEvent.SelectHandler() 
            {

                     @Override
                     public void onSelect(SelectEvent event) {
                          
                          if(event.getSource()==but_select)
                          {
                              FileItem selected = listView.listView.getSelectionModel().getSelectedItem();
                              path = listView.currentDir.path;
                              item = selected;
                          }
                          else {
                               path = null;
                              item = null;
                          }
                          hide();
                     }
             };
            but_select.addSelectHandler(selectHandler);
            but_close.addSelectHandler(selectHandler);
            
            listView.listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<FileItem>() {
                @Override
                public void onSelectionChanged(SelectionChangedEvent<FileItem> event) {
                    FileItem selectedItem = listView.listView.getSelectionModel().getSelectedItem();
                    if(selectedItem==null || selectedItem==listView.item_back )
                        but_select.setEnabled(false );
                    
                    if(dir_only)
                        but_select.setEnabled(true);
                    else {
                        but_select.setEnabled(!selectedItem.is_dir);
                    }
                }
            });
            but_select.addSelectHandler(selectHandler);
            
            but_close.addSelectHandler(selectHandler);
            
        }
        
    
    }
    
    
    
    static public class DirItem implements IsSerializable{
        public String path;
        public boolean is_root=false;
        public ArrayList<FileItem> fileItems;

        public DirItem() {
        }

        public DirItem(String path, ArrayList<FileItem> fileItems) {
            this.path = path;
            this.fileItems = fileItems;
        }

        
        
        
    }
    
    static public class FileItem implements IsSerializable{
        public String name;
        
        public boolean is_dir;
        public FileItem(String name,boolean is_dir) {
            this.name = name;
            this.is_dir = is_dir;
        }

        public FileItem() {
        }
        
    }
    String root_dir;
    FileItem item_back = new FileItem("Назад",false);
    
    
    DirItem currentDir;
    ListView<FileItem, FileItem> listView;
    @Override
    public Widget asWidget() {
         return listView;
    }
    
    
    public FileListView(final boolean  dir_only,final String ... filters) {
        final ListStore<FileItem> store = new  ListStore<FileItem>(new ModelKeyProvider<FileItem>() {
            @Override
            public String getKey(FileItem item) {
                if(item==item_back)
                    return "b"+item.name;
                return (item.is_dir?"d":"f")+item.name; 
            }
        });
        
        listView = new ListView<FileItem, FileItem>(store,new IdentityValueProvider<FileItem>());
        final AsyncCallback<DirItem> callback = new CallbackWithFailureDialog<DirItem>("Не удалось получить список файлов") {

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                listView.unmask();
                 onFinishLoading();
            }

            @Override
            public void onSuccess(DirItem result) {
 
                    onFinishLoading();
                    currentDir = result;
              
                    try{
                        store.clear(); // I dont know why? but cause nullpointer exception
                    }
                    catch(Exception ex){     }
                    
                    if(!result.is_root) {
                       store.add(item_back);
                    }
                    store.addAll(result.fileItems);
       
            
                
            }
        };
        AbstractCell<FileItem> cell = new AbstractCell<FileItem>() {
            
            @Override
            public void render(Cell.Context context, FileItem value, SafeHtmlBuilder sb) {
  
                String uri =null ;
                if(value==item_back)
                     uri = Resources.INSTANCE.arrow_up().getSafeUri().asString();
                else if(value.is_dir)
                     uri = Resources.INSTANCE.folder().getSafeUri().asString();
                else {
                      for(String img:new String[]{"png","jpg","jpeg","bmp"})
                      {
                          if(value.name.endsWith("."+img))
                              uri = Resources.INSTANCE.image().getSafeUri().asString();
                      }
                      if(uri==null)
                        uri = Resources.INSTANCE.file().getSafeUri().asString();
                }
                   
   
                sb.appendHtmlConstant("<table><tr vertical-align='middle'><td><img src = "+uri+" /></td><td> ")
                        .appendEscaped(value.name)
                        .appendHtmlConstant("</td></tr></table>");
            }
        };
        
         listView.setCell(cell);
        listView.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                FileItem item = listView.getSelectionModel().getSelectedItem();
                if(item==null) return;
       
                if(item==item_back){
                    GWTServiceAsync.instance.filesUp(currentDir.path,dir_only,filters, callback);
                    
                }
                else { 
                    if(item.is_dir)
                        GWTServiceAsync.instance.filesDown(currentDir.path+"/"+item.name, dir_only,filters,callback);
                    else 
                        return;
                
                }
                onBeginLoading(item);
            }
 
          
        }, DoubleClickEvent.getType());
        
        
        onBeginLoading(null);
        GWTServiceAsync.instance.filesDown(null, dir_only,filters,callback);
        
        
       
     
    }
    
    
    void onBeginLoading(FileItem item){
    }
    
    void onFinishLoading(){
    }
    
}
