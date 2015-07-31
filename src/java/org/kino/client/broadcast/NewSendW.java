/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.broadcast;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import org.kino.client.CallbackWithFailureDialog;
 
import org.kino.client.Resources;
import org.kino.client.rpc.GWTServiceAsync;
import org.kino.client.theater.AddTheaterToNewProjectDialog;

/**
 *
 * @author kirio
 */
public class NewSendW implements IsWidget{
     CenterLayoutContainer center = new CenterLayoutContainer();
    
    public void clear(){
      
        field_rus.clear();
        field_orig.clear();
        field_dir.clear();
 
        field_prog_name.clear();
        field_poster.clear();
        area_description.clear();
        fieldLabel_prog_name.setVisible(false);
 
        selectTheaterDialog.clear();
 
        lab_theater_count.setText("Выбрано: 0");
        but_show_poster.setVisible(false);
        needParentLayout();
    
    }
    @Override 
    public Widget asWidget() {
         return center;
    }

     
    public void needParentLayout(){

    };
    
    public void onCancelClick(){

    }
    public static class NewProject implements IsSerializable{
        public Integer id;
        public String rus_name;
        public String orig_name;
        public String project_dir;
        public String poster_path;
        public String project_name;
        public String anotation;
   

        public NewProject(Integer id, String rus_name, String orig_name, String dcp_path, String poster_path, String project_name,String description) {
            this.id = id;
            this.rus_name = rus_name;
            this.orig_name = orig_name;
            this.project_dir = dcp_path;
            this.poster_path = poster_path;
            this.project_name = project_name;
            this.anotation = description;
         
        }

        

        public NewProject() {
        }
        
        
    }
    boolean validateField(TextField f){
        boolean res = f.getValue()!=null;
        if(!res){
            f.focus();
        }
        return res;
     }
    TextField field_rus;
    TextField field_orig;
    TextField field_dir;
    TextField field_prog_name;
    TextField field_poster;
    Label lab_theater_count;
    TextArea   area_description;
    FieldLabel fieldLabel_prog_name;
    
    AddTheaterToNewProjectDialog selectTheaterDialog;
    TextButton but_show_poster;
    
