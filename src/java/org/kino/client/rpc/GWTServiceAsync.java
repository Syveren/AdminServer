/*
 * To change this license header, choose License Headers in CommonProjectInfo Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.kino.client.clean.CleantProjectsWidget;
import org.kino.client.broadcast.FileListView;
import org.kino.client.broadcast.NewSendW;
import org.kino.client.monitoring.ConreteProjectInfo;
import org.kino.client.monitoring.CurrentProjectsGrid;
import org.kino.client.monitoring.MonitoringW;
import org.kino.client.theater.MyLoadConfig;
import org.kino.client.theater.RateSchedulerDialog;
import org.kino.client.theater.TheaterItem;
import org.kino.client.theater.TheaterW;

/**
 *
 * @author kirio
 */
public interface GWTServiceAsync {

    public void myMethod(String s, AsyncCallback<String> callback);
    
    public static GWTServiceAsync instance = GWT.create(GWTService.class);

  // public Request getTheaterStatus(AsyncCallback<ArrayList<TheaterItem>> asyncCallback);

    public Request getCommonProjectInfo(AsyncCallback<ArrayList<MonitoringW.CommonProjectInfo>> asyncCallback);

    public void updateProjectInfo(AsyncCallback<ArrayList<MonitoringW.CommonProjectInfo>> asyncCallback);

 
    public Request getCountriesAndCitys(AsyncCallback<ArrayList<ArrayList<String>>> asyncCallback);

    public void addNewContact(int client_id, TheaterItem.ContactData contact, AsyncCallback<java.lang.Integer> asyncCallback);

    public void addNewTheater(TheaterItem theater, AsyncCallback<java.lang.Integer> asyncCallback);

    public void getProjectName(String path, AsyncCallback<String> asyncCallback);

    public void registerProject(NewSendW.NewProject project, ArrayList<String> uniq_client_ident,AsyncCallback<Boolean> asyncCallback);

    public void filesDown(String dirpath, boolean dir_only, java.lang.String[] filter_ext, AsyncCallback<FileListView.DirItem> asyncCallback);

    public void filesUp(String dirpath, boolean dir_only, java.lang.String[] filter_ext, AsyncCallback<FileListView.DirItem> asyncCallback);

    public void getCommonErrorInfo(AsyncCallback<ArrayList<MonitoringW.CommonErrorInfo>> asyncCallback);

    public void debugDelayRequest(int delay, AsyncCallback<Void> asyncCallback);

    public Request getConcreteProjects(int project_id, int total_size, PagingLoadConfig config, AsyncCallback<PagingLoadResult<ConreteProjectInfo.ConcreteProject>> asyncCallback);

    public void getProject(int id, AsyncCallback<ConreteProjectInfo.ConcreteInfo> asyncCallback);

    public void setClientProjectDownloadStatus(int id_clients_projects,boolean download, AsyncCallback<Void> asyncCallback);

    public Request getTheaterGridItems( int total_size, MyLoadConfig config, AsyncCallback<PagingLoadResult<TheaterItem>> asyncCallback);

    public Request getTheaterMainData(int id, AsyncCallback<TheaterW.TheaterMainData> asyncCallback);

    public Request getClientsNotRemovedProjects(String clinet_uniq_ident,AsyncCallback<ArrayList<CleantProjectsWidget.Item>> asyncCallback);

    public void markForRemove(ArrayList<Integer> id, AsyncCallback<Void> asyncCallback);

    public void addTheatersToProject(Integer project_id, ArrayList<String> clients_id, AsyncCallback<Void> asyncCallback);

    public Request getTheatersCanAddToProject(int project_id, int total_size, MyLoadConfig config, AsyncCallback<PagingLoadResult<TheaterItem>> asyncCallback);

    public void getRateLimitData(String client_uniq_ident, AsyncCallback<Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>>> asyncCallback);

    public void setRateLimitData(String client_uniq_ident, Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>> data, AsyncCallback<Void> asyncCallback);

    public void removeTheater(int client_id,String  client_uniq_id, AsyncCallback<Void> asyncCallback);

    public void updateTheaterInfo(TheaterItem updated, AsyncCallback<Void> asyncCallback);

    public void removeContact(int contract_id, AsyncCallback<Void> asyncCallback);

    public void updateContact(TheaterItem.ContactData contact, AsyncCallback<Void> asyncCallback);

    public void getCurrentProjectForTheater(String clinet_uniq_ident, AsyncCallback<ArrayList<CurrentProjectsGrid.ProjectItem>> asyncCallback);



  
    
}
