/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client;

import org.kino.client.broadcast.NewSendW;
import org.kino.client.monitoring.MonitoringW;
import org.kino.client.theater.TheaterW;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.kino.client.clean.CleantProjectsWidget;
import org.kino.client.monitoring.ConreteProjectInfo;

/**
 * Main entry point.
 *
 * @author kirio
 */
public class MainEntryPoint implements EntryPoint {

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }

    
    public static    ArrayList<ValueBaseField>   getFields(HasWidgets container) {
        ArrayList<ValueBaseField> list = new ArrayList<ValueBaseField>();
        Iterator<Widget> it = container.iterator();
        while (it.hasNext()) {
          Widget w = it.next();

            if (w instanceof ValueBaseField<?>) {
              list.add((ValueBaseField) w);
            }
           if (w instanceof HasWidgets) {
                list.addAll(getFields((HasWidgets) w));
           }

        }
        return list;
  }
    /**
     * The entry point method, called automatically by loading a module that
     * declares an implementing class as an entry-point
     */
    
    CardLayoutContainer cartLay = new CardLayoutContainer();
    MainMenu mainMenu;
    MonitoringW monitoringW;
    NewSendW newSendW;
    TheaterW theaterW;
    
    final static String PAGE_NEW = "new";
    final static String PAGE_THEATER = "theater";
    final static String PAGE_MONITOR = "monitor";
    Html5Historian history = new Html5Historian();
    @Override
    public void onModuleLoad() {
        
        String page = Window.Location.getParameter("page");
      
        String project_id = Window.Location.getParameter("project");
        if(project_id!=null)
        {
            final Viewport view = new Viewport();
            view.add(new ConreteProjectInfo(project_id));
            RootPanel.get().add(view,0,0);
            return;
        
        }
        String client_id = Window.Location.getParameter("clean");
        if(client_id!=null)
        {
            final Viewport view = new Viewport();
            view.add(new CleantProjectsWidget(client_id));
            RootPanel.get().add(view,0,0);
            return;
        
        }
        
        final Viewport view = new Viewport();
        final VerticalLayoutContainer vert =  new VerticalLayoutContainer();
       //BorderLayoutContainer border = new BorderLayoutContainer();
       // view.add(border);
       // border.setNorthWidget(createTop(),new BorderLayoutContainer.BorderLayoutData(50));
        history.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                    String page = Location.getParameter("page");
                    if(page!=null)
                    {
                        if(page.equals(PAGE_NEW))
                            cartLay.setActiveWidget(newSendW);
                        else if(page.equals(PAGE_MONITOR))
                            cartLay.setActiveWidget(monitoringW);
                        if(page.equals(PAGE_THEATER))
                            cartLay.setActiveWidget(theaterW);


                    }else 
                        cartLay.setActiveWidget(mainMenu);
                    cartLay.forceLayout();
            }
        });
        mainMenu = new MainMenu(){

            @Override
            void onMenuClick(int menu_id) {
                super.onMenuClick(menu_id);
                //System.out.println("selected: "+menu_id);
                if(menu_id==MENU_SEARCH){
                
                   history.newParamentr("page", PAGE_MONITOR, true);
                  //  cartLay.setActiveWidget(monitoringW);
                
                }
                else if(menu_id==MENU_ADD){
                   // String path = Window.Location.createUrlBuilder().setParameter("page",PAGE_NEW).buildString();
                    newSendW.clear();
                      history.newParamentr("page", PAGE_NEW, true);
                    //pushState(path);
                    //Window.Location.replace(path);
                    
                    //cartLay.setActiveWidget(newSendW);
                    
                }
                else if(menu_id==MENU_THEATER){
                      history.newParamentr("page", PAGE_THEATER, true);
                    //String path = Window.Location.createUrlBuilder().setParameter("page",PAGE_THEATER).buildString();
                   // pushState(path);
                   // Window.Location.replace(path);
                  // cartLay.setActiveWidget(theaterW);
                
                }
                // cartLay.forceLayout();
            }   
            
        
        };
        
        
        vert.add(createTop(),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        vert.add(cartLay,new VerticalLayoutContainer.VerticalLayoutData(1, 1));
        view.add(vert);
        monitoringW = new MonitoringW();
        newSendW = new NewSendW(){

            @Override
            public void onCancelClick() {
                super.onCancelClick(); 
                cartLay.setActiveWidget(mainMenu);
            }

            @Override
            public void needParentLayout() {
                
                 Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                         cartLay.forceLayout();
                    }});
            }

         
            
            
        
        };
        theaterW = new TheaterW() ;
        cartLay.add(mainMenu.asWidget());
        cartLay.add(monitoringW.asWidget());
        cartLay.add(newSendW.asWidget());
        cartLay.add(theaterW.asWidget());
        
       
        
        
        
        
     
   
        
        
        // hor_dcp  = new HBoxLayoutContainer();
        //hor_dcp.setSpacing(10);
        //hor_dcp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
       
      
     
         
        /*
        
         PosterThumb dossierPhotoThumbs = new PosterThumb();
         dossierPhotoThumbs.setPhoto(new PosterThumb.Photo(Resources.INSTANCE.empty_img().getSafeUri()));
//         dossierPhotoThumbs.setSize("162px", "162px");
         dossierPhotoThumbs.setPixelSize(160, 160);
         CenterLayoutContainer cont = new CenterLayoutContainer();
         HorizontalPanel hor = new HorizontalPanel();
         hor.add(dossierPhotoThumbs);
         
         hor.add(new HTML("hellow orld"));
         cont.add(hor);
         view.add(cont);*/
  
     //   frame.setSize("128px", "128px");
       /* PosterThumb dossierPhotoThumbs = new PosterThumb();
        dossierPhotoThumbs.setPhoto(new PosterThumb.Photo(Resources.INSTANCE.screen().getSafeUri()));
  
 */
 
