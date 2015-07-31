/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.theater;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.info.Info;
import java.util.Iterator;
import java.util.List;
import org.kino.client.CallbackWithFailureDialog;
import org.kino.client.rpc.GWTServiceAsync;

/**
 *
 * @author kirio
 */
public class CreateThreaderDialog  extends Dialog{
     TheaterItem data;
    
     CardLayoutContainer  card = new  CardLayoutContainer();
     
   
    
    final FieldLabel createLabel(Widget widget,String text,boolean important){
        if(!important)
            return new FieldLabel(widget, text);
        else 
        {
            FieldLabel lab = new FieldLabel(widget);
            lab.setHTML("<font color=red>*</font> "+SafeHtmlUtils.htmlEscape(text));
            return lab;
        
        }
    }
    final VBoxLayoutContainer lcwest;
    public CreateThreaderDialog() {
        setModal(true);
        setSize("500px", "500px");
        setBodyStyle("backgroundColor:white");
  
 
        BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
        
        add(borderLayoutContainer, new MarginData(0, 0, 0, 0));

        
       
        setHeadingText("Добавить кинотеатр");
        setPredefinedButtons(Dialog.PredefinedButton.CANCEL,Dialog.PredefinedButton.YES);
       
       final TextField field_uniq_ident = new TextField(); 
       final TextField field_name = new TextField();
           
       final TextField field_n_server = new TextField(); 
       final TextField field_hdd1 = new TextField();
          
       final TextField field_hdd2 = new TextField(); 
       final TextField field_bios_pass = new TextField();
       
       VerticalLayoutContainer.VerticalLayoutData vertData = new VerticalLayoutContainer.VerticalLayoutData(1, -1);
       VerticalLayoutContainer ver_main = new VerticalLayoutContainer();
       ver_main.setAdjustForScroll(true);
       ver_main.setScrollMode(ScrollSupport.ScrollMode.AUTO);
       ver_main.add(createLabel(field_uniq_ident,"Уникальный идентификатор",true),vertData);
       ver_main.add(createLabel(field_name,"Название",true),vertData);
       ver_main.add(createLabel(field_n_server,"№ сервера",true),vertData);
       ver_main.add(createLabel(field_hdd1,"HDD1",true),vertData);
       ver_main.add(createLabel(field_hdd2,"HDD2",true),vertData);
       ver_main.add(createLabel(field_bios_pass,"Bios pass",true),vertData);
       card.add(ver_main);
     
       
       final TextField field_county = new TextField();
       final TextField field_city = new TextField();
       final TextField field_street = new TextField();
       final TextField field_house = new TextField();
       final TextField field_index = new TextField();
       
       VerticalLayoutContainer ver_address = new VerticalLayoutContainer();
        ver_address.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        ver_address.setAdjustForScroll(true);
        ver_address.add(createLabel(field_county,"Страна",true),vertData);
        ver_address.add(createLabel(field_city,"Город",true),vertData);
        ver_address.add(createLabel(field_street,"Улица",true),vertData);
        ver_address.add(createLabel(field_index,"Индекс",true),vertData);    
        ver_address.add(createLabel(field_house,"Дом",true),vertData);
  
       card.add(ver_address);
       
       
       final TextField field_urid_county = new TextField();
       final TextField field_urid_city = new TextField();
       final TextField field_urid_street = new TextField();
       final TextField field_urid_house = new TextField();
       final TextField field_urid_index = new TextField();
       final TextField field_urid_phone = new TextField();
       final TextField field_urid_fax = new TextField();
       field_urid_fax.setData("nullable", true);
       final TextField field_urid_mail = new TextField();
       field_urid_mail.setData("nullable", true);
       VerticalLayoutContainer ver_urid_address = new VerticalLayoutContainer();
        ver_urid_address.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        ver_urid_address.setAdjustForScroll(true);
        ver_urid_address.add(createLabel(field_urid_county,"Страна",true),vertData);
        ver_urid_address.add(createLabel(field_urid_city,"Город",true),vertData);
        ver_urid_address.add(createLabel(field_urid_street,"Улица",true),vertData);
        ver_urid_address.add(createLabel(field_urid_index,"Индекс",true),vertData);    
        ver_urid_address.add(createLabel(field_urid_house,"Дом",true),vertData);
        ver_urid_address.add(createLabel(field_urid_phone,"Телефон",true),vertData);
        ver_urid_address.add(createLabel(field_urid_fax,"Факс",false),vertData);    
        ver_urid_address.add(createLabel(field_urid_mail,"mail",false),vertData);
       card.add(ver_urid_address);
       
       
       
       final TextField field_urid_comp_name = new TextField();
       final TextField field_urid_director = new TextField();
       final TextField field_urid_director_rd = new TextField();
       final TextField field_urid_inn = new TextField();
       final TextField field_urid_kpp = new TextField();
       final TextField field_urid_ogrn = new TextField();
       final TextField field_urid_rs = new TextField();
       final TextField field_urid_bank = new TextField();
       final TextField field_urid_bik = new TextField();
       
        VerticalLayoutContainer ver_urid = new VerticalLayoutContainer();
        ver_urid.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        ver_urid.setAdjustForScroll(true);
        ver_urid.add(createLabel(field_urid_comp_name,"Наименование",true),vertData);
        ver_urid.add(createLabel(field_urid_director,"Генеральный директор (Фамилия И.О.)",true),vertData);
        ver_urid.add(createLabel(field_urid_director_rd,"Генеральный директор (ФИО полностью в род.падеже)",true), vertData);
        ver_urid.add(createLabel(field_urid_inn,"ИНН",true),vertData);    
        ver_urid.add(createLabel(field_urid_kpp,"КПП",true),vertData);
        ver_urid.add(createLabel(field_urid_ogrn,"ОГРН",true),vertData);
        ver_urid.add(createLabel(field_urid_rs,"р/с",true),vertData);    
        ver_urid.add(createLabel(field_urid_bank,"Банк",true),vertData);
        ver_urid.add(createLabel(field_urid_bik,"Бик банка",true),vertData);
        card.add(ver_urid);
       
        VerticalLayoutContainer ver_dogovor = new VerticalLayoutContainer();
        ver_dogovor.setScrollMode(ScrollSupport.ScrollMode.AUTO);
        ver_dogovor.setAdjustForScroll(true);
        
        final TextField field_contract_number = new TextField();
        final DateField field_contract_date = new DateField();
        
         ver_dogovor.add(createLabel(field_contract_number,"№ контракта",true),vertData);
         ver_dogovor.add(createLabel(field_contract_date,"Дата контракта",true),vertData);    
 
        
        card.add(ver_dogovor);  
       
        
         List<FieldLabel> labels = FormPanelHelper.getFieldLabels(card);
         for (FieldLabel lbl : labels) {
           lbl.setLabelAlign(FormPanel.LabelAlign.TOP);
        }
   
         
      lcwest = new VBoxLayoutContainer();

      lcwest.setPadding(new Padding(5));
 
      lcwest.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
      BoxLayoutData vBoxData = new BoxLayoutData(new Margins(5, 5, 5, 5));

      
     ValueChangeHandler handler = new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if(event.getValue()==true)
                {
                    
                    int widgetIndex = lcwest.getWidgetIndex((ToggleButton)event.getSource());
                    card.setActiveWidget(card.getWidget(widgetIndex));
                
                }
            }
        };
     
     
     
      lcwest.add( createToggleButton("Основное",handler),vBoxData);
      lcwest.add( createToggleButton("Адрес расположения",handler),vBoxData);
      lcwest.add( createToggleButton("Юридический адрес",handler),vBoxData);
      lcwest.add( createToggleButton("Юридическое лицо",handler),vBoxData);
      lcwest.add( createToggleButton("Договор",handler),vBoxData);
      
       BorderLayoutData west = new BorderLayoutData(150);
 
      borderLayoutContainer.setWestWidget(lcwest,west);
      ContentPanel con = new ContentPanel();
      con.setHeaderVisible(false);
        
      con.add(card,new MarginData(10, 0, 0, 10));
      borderLayoutContainer.setCenterWidget(con);
      
       getButton(Dialog.PredefinedButton.CANCEL).addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                 data = null;
                 hide();
            }
        });
      
      getButton(Dialog.PredefinedButton.YES).addSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if(!validateNotEmty(card))
                {
                        Info.display(SafeHtmlUtils.fromTrustedString("Внимание"),SafeHtmlUtils.fromTrustedString("<font color='red'>Не все поля заполнены</font>"));
                        return;
                }
                
                final TheaterItem theater = new TheaterItem(0,false,
                         new TheaterItem.Main(field_name.getValue(),field_uniq_ident.getValue(), field_n_server.getValue(),
                                              field_hdd1.getValue(),  field_hdd2.getValue(),  field_bios_pass.getValue(), 
                                               field_contract_number.getValue(), field_contract_date.getValue()),
                        new TheaterItem.Address(field_county.getValue(),field_city.getValue(),field_index.getValue(), field_street.getValue(), field_house.getValue()),
                        new TheaterItem.UridAdress(field_urid_county.getValue(),field_urid_city.getValue(),field_urid_index.getValue(), 
                                                   field_urid_street.getValue(), field_urid_house.getValue(),field_urid_phone.getValue(),field_urid_fax.getValue(),field_urid_mail.getValue()),
                        new TheaterItem.UridInfo(field_urid_comp_name.getValue(),field_urid_director.getValue(),field_urid_director_rd.getValue(),field_urid_inn.getValue(),field_urid_kpp.getValue(),field_urid_ogrn.getValue(),field_urid_rs.getValue(),
                        field_urid_bank.getValue(),field_urid_bik.getValue()),
                        null);
                        
                                 
             
                CallbackWithFailureDialog<Integer> callback = new CallbackWithFailureDialog<Integer>("Не удалось добавить кинотеатр") {

                     @Override
                     public void onFailure(Throwable caught) {
                         super.onFailure(caught); //To change body of generated methods, choose Tools | Templates.
                     }
                     
                     @Override
                     public void onSuccess(Integer result) {
                         data = theater;
                         data.id = result;
                         hide();
                     }
                 };
                 GWTServiceAsync.instance.addNewTheater( theater, callback);
            }
      
      });
      
      
      ((ToggleButton) lcwest.getWidget(0)).setValue(true);  
    }
    boolean validateNotEmty(HasWidgets w){
         Iterator<Widget> iterator = w.iterator();
         while(iterator.hasNext()){
             Widget next = iterator.next();
             if(next instanceof HasWidgets)
             {
                 if(validateNotEmty((HasWidgets)next)==false)
                     return false;
             }
             else if(next instanceof ValueBaseField){
                 ValueBaseField field = (ValueBaseField) next;
                 if(field.getCurrentValue()==null && field.getData("nullable")==null)
                 {
                     int cardindex = card.getWidgetIndex(field.getParent().getParent());
                     
                     ToggleButton but = (ToggleButton) lcwest.getWidget(cardindex);
                     but.setValue(true, true);
                    
                     field.focus();
                     return false;
                 }
             
             }
         }
         return true;
         
    }
    private final ToggleGroup toggleGroup = new ToggleGroup();
     private ToggleButton createToggleButton(String name, ValueChangeHandler<Boolean> handler) {
         
    ToggleButton button = new ToggleButton(name);
     button.addValueChangeHandler(handler);
    button.setAllowDepress(false);
    toggleGroup.add(button);
 
    return button;
  }
}
