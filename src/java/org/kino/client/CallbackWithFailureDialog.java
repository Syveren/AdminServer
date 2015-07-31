/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.client;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldSet;

/**
 *
 * @author kirio
 * @param <T>
 */
abstract public class CallbackWithFailureDialog<T> implements AsyncCallback<T>{

    String failureMsg;
    public CallbackWithFailureDialog(String failureMsg) {
        this.failureMsg = failureMsg;
    }

    
    @Override
    public void onFailure(Throwable caught) {
     
         CauseAlertBox box = new CauseAlertBox("Внимание", "<b>"+SafeHtmlUtils.htmlEscape(failureMsg)+"</b>",SafeHtmlUtils.htmlEscape(caught.getLocalizedMessage()));
 
         box.show();
      
    }
    
    public static class CauseAlertBox extends AlertMessageBox{
        FieldSet fieldSet;
        VerticalLayoutContainer vert;
        public CauseAlertBox(String title, String message,String cause) {
            super(title, message);
            setResizable(true);
             vert = new VerticalLayoutContainer();
            fieldSet = new FieldSet();
            fieldSet.setHeadingText("Причина");
            fieldSet.setCollapsible(true);
            fieldSet.add(new HTML(cause));
            fieldSet.setExpanded(false);
            vert.add(fieldSet,new VerticalLayoutContainer.VerticalLayoutData(1,1));
            vert.setScrollMode(ScrollSupport.ScrollMode.AUTO);
            contentAppearance.getContentElement(getElement()).appendChild(vert.getElement());
        }
        @Override
        protected void doAttachChildren() {
          super.doAttachChildren();
          ComponentHelper.doAttach(fieldSet);
        }

        @Override
        protected void doDetachChildren() {
          super.doDetachChildren();
          ComponentHelper.doDetach(fieldSet);
        }
    
    }; 
    
}
