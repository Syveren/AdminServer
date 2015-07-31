/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 *
 * @author kirio
 */
public class MainMenu implements IsWidget{
    CenterLayoutContainer center = new CenterLayoutContainer();
    @Override
    public Widget asWidget() {
         return center;
    }

    static final int MENU_ADD=0;
    static final int MENU_SEARCH=1;
    static final int MENU_THEATER=2;
    
    void onMenuClick(int menu_id){}
    public MainMenu() {
        HorizontalPanel hor = new HorizontalPanel();
        hor.setSpacing(150);
        center.add(hor);
        
        final TextButton but_add = new TextButton("Новая рассылка",Resources.INSTANCE.plus());
        final TextButton but_search = new TextButton("Мониторинг",Resources.INSTANCE.search());
        final TextButton but_movie = new TextButton("Кинотеатры",Resources.INSTANCE.screen());
      
        hor.add(but_add);
        hor.add(but_search);
        hor.add(but_movie);
        
        but_add.setIconAlign(ButtonCell.IconAlign.TOP);
        but_search.setIconAlign(ButtonCell.IconAlign.TOP);
        but_movie.setIconAlign(ButtonCell.IconAlign.TOP);
        
        SelectEvent.SelectHandler selectHandler = new SelectEvent.SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                 int id = -1;
                 if(event.getSource()==but_add)
                     id = MENU_ADD;
                 else if(event.getSource()==but_search)
                     id = MENU_SEARCH;
                 else if(event.getSource()==but_movie)
                     id = MENU_THEATER;
                 onMenuClick(id);
            }
        };
        but_add.addSelectHandler(selectHandler);
        but_search.addSelectHandler(selectHandler);
        but_movie.addSelectHandler(selectHandler);
        
    }
    
}