//     view.clear();
//     view.add(new NewSendW());
     
     // SelectTheaterDialog d = new SelectTheaterDialog();
     // d.show();
        RootPanel.get().add(view,0,0);
        
        if(page!=null)
        {
            if(page.equals(PAGE_NEW))
                cartLay.setActiveWidget(newSendW);
            else if(page.equals(PAGE_MONITOR))
                cartLay.setActiveWidget(monitoringW);
            if(page.equals(PAGE_THEATER))
                cartLay.setActiveWidget(theaterW);
 
        
        }
  
    }
    
    Widget createTop(){
        HBoxLayoutContainer hbox = new HBoxLayoutContainer();
 
        Image logo = new Image(Resources.INSTANCE.logo());
        final int logo_width = 64;
        logo.setPixelSize(Resources.INSTANCE.logo().getWidth() * logo_width / Resources.INSTANCE.logo().getHeight(),logo_width );
 
        
        hbox.addStyleName("title");
            
        hbox.setHBoxLayoutAlign(HBoxLayoutContainer.HBoxLayoutAlign.MIDDLE);
        hbox.setEnableOverflow(false);
        hbox.add(logo, new BoxLayoutContainer.BoxLayoutData(new Margins(10, 45, 10, 10)));
 
        final  TextButton but_broadcast =  new TextButton("Рассылка",Resources.INSTANCE.plus_32());
        final TextButton but_monitoring = new TextButton("Мониторинг",Resources.INSTANCE.search_32());
        final TextButton but_theaters = new TextButton("Кинотеатры",Resources.INSTANCE.screen_32());
        hbox.add(but_broadcast, new BoxLayoutContainer.BoxLayoutData(new Margins(0, 5, 0, 0)));
        hbox.add(but_monitoring, new BoxLayoutContainer.BoxLayoutData(new Margins(0, 5, 0, 0)));
        hbox.add(but_theaters, new BoxLayoutContainer.BoxLayoutData(new Margins(0, 5, 0, 0)));
 
        SelectEvent.SelectHandler clickHandler = new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                  if(event.getSource()==but_monitoring){
                    history.newParamentr("page", PAGE_MONITOR, true);
                
                }
                else if(event.getSource()==but_broadcast){
                    newSendW.clear();
                    history.newParamentr("page", PAGE_NEW, true);
        
                    
                }
                else if(event.getSource()==but_theaters){
                      history.newParamentr("page", PAGE_THEATER, true);
            }
        }};
        but_broadcast.addSelectHandler(clickHandler);
        but_monitoring.addSelectHandler(clickHandler);
        but_theaters.addSelectHandler(clickHandler);
        logo.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
               history.goToBase(true);   
            }
        }
        );

        
        return hbox;
    }
}
