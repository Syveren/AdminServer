/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.theater;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.ComboBoxWithClearBut;
import org.kino.client.MainEntryPoint;
import org.kino.client.Resources;
import org.kino.client.monitoring.CurrentProjectsGrid;
import org.kino.client.rpc.GWTServiceAsync;

/**
 *
 * @author kirio
 */
public class TheaterW implements IsWidget{

    BorderLayoutContainer border = new BorderLayoutContainer();
    ComboBoxWithClearBut combo_filter_countryes =   new ComboBoxWithClearBut(); ;
    ComboBoxWithClearBut combo_filter_citys =   new ComboBoxWithClearBut();
    TheaterGrid grid = new TheaterGrid();
    TheaterInfo info = new TheaterInfo();
    CurrentProjectsGrid currentProjectsGrid;
    @Override 
    public Widget asWidget() {
         return border;
    }

    public void needLayout(){
    }
    public TheaterW() {
        final VerticalLayoutContainer vert_left = new VerticalLayoutContainer();
        
        BorderLayoutContainer.BorderLayoutData leftData = new BorderLayoutContainer.BorderLayoutData(400);
        leftData.setSplit(true);
        
        final ContentPanel contLeft = new ContentPanel();
 
        ContentPanel contCenter = new ContentPanel();
        border.setWestWidget(contLeft,leftData);
        border.setCenterWidget(contCenter);
         
         ToolBar bar = new ToolBar();
         
         TextButton but_refresh = new TextButton("Обновить", Resources.INSTANCE.refresh());
         
         but_refresh.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
               grid.loadData(true);
               border.forceLayout();
               needLayout();
              
            }
        });
         TextButton but_add_theater = new TextButton("Добавить",Resources.INSTANCE.plus_16());
         but_add_theater.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
             
                
               final CreateThreaderDialog dlg = new CreateThreaderDialog();
               dlg.show();
                dlg.addHideHandler(new HideEvent.HideHandler() {
                   @Override
                   public void onHide(HideEvent event) {
                      if(dlg.data==null)
                          return;
                      //grid.loadData(true);
                   }
               });
            }
         });
         
         final TextButton but_edit_theater = new TextButton("Изменить",Resources.INSTANCE.pencil());
         but_edit_theater.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                 if(item==null)
                        return;
                    
               final EditTheaterDialog dlg = new EditTheaterDialog(item);
               dlg.show();
               dlg.addHideHandler(new HideEvent.HideHandler() {
                   @Override
                   public void onHide(HideEvent event) {
                      if(dlg.edited==false)
                          return;
                       grid.grid.getStore().update(item);
                       info.setData(item);
                      
                   }
               });
            }
         });
         
         
         
       /* final TextButton but_remove_theater = new TextButton("Удалить",Resources.INSTANCE.cross_16());
         but_remove_theater.addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final Item item = grid.grid.getSelectionModel().getSelectedItem();
                 if(item==null)
                        return;
      
                    
                   }
               });
            }
         });*/
         
         bar.add(but_refresh);
         bar.add(but_add_theater);
        bar.add(but_edit_theater);
         vert_left.add(bar,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
         
 
          ContentPanel filterPanel= new ContentPanel();
          filterPanel.setHeadingText("фильтр");
          filterPanel.setCollapsible(true);
          //fieldSet.setExpanded(false);
          
      //    filterPanel.add(vert_sub,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
          
          class CollapseExpadeHandler implements CollapseEvent.CollapseHandler,ExpandEvent.ExpandHandler{

             @Override
             public void onCollapse(CollapseEvent event) {
                   border.forceLayout();
             }

             @Override
             public void onExpand(ExpandEvent event) {
                    border.forceLayout();
             }
          };
         CollapseExpadeHandler collapseExpadeHandler = new CollapseExpadeHandler();
         filterPanel.addCollapseHandler(collapseExpadeHandler);
         filterPanel.addExpandHandler(collapseExpadeHandler);
         filterPanel.setAnimCollapse(false);
         
      
 
         CallbackWithFailureDialog<ArrayList<ArrayList<String>>> asyncCallback = new CallbackWithFailureDialog<ArrayList<ArrayList<String>>>("Не удалось получить список городов и стран") {

            @Override
            public void onSuccess(ArrayList<ArrayList<String>> result) {
           
                  combo_filter_countryes.combo.getStore().replaceAll(result.get(0));
                  combo_filter_citys.combo.getStore().replaceAll(result.get(1));
  
            }
        };
        GWTServiceAsync.instance.getCountriesAndCitys(asyncCallback);
         
 

        
       //vert_left.add(new ComboBoxWithClearBut().asWidget(),new VerticalLayoutContainer.VerticalLayoutData(1,-1,new Margins(10, 10, 10, 10)));
        // vert_left.add(new ComboBoxWithClearBut().asWidget(),new VerticalLayoutContainer.VerticalLayoutData(1,-1) );
       vert_left.add(new FieldLabel(combo_filter_citys.asWidget() ,"Город"),new VerticalLayoutContainer.VerticalLayoutData(1,-1,new Margins(10, 10, 10, 10)));
        vert_left.add(new FieldLabel(combo_filter_countryes.asWidget(),"Страна"),new VerticalLayoutContainer.VerticalLayoutData(1,-1,new Margins(0, 10, 0, 10)));
        // vert_left.add(new Label(),new VerticalLayoutContainer.VerticalLayoutData(-1,-1));
        // vert_left.add(new FieldLabel(combo_filter_citys.asWidget(),"Город"),new VerticalLayoutContainer.VerticalLayoutData(1,-1));
        // vert_left.add(new Label(),new VerticalLayoutContainer.VerticalLayoutData(-1,-1));
         //vert_left.add(vert_sub,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
         vert_left.add(grid.asWidget(),new VerticalLayoutContainer.VerticalLayoutData(1,1,new Margins(10, 0, 0, 0)));
         vert_left.add(grid.pager,new VerticalLayoutContainer.VerticalLayoutData(1,-1));
 
         contLeft.add(vert_left);
         contCenter.add(info);
         contLeft.setHeaderVisible(false);
         //contLeft.setHeadingText("Кинотеатры");
         contCenter.setHeaderVisible(false);
         
         grid.grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<TheaterItem>() {

            @Override
            public void onSelectionChanged(SelectionChangedEvent<TheaterItem> event) {
                TheaterItem selectedItem = grid.grid.getSelectionModel().getSelectedItem();
                but_edit_theater.setEnabled(selectedItem!=null);
               // but_remove_theater.setEnabled(selectedItem!=null);
                if(selectedItem==null)
                    return;
                info.setData(selectedItem);
                 currentProjectsGrid.setCurrentUser(selectedItem.main.uniqIdent);
                 
            }
        });
        
    }
    
     class TheaterInfo implements IsWidget{ 
         
         
         TextField field_name = new TextField();
         TextField field_unique_ident = new TextField();
         TextField field_n_server = new TextField();
         TextField field_hdd1 = new TextField();
         TextField field_hdd2 = new TextField();
          TextField field_bios_pass = new TextField();
        
         TextField field_sys_version = new TextField();
         TextField field_sys_ip = new TextField();
         TextField field_sys_free_space = new TextField();
         
         
         TextField field_county = new TextField();
         TextField field_city = new TextField();
         TextField field_index = new TextField();
         TextField field_street = new TextField();
         TextField field_house = new TextField();
         
          TextField field_urid_county = new TextField();
         TextField field_urid_city = new TextField();
         TextField field_urid_index = new TextField();
         TextField field_urid_street = new TextField();
         TextField field_urid_house = new TextField();
         TextField field_urid_phone = new TextField();
         TextField field_urid_fax = new TextField();
         TextField field_urid_mail = new TextField();
         
        TextField field_urid_comp_name = new TextField();
        TextField field_urid_director = new TextField();
        TextField field_urid_inn = new TextField();
        TextField field_urid_kpp = new TextField();
        TextField field_urid_ogrn = new TextField();
        TextField field_urid_rs = new TextField();
        TextField field_urid_bank = new TextField();
        TextField field_urid_bik = new TextField();
       
       
         TextField field_contractN = new TextField();
         TextField field_contractDate = new TextField();
         VerticalLayoutContainer vert = new VerticalLayoutContainer();
         
         ComboBox<TheaterItem.ContactData> combo_posts;
         TextField field_fio = new TextField();
         TextField field_mail = new TextField();
         TextField field_phone = new TextField();
         
         HTML lab_client_total_space = new HTML();
         HTML lab_client_free_space = new HTML();
         HTML lab_space_request = new HTML();
         //TextField field_phone = new TextField();
         //TextField field_phone = new TextField();
         //TextField field_phone = new TextField();
         
 
         
        @Override
        public Widget asWidget() {
             return vert;
        }

        public TheaterInfo() {
            
            
            FieldSet fieldSet_main = new FieldSet();
            fieldSet_main.setData("id", "main");
            fieldSet_main.setHeadingText("Основное");
            fieldSet_main.setCollapsible(true);
            VerticalLayoutContainer vert0= new VerticalLayoutContainer();
            fieldSet_main.add(vert0);
            vert0.add(new FieldLabel(field_name,"Название"));
            vert0.add(new FieldLabel(field_unique_ident,"Уникальный идентификатор"));
             vert0.add(new FieldLabel(field_n_server,"Номер сервера"));
            vert0.add(new FieldLabel(field_hdd1,"HDD1"));
            vert0.add(new FieldLabel(field_hdd2,"HDD2"));
            vert0.add(new FieldLabel(field_bios_pass,"BIOS пароль"));
            
            vert0.add(new FieldLabel(field_sys_version,"Версия клиента"));
             vert0.add(new FieldLabel(field_sys_ip,"vpn ip"));
             vert0.add(new FieldLabel(field_sys_free_space,"Свободное место"));
            
            
            
            
            vert.add(fieldSet_main);
            
            FieldSet fieldSet_address = new FieldSet();
             fieldSet_address.setData("id", "address");
            fieldSet_address.setHeadingText("Адрес расположения");
            fieldSet_address.setCollapsible(true);
            VerticalLayoutContainer vert1= new VerticalLayoutContainer();
            fieldSet_address.add(vert1);
            vert1.add(new FieldLabel(field_county,"Страна"));
            vert1.add(new FieldLabel(field_city,"Город"));
            vert1.add(new FieldLabel(field_index,"Индекс"));
            vert1.add(new FieldLabel(field_street,"Улица"));
            vert1.add(new FieldLabel(field_house,"Дом"));
            
            vert.add(fieldSet_address);
            
            FieldSet fieldSet_urid_address = new FieldSet();
            fieldSet_urid_address.setData("id", "urid_address");
            fieldSet_urid_address.setHeadingText("Юридический адрес");
            fieldSet_urid_address.setCollapsible(true);
            VerticalLayoutContainer vert12= new VerticalLayoutContainer();
            fieldSet_urid_address.add(vert12);
            vert12.add(new FieldLabel(field_urid_county,"Страна"));
            vert12.add(new FieldLabel(field_urid_city,"Город"));
            vert12.add(new FieldLabel(field_urid_index,"Индекс"));
            vert12.add(new FieldLabel(field_urid_street,"Улица"));
            vert12.add(new FieldLabel(field_urid_house,"Дом"));
            vert12.add(new FieldLabel(field_urid_phone,"Телефон"));
            vert12.add(new FieldLabel(field_urid_fax,"Факс"));
            vert12.add(new FieldLabel(field_urid_mail,"mail"));
            vert.add(fieldSet_urid_address);
            
            
            FieldSet fieldSet_urid_info = new FieldSet();
            fieldSet_urid_info.setData("id", "urid_info");
            fieldSet_urid_info.setHeadingText("Юридическое лицо");
            fieldSet_urid_info.setCollapsible(true);
            VerticalLayoutContainer vert13= new VerticalLayoutContainer();
            fieldSet_urid_info.add(vert13);
             vert13.add(new FieldLabel(field_urid_comp_name,"Наименование"));
             vert13.add(new FieldLabel(field_urid_director,"Генеральный директор"));
             vert13.add(new FieldLabel(field_urid_inn,"ИНН"));    
             vert13.add(new FieldLabel(field_urid_kpp,"КПП"));
             vert13.add(new FieldLabel(field_urid_ogrn,"ОГРН"));
             vert13.add(new FieldLabel(field_urid_rs,"р/с"));    
             vert13.add(new FieldLabel(field_urid_bank,"Банк"));
             vert13.add(new FieldLabel(field_urid_bik,"Бик банка"));
            vert.add(fieldSet_urid_info);
            
             FieldSet fieldSet_contract = new FieldSet();
              fieldSet_contract.setData("id", "contract");
            fieldSet_contract.setHeadingText("Договор");
            fieldSet_contract.setCollapsible(true);
            VerticalLayoutContainer vert2= new VerticalLayoutContainer();
            fieldSet_contract.add(vert2);
            vert2.add(new FieldLabel(field_contractN,"№ контракта"));
             vert2.add(new FieldLabel(field_contractDate,"Дата контракта"));
             vert.add(fieldSet_contract);
             
             
             ListStore<TheaterItem.ContactData> comboList =new ListStore<TheaterItem.ContactData>(new ModelKeyProvider<TheaterItem.ContactData>() {

                @Override
                public String getKey(TheaterItem.ContactData item) {
                     return item.id+" ";
                }
            });
             combo_posts = new ComboBox<TheaterItem.ContactData>(comboList,new  LabelProvider<TheaterItem.ContactData>() {

                @Override
                public String getLabel(TheaterItem.ContactData item) {
                    return item.post;
                }
            });
            FieldSet fieldSet_contacts = new FieldSet();
             fieldSet_contacts.setData("id", "contacts");
            fieldSet_contacts.setHeadingText("Контакты");
            fieldSet_contacts.setCollapsible(true);
            VerticalLayoutContainer vert3= new VerticalLayoutContainer();
            fieldSet_contacts.add(vert3);
            vert3.add(new FieldLabel(combo_posts,"Должность"));
            vert3.add(new FieldLabel(field_fio,"ФИО"));
            vert3.add(new FieldLabel(field_mail,"e-mail"));
            vert3.add(new FieldLabel(field_phone,"Телефон"));
            TextButton but_add_contact = new TextButton("Добавить контакт",Resources.INSTANCE.plus_16());
            final  TextButton but_remove_contact = new TextButton("Удалить контакт",Resources.INSTANCE.cross_16());
            final  TextButton but_edit_contact = new TextButton("Изменить контакт",Resources.INSTANCE.pencil());
           // but_remove_contact.setEnabled(false);
           // but_edit_contact.setEnabled(false);
            HBoxLayoutContainer hor_but = new HBoxLayoutContainer();
            hor_but.add(but_add_contact,new BoxLayoutContainer.BoxLayoutData(new Margins(0, 10, 0, 0)));
            hor_but.add(but_edit_contact,new BoxLayoutContainer.BoxLayoutData(new Margins(0, 10, 0, 0)));
            hor_but.add(but_remove_contact,new BoxLayoutContainer.BoxLayoutData(new Margins(0, 10, 0, 0)));
 
            vert3.add(hor_but);
            but_add_contact.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    final TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                    if(item==null){
                        Info.display("Внимание","Выберите кинотеатр");
                        return;
                    }
                    final CreateContactDialog dlg = new CreateContactDialog(item.id);
                    dlg.show();
                    dlg.addHideHandler(new HideEvent.HideHandler() {

                        @Override
                        public void onHide(HideEvent event) {
                           
                            if(dlg.data==null)
                                return;
                            TheaterItem.ContactData new_data = dlg.data;
                            item.contactList.add(new_data);
                            combo_posts.getStore().add(new_data);
                            
                            
                            if(combo_posts.getStore().size()==1)
                            {
                                combo_posts.setValue(new_data);
                                field_fio.setValue(new_data.fio);
                                field_mail.setValue(new_data.email);
                                field_phone.setValue(new_data.phone);
                            }  
                            Info.display("Информация","Запись добавлена");
                            
                        }
                    });
                }
            });
            
            but_edit_contact.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    final TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                    if(item==null){
                        Info.display("Внимание","Выберите кинотеатр");
                        return;
                    }
                    final TheaterItem.ContactData value = combo_posts.getCurrentValue();
                    if(value==null)
                        return;
                    final EditContactDialog dlg = new EditContactDialog(value);
                    dlg.show();
                    dlg.addHideHandler(new HideEvent.HideHandler() {
                        @Override
                        public void onHide(HideEvent event) {
                           if(dlg.edited==false)
                               return;
                            combo_posts.clear();
                            combo_posts.setValue(value);
                            field_fio.setValue(value.fio);
                            field_mail.setValue(value.email);
                            field_phone.setValue(value.phone);
      

                        }
                    });
                }
            });
            
            but_remove_contact.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    final TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                    if(item==null){
                        Info.display("Внимание","Выберите кинотеатр");
                        return;
                    }
                    final TheaterItem.ContactData value = combo_posts.getCurrentValue();
                    if(value==null)
                        return;
                    
                    if (!Window.confirm("Вы уверены что хотите удалить данный контакт?"))  
                        return;
                    GWTServiceAsync.instance.removeContact( value.id, new CallbackWithFailureDialog<Void>("Не удалось удалить контакт") {
                        
                        @Override
                        public void onSuccess(Void result) {
                            
                            
                            combo_posts.getStore().remove(value);
                            item.contactList.remove(value);
                            
                            List<TheaterItem.ContactData> all_contacts = combo_posts.getStore().getAll();
                            if(all_contacts.isEmpty())
                            {
                                 combo_posts.clear();
                                 field_fio.clear();
                                 field_mail.clear();
                                 field_phone.clear();
                            }
                            else {
                                 TheaterItem.ContactData firstcontact = all_contacts.get(0);
                                 combo_posts.setValue(firstcontact);
                                 field_fio.setValue(firstcontact.fio);
                                 field_mail.setValue(firstcontact.email);
                                 field_phone.setValue(firstcontact.phone);
                            
                            }
                            Info.display("Информация","Запись удалена");
                        }
                    });
                    
             
                }
            });
            
            
            combo_posts.setTriggerAction(ComboBoxCell.TriggerAction.ALL);
            combo_posts.addSelectionHandler(new SelectionHandler<TheaterItem.ContactData>() {
                @Override
                public void onSelection(SelectionEvent<TheaterItem.ContactData> event) {
                    TheaterItem.ContactData item = event.getSelectedItem();
                    field_fio.setValue(item.fio);
                    field_mail.setValue(item.email);
                    field_phone.setValue(item.phone);
                   
                }
            });
            vert.add(fieldSet_contacts);

            
             /// Блок текущие проекты
            FieldSet fieldSet_current_projects = new FieldSet();
            fieldSet_current_projects.setData("id", "current_projects");
            fieldSet_current_projects.setHeadingText("Текущие проекты");
            fieldSet_current_projects.setCollapsible(true);
 
            currentProjectsGrid = new CurrentProjectsGrid();
            fieldSet_current_projects.add(currentProjectsGrid);
            vert.add(fieldSet_current_projects);
            currentProjectsGrid.asWidget().setHeight(150+"px");
            
           
            
            FieldSet fieldSet_optional = new FieldSet();
            fieldSet_optional.setData("id", "optional");   
            fieldSet_optional.setHeadingText("Дополнительно");
            fieldSet_optional.setCollapsible(true);
            VerticalLayoutContainer vert4= new VerticalLayoutContainer();
            fieldSet_optional.add(vert4);
            
          
         
            if(Cookies.isCookieEnabled()){
                final  FieldSet[] fieldSetArray = new FieldSet[]{
                fieldSet_main,fieldSet_address,fieldSet_urid_address,
                fieldSet_urid_info,fieldSet_contract,fieldSet_contacts,fieldSet_optional
                };
                
                CollapseEvent.CollapseHandler collapseHandler = new CollapseEvent.CollapseHandler() {
                    @Override
                    public void onCollapse(CollapseEvent event) {
                        Component source = event.getSource();
                        String data = (String) source.getData("id");
                        if(data!=null)
                            Cookies.removeCookie("gui_theater_info_expand_"+data);
                        
                    }
                };
                ExpandEvent.ExpandHandler expandHandler = new ExpandEvent.ExpandHandler() {

                    @Override
                    public void onExpand(ExpandEvent event) {
                        FieldSet source = (FieldSet)event.getSource();
                        String data = (String) source.getData("id");
                        if(data!=null)
                            Cookies.setCookie("gui_theater_info_expand_"+data,"true"); 
                        source.forceLayout();
                    }
                    
                };    
                for(FieldSet cont:fieldSetArray)
                {
                    cont.addExpandHandler(expandHandler);
                    cont.addCollapseHandler(collapseHandler);
                    if(Cookies.getCookie("gui_theater_info_expand_"+cont.getData("id"))!=null)
                        cont.expand();
                    else 
                        cont.collapse();
                }
                //fieldSet_main.setExpanded(Cookies.getCookie(null));
            
            
            }
            /*HorizontalPanel hor = new HorizontalPanel();
            hor.add(lab_client_total_space);
            hor.add(lab_client_free_space);
            vert4.add(hor,new VerticalLayoutContainer.VerticalLayoutData(1, -1));*/
           // vert4.add(new TextButton("Обновить"));
           // vert4.add(new TextButton("Очистка"));
           
            
            /*HorizontalPanel horbut = new HorizontalPanel();
            horbut.add(new TextButton("Запросить"));*/
            
           // horbut.add(new TextButton("Очистка"));
            TextButton but_clean = new TextButton("Удаление проектов",Resources.INSTANCE.trash());
            but_clean.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                    if(item==null){
                        Info.display("Внимание","Выберите кинотеатр");
                        return;
                    }
                    UrlBuilder urlb = Window.Location.createUrlBuilder();
                    urlb.setParameter("clean", item.id+"");
                    Window.open(urlb.buildString(),"_blank","");
                }
            });
            TextButton but_rate_limit = new TextButton("Ограничение скорости",Resources.INSTANCE.rate_timer());
            but_rate_limit.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                    if(item==null){
                        Info.display("Внимание","Выберите кинотеатр");
                        return;
                    }
                    RateSchedulerDialog dlg = new RateSchedulerDialog(item.main.uniqIdent);
                    dlg.show();
                
                }
            });
            
            TextButton but_contract_generate = new TextButton("Сгенерировать договор",Resources.INSTANCE.doc_word());
            but_contract_generate.addSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    TheaterItem item = grid.grid.getSelectionModel().getSelectedItem();
                    if(item==null){
                        Info.display("Внимание","Выберите кинотеатр");
                        return;
                    }
                   
                    Window.open(GWT.getHostPageBaseURL() + "contractgenerator?id="+item.id, "_self", "enabled");
                
                }
            });

            
            HBoxLayoutContainer hor = new HBoxLayoutContainer();
            hor.add(but_clean,new BoxLayoutContainer.BoxLayoutData(new Margins(0, 10, 0, 0)));
            hor.add(but_rate_limit,new BoxLayoutContainer.BoxLayoutData(new Margins(0, 10, 0, 0)));
            hor.add(but_contract_generate,new BoxLayoutContainer.BoxLayoutData(new Margins(0, 10, 0, 0)));
            vert4.add(hor); 
            
            vert.add(fieldSet_optional);
            
    
            
           // vert.add(fieldSet4);
            
            
            vert.setScrollMode(ScrollSupport.ScrollMode.AUTO);
            //vert.add(fieldSet4);
            for(ValueBaseField field:MainEntryPoint.getFields(vert)){
                field.setWidth(350); 
            
            }
           /*  field_unique_ident.setWidth(350);
          field_county.setWidth(350);
           field_city.setWidth(350);
          field_name.setWidth(350);
          
           field_index.setWidth(350);
          field_street.setWidth(350);
          field_house.setWidth(350);
          field_contractN.setWidth(350);
          field_contractDate.setWidth(350);
     
         
            combo_posts.setWidth(350);
            field_fio.setWidth(350);
            field_mail.setWidth(350);
            field_phone.setWidth(350);*/
  
        }
       
        void setData(TheaterItem theater){
           
            field_name.setValue(theater.main.name);
            field_unique_ident.setValue(theater.main.uniqIdent);
            field_n_server.setValue(theater.main.n_server);
            field_hdd1.setValue(theater.main.hdd1);
            field_hdd1.setValue(theater.main.hdd2);
            field_hdd1.setValue(theater.main.biospass);
            
            field_sys_version.setValue(theater.sysInfo.version);
            field_sys_ip.setValue(theater.sysInfo.vpn_ip);
            if(theater.sysInfo.free_space!=0){
                  NumberFormat nf =  NumberFormat.getFormat("#.##");
                  double val;
                          String suffix;
                  if(theater.sysInfo.free_space<1024){
                              val = theater.sysInfo.free_space;
                              suffix = " Байт";
                   }
                    else if(theater.sysInfo.free_space<(1024*1024)){
                        val = ((double)theater.sysInfo.free_space)/(1024);
                        suffix = " КБайт";
                    }
                    else if(theater.sysInfo.free_space<(1024*1024*1024)){
                        val = ((double)theater.sysInfo.free_space)/(1024*1024);
                        suffix = " MБайт";
                    }
                    else {
                        val = ((double)theater.sysInfo.free_space)/(1024*1024*1024);
                        suffix = " ГБайт";

                    }
                    double percent = (double)theater.sysInfo.free_space*100/theater.sysInfo.total_space;
                    field_sys_free_space.setValue(nf.format(val) +  suffix+" ("+nf.format(percent)+"%)");
            }
            else {
                field_sys_free_space.setValue(null);
            }
            
            
            
           field_county.setValue(theater.address.county);
           field_city.setValue(theater.address.city);
           
           field_index.setValue(theater.address.index);
           field_street.setValue(theater.address.street);
           field_house.setValue(theater.address.house);
           
           field_contractN.setValue(theater.main.contractNumber);
           field_contractDate.setValue(theater.main.contractDate==null?null: theater.main.contractDate.toString());
           
         
           field_urid_county.setValue(theater.uridAdress.county);
           field_urid_city.setValue(theater.uridAdress.city);
           field_urid_index.setValue(theater.uridAdress.index);
           field_urid_street.setValue(theater.uridAdress.street);
           field_urid_house.setValue(theater.uridAdress.house);
           field_urid_phone.setValue(theater.uridAdress.index);
           field_urid_fax.setValue(theater.uridAdress.street);
           field_urid_mail.setValue(theater.uridAdress.house);
           
           field_urid_comp_name.setValue(theater.uridInfo.name);
           field_urid_director.setValue(theater.uridInfo.dir_fio);
           field_urid_ogrn.setValue(theater.uridInfo.ogrn);
           field_urid_inn.setValue(theater.uridInfo.inn);
           field_urid_kpp.setValue(theater.uridInfo.kpp);
           field_urid_ogrn.setValue(theater.uridInfo.ogrn);
           field_urid_rs.setValue(theater.uridInfo.rs);
           field_urid_bank.setValue(theater.uridInfo.bank);
           field_urid_bik.setValue(theater.uridInfo.bik);
           
           
           field_hdd1.setValue(theater.main.hdd1);
           field_hdd2.setValue(theater.main.hdd2);
           field_bios_pass.setValue(theater.main.biospass);
           
           
           combo_posts.clear();
           field_fio.clear();
           field_phone.clear();
           field_mail.clear();
           combo_posts.getStore().replaceAll(theater.contactList);
           if(!theater.contactList.isEmpty()){
                TheaterItem.ContactData data = theater.contactList.get(0);
                combo_posts.setValue(data);
                field_fio.setValue(data.fio);
                field_mail.setValue(data.email);
                field_phone.setValue(data.phone);
                
           }
           
        }
     }
     
 
     
    public static class TheaterMainData implements IsSerializable{
        public Integer id;
        public String county;
        public String city;
        public String name;
        public String uniqIdent;

        public TheaterMainData() {
        }

        public TheaterMainData(Integer id, String county, String city, String name, String uniqIdent) {
            this.id = id;
            this.county = county;
            this.city = city;
            this.name = name;
            this.uniqIdent = uniqIdent;
        }
        
     
     }
    
   
    
  
    class TheaterGrid implements IsWidget{
        int totalSize = -1;
        @Override
        public Widget asWidget() {
             return grid;
        }
        Grid<TheaterItem> grid;
        boolean loaded = false;
       PagingLoader<MyLoadConfig, PagingLoadResult<TheaterItem>> loader ;
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
              
           ColumnConfig<TheaterItem,TheaterItem> col_status = new ColumnConfig<TheaterItem,TheaterItem>(new ValueProvider<TheaterItem,TheaterItem>() {
                @Override
                public TheaterItem getValue(TheaterItem object) {
                     return object ;
                }

                @Override
                public void setValue(TheaterItem object, TheaterItem value) {
                    
                }

                @Override
                public String getPath() {
                    return "time_last_active_sec";
                }
            },50,"Статус");
            List<ColumnConfig<TheaterItem,?>> list = new ArrayList<ColumnConfig<TheaterItem,?>>();
            list.add(new RowNumberer<TheaterItem>(new IdentityValueProvider<TheaterItem>()));
            list.add(col_city);
            list.add(col_name);
            list.add(col_status);
           // for(ColumnConfig col:list)
            //    col.setSortable(false);
            
            col_status.setCell(new AbstractCell<TheaterItem>() {
                
                @Override
                public void render(Cell.Context context, TheaterItem value, SafeHtmlBuilder sb) {
                    ImageResource res  = (value.status? Resources.INSTANCE.status_green():Resources.INSTANCE.status_red());
                    sb.appendHtmlConstant("<img src='"+res.getSafeUri().asString()+"' /> ");
                    if((value.sysInfo.total_space!=0) && 
                            value.sysInfo.need_to_load >= value.sysInfo.free_space)
                            sb.appendHtmlConstant("<img class='warning' src='"+Resources.INSTANCE.exclamation().getSafeUri().asString()+"' />");
                }
            });
            ColumnModel<TheaterItem> cm = new ColumnModel<TheaterItem>(list);
            grid = new Grid<TheaterItem>(store,cm  ){
                 @Override
                protected void onAfterFirstAttach() {
                    super.onAfterFirstAttach(); //To change body of generated methods, choose Tools | Templates.
                    loadData(true);

                }
            
            };
           grid.addCellClickHandler(new CellClickEvent.CellClickHandler() {

                @Override
                public void onCellClick(CellClickEvent event) {
                    Element as = Element.as(event.getEvent().getEventTarget());
                    if("img".equalsIgnoreCase(as.getTagName()) && "warning".equals(as.getClassName()))
                         Info.display("Внимание","На диске мало места");
                }
            });
           //  grid.getView().getHeader().setVisible(false);
             
             
              RpcProxy<MyLoadConfig, PagingLoadResult<TheaterItem>> proxy =   new RpcProxy<MyLoadConfig, PagingLoadResult<TheaterItem>>() {
           
          
                @Override
                public void load(MyLoadConfig loadConfig, AsyncCallback<PagingLoadResult<TheaterItem>> callback) {
                    if(grid_data_request!=null)
                        grid_data_request.cancel();
  
                    loadConfig.setFilter("country", combo_filter_countryes.getCurrentValue());
                    loadConfig.setFilter("city", combo_filter_citys.getCurrentValue());
                    grid_data_request = GWTServiceAsync.instance.getTheaterGridItems( totalSize,loadConfig,callback);
 
                }
            };
       
        loader  = new PagingLoader<MyLoadConfig, PagingLoadResult<TheaterItem>>(proxy) 
        {

             @Override
             protected void onLoadSuccess(MyLoadConfig loadConfig, PagingLoadResult<TheaterItem> result) {
                 super.onLoadSuccess(loadConfig, result);
                 
                 if(totalSize!=result.getTotalLength())
                    totalSize = result.getTotalLength();
                  needLayout();
             }


        };
        loader.addLoadHandler(new LoadResultListStoreBinding<MyLoadConfig,TheaterItem, PagingLoadResult<TheaterItem>>(store));
        loader.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                        if(status_request!=null)
                        status_request.cancel();
                       // updateStatusTimer.cancel();
                       // updateStatusTimer.schedule(TIMER_REFRESH_TIMEOUT); 
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
        
            
          
            
      
        
            
            
            final CallbackWithFailureDialog<ArrayList<TheaterItem>> callback = new CallbackWithFailureDialog<ArrayList<TheaterItem>>("Не удалось запросить статус") {

                   

                    @Override
                    public void onSuccess(ArrayList<TheaterItem> result) {
                         for(TheaterItem item:result){
                             TheaterItem findModelWithKey = store.findModelWithKey(String.valueOf(item.id));
                             if(findModelWithKey==null)
                                 continue;
                             findModelWithKey.status=item.status;
                        }
                        store.fireEvent(new StoreUpdateEvent<TheaterItem>(store.getAll()));
                       // if(border.isVisible())
                        //    updateStatusTimer.schedule(TIMER_REFRESH_TIMEOUT);

                    }
                };
            
                updateStatusTimer = new Timer() {
                    @Override
                    public void run() {
                       // status_request = GWTServiceAsync.instance.getTheaterStatus(callback);

                }
                };
                
                
                
                
                
                
                 border.addShowHandler(new ShowEvent.ShowHandler() {
                @Override
                public void onShow(ShowEvent event) {
                    if(border.isVisible() && grid.isVisible()){
                         
                        
                        if(loaded==false)
                        {
                            loaded = true;
                            loadData(true);
                        }
                        else {
                            
                            if(status_request!=null)
                                status_request.cancel();
                            updateStatusTimer.cancel();
                           // updateStatusTimer.schedule(TIMER_REFRESH_TIMEOUT);
                        }
                    }
                   
                }});    
               
                 
            
        }
        static final int PAGE_SIZE = 30;
        PagingToolBar pager;
        final Timer updateStatusTimer;
        void loadData(boolean refreshTotalSize){
            
                if(status_request!=null)
                    status_request.cancel();
                updateStatusTimer.cancel();
                if(refreshTotalSize==true)
                    totalSize=-1;
                loader.load();
                
              /*  final CallbackWithFailureDialog<ArrayList<TheaterW.Item>> asyncCallback = new CallbackWithFailureDialog<ArrayList<Item>>("Не удалось получить информацию о кинотеатрах") {
                @Override
                public void onSuccess(ArrayList<Item> result) {
                     grid.getStore().replaceAll(result);
                     updateStatusTimer.schedule(TIMER_REFRESH_TIMEOUT);
                    }
                };
                GWTServiceAsync.instance.getTheaterGridItems(combo_filter_countryes.getCurrentValue(),combo_filter_citys.getCurrentValue(),asyncCallback);*/
                
        }
        Request status_request;
        Request grid_data_request;
    }
    
}
