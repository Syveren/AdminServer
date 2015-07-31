/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.ResizeCell;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 *
 * @author kirio
 */
public class CellColumnResizer implements ColumnWidthChangeEvent.ColumnWidthChangeHandler {

    private final Grid  grid;
    private final ColumnConfig  column;
    private final ResizeCell  cell;

    public CellColumnResizer(Grid grid, ColumnConfig column, ResizeCell  cell) {
      this.grid = grid;
      this.column = column;
      this.cell = cell;
    }
    @Override
    public void onColumnWidthChange(ColumnWidthChangeEvent event) {
         if (column == event.getColumnConfig()) {
                int w = event.getColumnConfig().getWidth();
                int rows = grid.getStore().size();

                int col = grid.getColumnModel().indexOf(column);

                cell.setWidth(w-10);

               // ListStore<M> store = grid.getStore();

                 for (int i = 0; i < rows; i++) {
                  Object p = grid.getStore().get(i);
                  grid.getStore().update(p);
                 
                  Element parent = grid.getView().getCell(i, col);
                  if (parent != null) {
                  parent = parent.getFirstChildElement();
                  SafeHtmlBuilder sb = new SafeHtmlBuilder();
                  cell.render(new Context(i, col, grid.getStore().getKeyProvider().getKey(p)), column.getValueProvider().getValue(p),
                      sb);
                  parent.setInnerHTML(sb.toSafeHtml().asString());
                }
                 }
          
    }
    
    }
}
