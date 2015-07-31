/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.user.client.Window;
 

public class Html5Historian implements Historian,
    // allows the use of ValueChangeEvent.fire()
    HasValueChangeHandlers<String> {
 
  private final SimpleEventBus handlers = new SimpleEventBus();
 
  public Html5Historian() {
    initEvent();
  }
 
  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
    return this.handlers.addHandler(ValueChangeEvent.getType(), valueChangeHandler);
  }
 
  @Override
  public String getToken() {
        return Window.Location.getPath().substring(1);
  }
 
  @Override
  public void newItem(String token, boolean issueEvent) {
    if (getToken().equals(token)) { // not sure if this is needed, but just in case
      return;
    }
    // your own logic here to construct the new URI
    
     String path = Window.Location.createUrlBuilder().setParameter(token).buildString();
 
     pushState(path);
    if (issueEvent) {
      ValueChangeEvent.fire(this, getToken());
    }
  }
  
  public void goToBase(boolean issueEvent)
  {
       if(Window.Location.getParameter("page")==null)
           return;
      
        String path = Window.Location.createUrlBuilder().removeParameter("page").buildString();
 
        pushState(path);
        if (issueEvent) {
         ValueChangeEvent.fire(this, getToken());
       }
  }
  public void newParamentr(String param,String value,boolean issueEvent)
  {
       String oldPath = Window.Location.createUrlBuilder().buildString();
    
   
        String path = Window.Location.createUrlBuilder().setParameter(param,value).buildString();
        if(oldPath.equals(path)){
            return;
        }
        pushState(path);
         if (issueEvent) {
          ValueChangeEvent.fire(this, getToken());
        }
  }
  @Override
  public void fireEvent(GwtEvent<?> event) {
    this.handlers.fireEvent(event);
  }
 
  private native void initEvent() /*-{
    var that = this;
    var oldHandler = $wnd.onpopstate;
    $wnd.onpopstate = $entry(function(e) {
      that.@org.kino.client.Html5Historian::onPopState()();
      if (oldHandler) {
        oldHandler();
      }
    });
  }-*/;
 
  private void onPopState() {
    ValueChangeEvent.fire(this, getToken());
  }
 //+"?gwt.codesvr=127.0.0.1:9998");
  private native void pushState(String url) /*-{
    $wnd.history.pushState(null, $doc.title, url);
  }-*/;
  
  
  public native void back()/*-{
    $wnd.history.back();
  }-*/;
}