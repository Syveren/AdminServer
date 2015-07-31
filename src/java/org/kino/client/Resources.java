/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

/**
 *
 * @author kirio
 */
 
public interface Resources extends ClientBundle {
  public static final Resources INSTANCE =  GWT.create(Resources.class);
 
    @Source("img/logo.jpg")
    // @ImageOptions(height = 32)    
  public ImageResource logo(); 
   @Source("img/exclamation.png")
  public ImageResource exclamation(); 
  
   @Source("img/control_play.png")
  public ImageResource control_play(); 
   @Source("img/control_pause.png")
  public ImageResource control_pause(); 
  
  @Source("img/globe.png")
  public ImageResource globe_24(); 
  @Source("img/cross.png")
  public ImageResource cross_24(); 
   @Source("img/cross.png")
   @ImageOptions(width = 16,height = 16)    
  public ImageResource cross_16();
  
  @Source("img/empty_img.png")
  public ImageResource empty_img_128(); 
  
  @Source("img/cross_white.png")
  public ImageResource cross_white(); 
  
  @Source("img/folder_open.png")
  public ImageResource folder_open();
  
   @Source("img/arrow_up.png")
  public ImageResource arrow_up(); 
   @Source("img/folder.png")
  public ImageResource folder();
   @Source("img/file.png")
  public ImageResource file();
  @Source("img/folder_open_full.png")
  public ImageResource folder_open_full();
   @Source("img/image.png")
  public ImageResource image(); 
  
  
  @Source("img/plus.png")
  public ImageResource plus();
   @Source("img/plus.png")
   @ImageOptions(width = 16,height = 16)    
  public ImageResource plus_16();
  
  @Source("img/search.png")
   @ImageOptions(width = 16,height = 16)    
  public ImageResource search_16();
  
  @Source("img/screen.png")
  public ImageResource screen();
   @Source("img/screen.png")
  @ImageOptions(width = 16,height = 16)    
  public ImageResource screen_16();
 
    
  @Source("img/search.png")
  public ImageResource search();
  
  
 @Source("img/pencil.png")
  public ImageResource pencil();
  
  
   @Source("img/plus.png")
   @ImageOptions(width = 32,height = 32)    
  public ImageResource plus_32();
  
  @Source("img/search.png")
   @ImageOptions(width = 32,height = 32)    
  public ImageResource search_32();
  
 
   @Source("img/screen.png")
  @ImageOptions(width = 32,height = 32)    
  public ImageResource screen_32();

 
  
  @Source("img/search.png")
   @ImageOptions(width = 48,height = 48)    
  public ImageResource search_48();

  @Source("img/status_red.png")
 //   @ImageOptions(width = 16,height = 16)
  public ImageResource status_red();
    @Source("img/status_green.png")
  //  @ImageOptions(width = 16,height = 16)    
  public ImageResource status_green();
  
  @Source("img/document_word.png")
  public ImageResource doc_word();
    @Source("img/trash.png")
  public ImageResource trash();
    @Source("img/clock_network.png")
  public ImageResource rate_timer();
  
  
   @Source("img/refresh.png")
  public ImageResource refresh();
  
   @Source("img/loading_bar.gif")
  public ImageResource loading_bar();
 
}