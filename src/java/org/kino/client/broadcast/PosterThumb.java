package org.kino.client.broadcast;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
 

import java.util.ArrayList;
import java.util.List;
import org.kino.client.Resources;

public class PosterThumb extends Component {
   
   public static class Photo {
        SafeUri thumbPathUri;
        String comment = "Кликните чтобы добавить постер";
        public Photo(SafeUri thumbPhotoDiv) {
    
            this.thumbPathUri = thumbPhotoDiv;
        }

        public String getComment() {
            return comment;
        }

     

        public SafeUri getThumbPathUri() {
            return thumbPathUri;
        }
        
        
    
    }
    interface PhotoRenderer extends XTemplates {//position: relative;
        @XTemplates.XTemplate("<div id=\"thumbPhotoDiv\" class=\"{style.thumbPhoto}\">" +  //{style.aligncenter}
                "<div style=\"border: 1px solid #fff;\" class=\"{style.aligncenter}\">" +
                "<img src=\"{photo.thumbPathUri}\" class=\"{style.aligncenter}\">" +  //style="position: absolute;"
                "<div style=\"position: absolute; opacity: 0.8; visibility: hidden; pointer-events: none;\" class=\"{style.aligncenter}\"><img src=\"{imageUri}\"></div>" +
                "</div>" +
                "</div>")
        public SafeHtml render(Photo photo, PhotoThumbsStyle style, SafeUri imageUri);
    }
    private PhotoRenderer renderer;
    private PhotoThumbsStyle style;
    private Photo photo = null;
    private Element contentElem = null;

    private ToolTipConfig config = null;

    private List<OnClickPhotoThumbsHandler> clickHandlers = new ArrayList<OnClickPhotoThumbsHandler>();
    public PosterThumb() {
        super();
        
        contentElem = DOM.createDiv();
        setElement(contentElem);

        final PhotoThumbsResources resources = GWT.create(PhotoThumbsResources.class);
        
        style = resources.css();
        resources.css().ensureInjected();
        renderer = GWT.create(PhotoRenderer.class);

        addStyleName(ThemeStyles.get().style().border());

        config = new ToolTipConfig();
        config.setTitleHtml("Описание фотографии");
        config.setMouseOffsetX(0);
        config.setMouseOffsetY(0);
        config.setAnchor(com.sencha.gxt.core.client.Style.Side.TOP);

        setPixelSize(142, 142);
//        setPixelSize(200, 200);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
    }

    private Element findElementById(Element el, String id) {
        NodeList<Node> list = el.getChildNodes();
        for(int i = 0; i < list.getLength(); i++) {
            Node node = list.getItem(i);
            if(node == null) continue;
            Element elNode = Element.as(node);
            if(elNode.getId().equals(id)) {
                return elNode;
            }
            elNode = findElementById(elNode, id);
            if(elNode != null) return elNode;
        }
        return null;
    }

