/*
 * To change this license header, choose License Headers in CommonProjectInfo Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
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
@RemoteServiceRelativePath("rpc/gwtservice")
public interface GWTService extends RemoteService {

    public String myMethod(String s);
    public ArrayList<ArrayList<String>> getCountriesAndCitys() throws Exception;


    public PagingLoadResult<TheaterItem> getTheaterGridItems(int total_size,MyLoadConfig config) throws Exception;
    
    public PagingLoadResult<TheaterItem> getTheatersCanAddToProject(int project_id,int total_size,MyLoadConfig config) throws Exception;
    
    
    //public ArrayList<TheaterW.Item> getTheaterStatus() throws Exception;
    public ArrayList<CurrentProjectsGrid.ProjectItem> getCurrentProjectForTheater(String clinet_uniq_ident)throws Exception;
    
    public ArrayList<MonitoringW.CommonProjectInfo> getCommonProjectInfo() throws Exception;
    public ArrayList<MonitoringW.CommonErrorInfo> getCommonErrorInfo() throws Exception;
    
    public ArrayList<MonitoringW.CommonProjectInfo> updateProjectInfo() throws Exception;
    
    
    public FileListView.DirItem filesDown(String dirpath,boolean dir_only,String...filter_ext) throws Exception;
    public FileListView.DirItem filesUp(String dirpath,boolean dir_only,String...filter_ext) throws Exception;
    
    public int addNewContact(int client_id,TheaterItem.ContactData contact)throws Exception;
    public void updateContact( TheaterItem.ContactData contact)throws Exception;
    public void removeContact(int contract_id) throws Exception;
    
    
    
    
    public int addNewTheater(TheaterItem theater)throws Exception;
    
    public void removeTheater(int client_id,String  client_uniq_id) throws Exception;
    public void updateTheaterInfo(TheaterItem updated)throws Exception;
 
    
    
    
    public void debugDelayRequest(int delay)throws Exception;
    
    
    public PagingLoadResult<ConreteProjectInfo.ConcreteProject> getConcreteProjects(int project_id,int total_size,PagingLoadConfig config)throws Exception;
    public ConreteProjectInfo.ConcreteInfo getProject(int id) throws Exception;
    
    public String getProjectName(String path) throws Exception;
    public Boolean registerProject(NewSendW.NewProject project,ArrayList<String> uniq_client_ident)throws Exception;
    
    public void setClientProjectDownloadStatus(int id_clients_projects,boolean download) throws Exception;
    
    
    
    public TheaterW.TheaterMainData getTheaterMainData(int id)throws Exception;
    
    public ArrayList<CleantProjectsWidget.Item> getClientsNotRemovedProjects(String clinet_uniq_ident)throws Exception;
    public void markForRemove(ArrayList<Integer> id) throws Exception;
    
    public void addTheatersToProject(Integer project_id,ArrayList<String> clients_id)throws Exception;
 
    
    public Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>>   getRateLimitData(String client_uniq_ident) throws Exception;
    public void setRateLimitData(String client_uniq_ident,Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>> data) throws Exception;


    
    


}
 