    void showPosterDialog(final String path){
    
        final Dialog dlg = new Dialog();
        dlg.setHeadingText("Изображение: "+path);
        dlg.setAutoHide(true);
        dlg.setModal(true);
        dlg.setSize("550px", "550px");
        dlg.setBodyStyle("backgroundColor:#ddd");
        final CenterLayoutContainer center_cont = new CenterLayoutContainer();
        dlg.add(center_cont);
        dlg.setHideOnButtonClick(true);
        System.out.println(GWT.getHostPageBaseURL());
       
        //final HTML html = new HTML("<table><tr><td valign='middle'><img src='image_servlet?path="+ path +"&w=500&h=500' /></td></tr></table");
       // html.setSize("500px", "500px");
        final Image img = new Image("image_servlet?path="+ path +"&w=500&h=500");
        
        RootPanel.get().add(img);
        img.setVisible(false);
        
        center_cont.setWidget(new Image(Resources.INSTANCE.loading_bar()));
        
        
         dlg.show();
      
         img.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                
                  center_cont.setWidget(img);
                   img.setVisible(true);
                   dlg.forceLayout(); 
            }
        });
    
        //Scheduler.get().scheduleDeferred(task);
        
         
    }
    
    
    
    public NewSendW() {
 
        selectTheaterDialog = new AddTheaterToNewProjectDialog();
//        center.setResize(false);
        field_rus = new TextField();
        //field_rus.setEmptyText("Введите русское название проекта");
         field_orig = new TextField();
         // field_orig.setEmptyText("Введите оригинальное название проекта");
         field_dir = new TextField();
         
         field_poster = new TextField();
          //field_poster.setEmptyText("Выберите постер");
         field_poster.setReadOnly(true);
         
          //field_dir.setEmptyText("Выберите файл DCP");
         field_dir.setReadOnly(true);
         area_description = new TextArea();
          //area_description.setEmptyText("Введите описание");
         
          field_prog_name = new TextField();
          field_prog_name.setReadOnly(true); 
          
          lab_theater_count = new Label("Выбрано: 0");
        // PosterThumb postgreThumb = new PosterThumb();
       
         // postgreThumb.setPhoto(new PosterThumb.Photo(Resources.INSTANCE.empty_img_128().getSafeUri()));
        final ContentPanel cont = new ContentPanel();
        cont.setHeaderVisible(false);
        cont.setWidth(600);
        cont.setHeight(600);
    
        final VerticalLayoutContainer vert_global = new VerticalLayoutContainer();
    
        cont.add(vert_global);
         
 
 
        Margins margin = new Margins(5, 0,5, 10);
 
        BoxLayoutContainer.BoxLayoutData box2 = new BoxLayoutContainer.BoxLayoutData();
        box2.setFlex(1);
        HBoxLayoutContainer hbox_dir = new HBoxLayoutContainer(HBoxLayoutContainer.HBoxLayoutAlign.MIDDLE);
        hbox_dir.add(field_dir,box2);
        final TextButton but_add_dir = new TextButton("",Resources.INSTANCE.search_16());
        final TextButton but_clear_dir = new TextButton("",Resources.INSTANCE.cross_white());
        hbox_dir.add( but_add_dir);
        hbox_dir.add( but_clear_dir);
        final HBoxLayoutContainer hbox_post = new HBoxLayoutContainer(HBoxLayoutContainer.HBoxLayoutAlign.MIDDLE);
        hbox_post.add(field_poster,box2);
        final TextButton but_add_poster = new TextButton("",Resources.INSTANCE.search_16());
        final TextButton but_clear_poster = new TextButton("",Resources.INSTANCE.cross_white());
         but_show_poster = new TextButton("",Resources.INSTANCE.image());
        hbox_post.add(but_add_poster );
        hbox_post.add( but_clear_poster);
        hbox_post.add( but_show_poster);
        but_show_poster.setVisible(false);
        HBoxLayoutContainer hbox_theater = new HBoxLayoutContainer(HBoxLayoutContainer.HBoxLayoutAlign.MIDDLE);
        final TextButton but_add_theater = new TextButton("добавить / удалить");
        hbox_theater.add(but_add_theater,new BoxLayoutContainer.BoxLayoutData(new Margins(0,10,0,0)));
        hbox_theater.add(lab_theater_count,box2);
 
        selectTheaterDialog.addHideHandler(new HideEvent.HideHandler() {
            @Override
            public void onHide(HideEvent event) {
      
               lab_theater_count.setText("Выбрано: "+String.valueOf(selectTheaterDialog.checkedItems.size()) );
            }
        });
        but_add_theater.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                selectTheaterDialog.show();
             
            }
        });
        
        fieldLabel_prog_name = new FieldLabel(field_prog_name,"Имя проекта");
        vert_global.setAdjustForScroll(true);
        vert_global.add(new FieldLabel(field_rus,"Русское название"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,margin));
        vert_global.add(new FieldLabel(field_orig,"Оригинально название"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,margin));
        vert_global.add(new FieldLabel(hbox_post,"Путь к постеру"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,margin));
        vert_global.add(new FieldLabel(hbox_dir,"Путь к DCP"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,margin));
        vert_global.add(new FieldLabel(hbox_theater,"Кинтотеатры"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,margin));
        vert_global.add(fieldLabel_prog_name,new VerticalLayoutContainer.VerticalLayoutData(1, -1,margin));
        vert_global.add(new FieldLabel(area_description,"Описание"),new VerticalLayoutContainer.VerticalLayoutData(1,  1,margin));
        fieldLabel_prog_name.setVisible(false);
        
        but_clear_dir.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                  field_dir.setValue("");
                  field_prog_name.setValue("");
                  fieldLabel_prog_name.setVisible(false);
                  needParentLayout();
            }
        });
          but_clear_poster.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                  field_poster.setValue("");
                  but_show_poster.setVisible(false);
                  hbox_post.forceLayout();
            }
        });
          
          but_show_poster.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                showPosterDialog(field_poster.getCurrentValue());
            }
        });
  
        
        
        SelectEvent.SelectHandler handler = new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                 final boolean project_dcp = event.getSource().equals(but_add_dir);
                 String[] filters = (project_dcp?new String[0]:new String[]{"png","jpeg","bmp","jpg"});
                 final FileListView.SelectFileDialog dlg = new FileListView.SelectFileDialog("Выберите файл",project_dcp,filters);
                 dlg.show();
                 dlg.addHideHandler(new HideEvent.HideHandler() {
                     @Override
                     public void onHide(HideEvent event) {
                        if(dlg.item==null)
                            return;
            
                        final String path = dlg.path+"/"+dlg.item.name;
                        
                        if(!project_dcp){ 
                            field_poster.setValue(path);
                            but_show_poster.setVisible(true);
                            hbox_post.forceLayout();
                        }
                        else {
                            GWTServiceAsync.instance.getProjectName(path, new CallbackWithFailureDialog<String>("Не удалось добавить папку проекта") {

                                @Override
                                public void onFailure(Throwable caught) {
                                    super.onFailure(caught); 
                                }
                                
                                @Override
                                public void onSuccess(String result) {
                                      field_dir.setValue(path);
                                      field_prog_name.setValue(result);
                                      fieldLabel_prog_name.setVisible(true);
                                      needParentLayout();
                                }
                            }); 
//                             field_dir.setValue(path);
//                                field_prog_name.setValue("test");
//                                fieldLabel_prog_name.setVisible(true);
//                                needParentLayout();
                        
                       
                        }
                            
                        
                            
                     }
                 });
  
            }
        };
        but_add_dir.addSelectHandler(handler);
        but_add_poster.addSelectHandler(handler);
        
        
       
      
      
    
 
         vert_global.add(new Label(),new VerticalLayoutContainer.VerticalLayoutData(-1,-1,new Margins(5)));
         
        
         ToolBar bar = new ButtonBar();
         bar.setSpacing(15);
         bar.add(new FillToolItem());
         final TextButton but_send = new TextButton("Отправить",Resources.INSTANCE.globe_24());
         final TextButton but_cancel =   new TextButton("Отмена",Resources.INSTANCE.cross_24());
        
         but_send.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                
                
                if(!validateField(field_rus) 
                       
                        || !validateField(field_dir)
                        )
                {
                        Info.display("Внимание","<font color='red'>Не все поля заполнены</font>");
                        return;
                }
                but_send.setEnabled(false);
                but_cancel.setEnabled(false);
                final NewProject project = new NewProject();
                project.rus_name = field_rus.getCurrentValue();
                project.orig_name = field_orig.getCurrentValue();
                project.project_name = field_prog_name.getCurrentValue();
                project.project_dir = field_dir.getCurrentValue();
                project.poster_path = field_poster.getCurrentValue();
                project.anotation = area_description.getCurrentValue();
                ArrayList<String> client_id_list = new ArrayList<String>(selectTheaterDialog.checkedItems.values());
        
               GWTServiceAsync.instance.registerProject(project,client_id_list, new CallbackWithFailureDialog<Boolean>("Не удалось зарегистрировать проект") {

                    @Override
                    public void onFailure(Throwable caught) {
                        super.onFailure(caught); //To change body of generated methods, choose Tools | Templates.
                         but_send.setEnabled(true);
                       but_cancel.setEnabled(true);
                    }
                                
                    @Override
                    public void onSuccess(Boolean result) {

                        if(result==false)
                        {
                            AlertMessageBox alert = new AlertMessageBox("Внимание", "Проект уже ранее был добавлен");
                            alert.show();
                        }
                        else {
                            Info.display("Инфо","Проект зарегистрирован<br>Смотри вкладку 'Мониторинг'");
                        }
                        but_send.setEnabled(true);
                        but_cancel.setEnabled(true);

                    }
                });
                
                
            }
          });
            but_cancel.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                onCancelClick();  
            }
        });
           bar.add(but_send);
          bar.add(but_cancel);
            bar.add(new FillToolItem());
           vert_global.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
      
         center.add(cont);
        
    }
    
    
}