    @Override
    protected void onResize(final int width, final int height) {
        super.onResize(width, height);
        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Element div = contentElem.getFirstChildElement();

                if(div != null) {
                    div.getStyle().setWidth(width - 4, Style.Unit.PX);
                    div.getStyle().setHeight(height - 4, Style.Unit.PX);

                    Element borderDiv = div.getFirstChildElement();
                    borderDiv.getStyle().setWidth(width - 12, Style.Unit.PX);
                    borderDiv.getStyle().setHeight(height - 12, Style.Unit.PX);
                }
            }
        });
    }

    @Override
    protected void onShow() {
        super.onShow();
        onResize(getOffsetWidth(), getOffsetHeight());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        onResize(getOffsetWidth(), getOffsetHeight());
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

  

    public void setPhoto(final Photo photo) {
        this.photo = photo;
//        SafeHtml iconHtml = AbstractImagePrototype.create(VksResources.INSTANCE.zoom4_32()).getSafeHtml();
        contentElem.setInnerSafeHtml(renderer.render(photo, style, Resources.INSTANCE.search_48().getSafeUri()));
        onResize(getElement().getStyleSize().getWidth() + 2, getElement().getStyleSize().getHeight() + 2);
        final Element rootDiv = contentElem.getFirstChildElement();
        final Element borderDiv = rootDiv.getFirstChildElement();
        if(photo.getComment() == null)
            setToolTipConfig(null);
        else {
            setToolTipConfig(config);
            config.setAnchor(com.sencha.gxt.core.client.Style.Side.TOP);
            config.setBodyText(photo.getComment());
            getToolTip().update(config);
        }
        rootDiv.getStyle().setBackgroundColor("#fff");
        final Element img = borderDiv.getFirstChildElement();
        final Element divZoom = Element.as(borderDiv.getChildNodes().getItem(1));
        Element imgZoom = null;
        if(divZoom != null) {
            imgZoom = divZoom.getFirstChildElement();
            DOM.sinkEvents((com.google.gwt.user.client.Element) imgZoom, Event.ONLOAD);
            final Element finalImgZoom = imgZoom;
            DOM.setEventListener((com.google.gwt.user.client.Element) imgZoom, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    switch (event.getTypeInt()) {
                        case Event.ONLOAD: {
                            if(finalImgZoom.getClientWidth() > 0)
                                divZoom.getStyle().setWidth(finalImgZoom.getClientWidth() + 2, Style.Unit.PX);
                            if(finalImgZoom.getClientHeight() > 0)
                                divZoom.getStyle().setHeight(finalImgZoom.getClientHeight() + 2, Style.Unit.PX);
                        } break;
                    }
                }
            });
        }
        DOM.sinkEvents((com.google.gwt.user.client.Element) borderDiv, Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
        final Element finalImgZoom = imgZoom;
        DOM.setEventListener((com.google.gwt.user.client.Element) borderDiv, new EventListener() {
            @Override public void onBrowserEvent(Event event) {
                switch(event.getTypeInt()) {
                    case Event.ONCLICK: {
                        onClickPhotoThumbs();
                    } break;
                    case Event.ONMOUSEOVER: {
                        if(finalImgZoom != null) {
                            if (finalImgZoom.getClientWidth() > 0)
                                divZoom.getStyle().setWidth(finalImgZoom.getClientWidth() + 2, Style.Unit.PX);
                            if (finalImgZoom.getClientHeight() > 0)
                                divZoom.getStyle().setHeight(finalImgZoom.getClientHeight() + 2, Style.Unit.PX);
                        }
                        rootDiv.getStyle().setBackgroundColor(ThemeStyles.get().backgroundColorLight());
                        borderDiv.addClassName(style.thumbPhotoBorder());
                        if(divZoom != null) {
                            divZoom.getStyle().setVisibility(Style.Visibility.VISIBLE);
                        }
                    } break;
                    case Event.ONMOUSEOUT: {
                        rootDiv.getStyle().setBackgroundColor("#fff");
                        borderDiv.removeClassName(style.thumbPhotoBorder());
                        img.getStyle().clearProperty("cursor");
                        if(divZoom != null) {
                            divZoom.getStyle().setVisibility(Style.Visibility.HIDDEN);
                        }
                    } break;
                }
            }
        });
    }

    public Photo getPhoto() {
        return photo;
    }

    public static interface OnClickPhotoThumbsHandler {
        public abstract void onClick();
    }

    public void addOnClickPhotoThumbsHandler(OnClickPhotoThumbsHandler handler) {
        clickHandlers.add(handler);
    }

    public void removeOnClickPhotoThumbsHandler(OnClickPhotoThumbsHandler handler) {
        clickHandlers.remove(handler);
    }

    public void removeAllOnClickPhotoThumbsHandler() {
        clickHandlers.clear();
    }

    private void onClickPhotoThumbs() {
        for(OnClickPhotoThumbsHandler handler: clickHandlers) {
            handler.onClick();
        }
    }
}
