/*
 * To change this license header, choose License Headers in CommonProjectInfo Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.kino.client.clean.CleantProjectsWidget;
import org.kino.client.broadcast.FileListView;
import org.kino.client.broadcast.NewSendW;
import org.kino.client.monitoring.ConreteProjectInfo;
import org.kino.client.monitoring.CurrentProjectsGrid;
import org.kino.client.monitoring.MonitoringW;
import org.kino.client.theater.TheaterW;

import org.kino.client.rpc.GWTService;
import org.kino.client.theater.MyLoadConfig;
import org.kino.client.theater.RateSchedulerDialog;
import org.kino.client.theater.TheaterItem;
import org.kino.server.structs.CheckNewProjectHelper.ProjectInitException;
import org.kino.server.structs.CheckNewProjectHelper;
import org.kino.server.structs.DataSourceSettings;
 
import org.kino.server.structs.ImageUtil;
import org.kino.server.structs.ProjectInitialisation;
import org.kino.server.structs.Settings;
 

/**
 *
 * @author kirio
 */
public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {

    public GWTServiceImpl() {
    
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
             
        }
    
        
    }

    
    @Override
    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.
        return "Server says: " + s;
    }

   
    
    @Override
    public PagingLoadResult<TheaterItem> getTheatersCanAddToProject(int project_id, int total_size, MyLoadConfig config) throws Exception {
        try(Connection db = DataSourceSettings.dataSource.getConnection()) 
        {
 
         String filter_county   =  config.getFilter("country");
 
         String filter_city     =  config.getFilter("city");
         
         String[] compex_filter_list = null;
         if(config.getFilter("complex")!=null)
         {
              
              String[]  tmp = config.getFilter("complex").trim().split(",");
              ArrayList<String> nonEmptyItems = new ArrayList<>(tmp.length);
              for(String item:tmp)
              {
                  String trimmed =  item.trim();
                  if(trimmed.isEmpty())
                      continue;
                  
                  nonEmptyItems.add("%"+trimmed+"%");
              }
              
              compex_filter_list = nonEmptyItems.isEmpty()?null:nonEmptyItems.toArray(new String[nonEmptyItems.size()]);
         }
          
         ArrayList<TheaterItem> result = new ArrayList<>();
 
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE unique_ident NOT IN (SELECT client_uniq_ident FROM clients_projects WHERE project_id = '").append(project_id).append("')");
        if(filter_county!=null)
            sb.append(" AND country = ? ");
            
        if(filter_city!=null){
            sb.append(sb.length()!=0?" AND ":" WHERE ");
            sb.append(" city = ? ");
        }
        
        if(compex_filter_list!=null){
            sb.append(sb.length()!=0?" AND ":" WHERE ");
 
            sb.append(" (city ILIKE ANY (?)");
            sb.append(" OR country ILIKE ANY (?)");
            sb.append(" OR name ILIKE ANY (?))");
 
        }
 
         String userSort = getSortString(config);
            if(!userSort.isEmpty())
                userSort+=",";
        StringBuilder query = new StringBuilder();
        query.append("SELECT id, name,unique_ident, n_server,hdd1, hdd2,bios_pass,n_contract, contract_date,"  // 1-9
                + " country, city, index, street, house, "//10-14
                + " urid_country, urid_city, urid_index, urid_street, urid_house,urid_phone,urid_fax,urid_mail, "//15-22
                + " urid_name,urid_director_fio,urid_director_fio_rd,inn, kpp, ogrn,rs,bank,bank_bik,")//23-31
                
 
                
                .append("COALESCE(EXTRACT(EPOCH FROM (now() AT TIME ZONE 'UTC' - time_last_active AT TIME ZONE 'UTC'))::bigint,")
                .append(Long.MAX_VALUE)
                .append(") as time_last_active_sec ") //32
                .append(total_size<0?",count(*) OVER() AS full_count":",-1") //33
                .append(" FROM clients ")
                .append(sb)
                .append(" ORDER BY  ")
                .append(userSort)
                .append(" id ")
                .append(" OFFSET ").append(config.getOffset())
                .append(" LIMIT ").append(config.getLimit());
        PreparedStatement pst = db.prepareStatement(query.toString());
     
        int pstindex = 1;
         if(filter_county!=null)
            pst.setString(pstindex++, filter_county);
         if(filter_city!=null)
            pst.setString(pstindex++, filter_city);
         if(compex_filter_list!=null){
             Array array = db.createArrayOf("text", compex_filter_list);
             pst.setArray(pstindex++, array);
             pst.setArray(pstindex++, array);
             pst.setArray(pstindex++, array);
         }
        ResultSet rs = pst.executeQuery();
        
        
         while(rs.next()){
             
        
             int client_id = rs.getInt(1);
             ArrayList<TheaterItem.ContactData> contacts = null;
             
            TheaterItem.Main main = new TheaterItem.Main(rs.getString(2), rs.getString(3), 
                            rs.getString(4), rs.getString(5), rs.getString(6),  rs.getString(7),rs.getString(8), rs.getDate(9));
            TheaterItem.Address address = new TheaterItem.Address(rs.getString(10),  rs.getString(11), rs.getString(12), rs.getString(13),rs.getString(14));
            
             TheaterItem.UridAdress uridAddress = new TheaterItem.UridAdress(
                     rs.getString(15),
                     rs.getString(16), rs.getString(17), 
                     rs.getString(18), rs.getString(19), 
                     rs.getString(20), rs.getString(21),rs.getString(22));
             
            TheaterItem.UridInfo uridInfo = new TheaterItem.UridInfo(
                    rs.getString(23),rs.getString(24),
                    rs.getString(25),rs.getString(26),
                    rs.getString(27),rs.getString(28),
                    rs.getString(29),rs.getString(30),rs.getString(31));
            
            
            
            
            Long last_active_sec_ago = rs.getLong(32);
             
           
          
             boolean status =  (last_active_sec_ago<(30));
             TheaterItem data = new TheaterItem(client_id,status,main,address,uridAddress,uridInfo,contacts);
             result.add(data);
            
             if(total_size<0)
               total_size = rs.getInt(33);
         }
         return new PagingLoadResultBean<>(result, result.isEmpty()?0:total_size, config.getOffset());
        }
        catch(SQLException ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }
    
    
   private  static boolean checkNonWordCharacters(String str)  
    {
         Pattern pattern = Pattern.compile("\\w*\\.?\\w+");
        Matcher matcher = pattern.matcher(str);
        boolean normal =  matcher.matches();
        if(!normal)
            System.out.println("WARNING: Potential sql injection found -> '"+str+"'");
        return  normal;
    }
 public static String getSortString(  PagingLoadConfig config) throws Exception{
        
        StringBuilder sb = new StringBuilder();
        for(SortInfo  bean:config.getSortInfo())
        {
            if(!checkNonWordCharacters(bean.getSortField()))
                continue;
            
            if(sb.length()!=0)
                sb.append(",");
            sb.append(bean.getSortField()).append(" ").append(bean.getSortDir().name());
        
        }
        return sb.toString();
    }
    @Override
    public PagingLoadResult<TheaterItem> getTheaterGridItems( int total_size,MyLoadConfig config) throws Exception{
        try(Connection db = DataSourceSettings.dataSource.getConnection()) {
 
         String filter_county   =  config.getFilter("country");
 
         String filter_city     =  config.getFilter("city");
             
         String[] compex_filter_list = null;
         if(config.getFilter("complex")!=null)
         {
              
              String[]  tmp = config.getFilter("complex").trim().split(",");
              ArrayList<String> nonEmptyItems = new ArrayList<>(tmp.length);
              for(String item:tmp)
              {
                  String trimmed =  item.trim();
                  if(trimmed.isEmpty())
                      continue;
                  
                  nonEmptyItems.add("%"+trimmed+"%");
              }
              
              compex_filter_list = nonEmptyItems.isEmpty()?null:nonEmptyItems.toArray(new String[nonEmptyItems.size()]);
         }
          
         ArrayList<TheaterItem> result = new ArrayList<>();
 
                                                                    
       
        StringBuilder sb = new StringBuilder();
        if(filter_county!=null)
            sb.append(" WHERE country = ? ");
            
        if(filter_city!=null){
            sb.append(sb.length()!=0?" AND ":" WHERE ");
            sb.append(" city = ? ");
        }
        
        if(compex_filter_list!=null){
            sb.append(sb.length()!=0?" AND ":" WHERE ");
 
            sb.append(" (city ILIKE ANY (?)");
            sb.append(" OR country ILIKE ANY (?)");
            sb.append(" OR name ILIKE ANY (?))");
  
        
        }
         String userSort = getSortString(config);
            if(!userSort.isEmpty())
                userSort+=",";

        StringBuilder query = new StringBuilder();
        query.append("SELECT id, name, unique_ident, n_server,hdd1, hdd2,bios_pass,n_contract, contract_date,"  // 1-9
                + " country, city, index, street, house, "//10-14
                + " urid_country, urid_city, urid_index, urid_street, urid_house,urid_phone,urid_fax,urid_mail, "//15-22
                + " urid_name,urid_director_fio,urid_director_fio_rd,inn, kpp, ogrn,rs,bank,bank_bik,")//23-31

                .append("COALESCE(EXTRACT(EPOCH FROM (now() AT TIME ZONE 'UTC' - time_last_active AT TIME ZONE 'UTC'))::bigint,")
                .append(Long.MAX_VALUE)
                .append(") as time_last_active_sec, ") //32
                
                .append("sys_net_info,sys_total_space,sys_free_space,sys_version ") //33-36
                .append(total_size<0?",count(*) OVER() AS full_count":",-1") //37
                .append(" FROM clients ")
                .append(sb)
                .append(" ORDER BY  ")
                .append(userSort)
                .append(" id ")
                .append(" OFFSET ").append(config.getOffset())
                .append(" LIMIT ").append(config.getLimit());
        //SELECT sys_net_info,sys_total_space,sys_free_space,sys_version FROM clients WHERE id NOT IN(1,2,3,4,5)
        PreparedStatement pst = db.prepareStatement(query.toString());
     
        PreparedStatement pst_get_need_download = db.prepareStatement("SELECT  SUM ((1-percent)*size)"
                + " FROM projects a,clients_projects b,clients c WHERE a.id = b.project_id AND c.unique_ident = b.client_uniq_ident" 
                +" AND a.status NOT IN ('removed') AND c.unique_ident = ? " 
                +" GROUP BY c.unique_ident ");
        
        
        int pstindex = 1;
         if(filter_county!=null)
            pst.setString(pstindex++, filter_county);
         if(filter_city!=null)
            pst.setString(pstindex++, filter_city);
         if(compex_filter_list!=null){
             Array array = db.createArrayOf("text", compex_filter_list);
             pst.setArray(pstindex++, array);
             pst.setArray(pstindex++, array);
             pst.setArray(pstindex++, array);
         }
        ResultSet rs = pst.executeQuery();
        
        PreparedStatement pst_contacts = db.prepareStatement("SELECT id, fio, post,  phone, mail" 
            +"  FROM contacts WHERE client_id = ? ORDER BY post");
        
        Pattern ip_tun_extract = Pattern.compile("tun[^:]*:(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
         while(rs.next()){
 
             int client_id = rs.getInt(1);
             pst_contacts.setInt(1,client_id);
             ArrayList<TheaterItem.ContactData> contacts = new ArrayList<>();
             ResultSet rs_contacts = pst_contacts.executeQuery();
             while(rs_contacts.next()){
                contacts.add(new TheaterItem.ContactData(
                        rs_contacts.getInt(1), 
                        rs_contacts.getString(2),
                        rs_contacts.getString(3),
                        rs_contacts.getString(4),
                        rs_contacts.getString(5)));
             }
             String uniq_ident =  rs.getString(3);
            TheaterItem.Main main = new TheaterItem.Main(rs.getString(2), uniq_ident, 
                            rs.getString(4), rs.getString(5), rs.getString(6),  rs.getString(7),rs.getString(8), rs.getDate(9));
            TheaterItem.Address address = new TheaterItem.Address(rs.getString(10),  rs.getString(11), rs.getString(12), rs.getString(13),rs.getString(14));
            
             TheaterItem.UridAdress uridAddress = new TheaterItem.UridAdress(
                     rs.getString(15),
                     rs.getString(16), rs.getString(17), 
                     rs.getString(18), rs.getString(19), 
                     rs.getString(20), rs.getString(21),rs.getString(22));
             
            TheaterItem.UridInfo uridInfo = new TheaterItem.UridInfo(
                    rs.getString(23),rs.getString(24),
                    rs.getString(25),rs.getString(26),
                    rs.getString(27),rs.getString(28),
                    rs.getString(29),rs.getString(30),rs.getString(31));
            
            
            
            
            Long last_active_sec_ago = rs.getLong(32);
             
           
          
             boolean status =  (last_active_sec_ago<(30));
             
             
             pst_get_need_download.setString(1,uniq_ident );
            
             
             String net_info    = rs.getString(33);
             long   total_space = rs.getLong(34);
             long   free_space  = rs.getLong(35);
             String   version  = rs.getString(36);
             long need_download = 0;
             ResultSet rs_2 = pst_get_need_download.executeQuery();
             if(rs_2.next())
                 need_download = rs_2.getLong(1);
             
             
             
             if(net_info!=null){
                 Matcher matcher = ip_tun_extract.matcher(net_info);
                 net_info = (matcher.find()?matcher.group(1):null);
          
             }
             
             
             
             TheaterItem.SystemInfo sysInfo = new TheaterItem.SystemInfo(net_info, free_space, total_space,need_download,version);
             TheaterItem data = new TheaterItem(client_id,status,main,address,uridAddress,uridInfo,contacts);
             data.sysInfo = sysInfo;
             result.add(data);
             if(total_size<0)
               total_size = rs.getInt(37);
         }
         return new PagingLoadResultBean<>(result, result.isEmpty()?0:total_size, config.getOffset());
        }
        catch(Exception ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

    
    int client_timer_interval = 45;
   /* @Override
    public ArrayList<TheaterItem> getTheaterStatus() throws Exception {
        
        try(Connection db = DataSourceSettings.dataSource.getConnection()) {
         ArrayList<TheaterItem> result = new ArrayList<>();
                                                                    
        PreparedStatement pst = db.prepareStatement("SELECT client_id,EXTRACT(EPOCH FROM (now() - time_last_active))::bigint FROM client_last_check "
                + "  RIGHT JOIN (SELECT id FROM clients) as t2 ON t2.id = client_last_check.client_id;");
         ResultSet rs = pst.executeQuery();
         while(rs.next())
         {
             int interval = (int) (Math.random()*80);//rs.getInt(2);
 
             result.add(new TheaterItem(rs.getInt(1),null,null,null,null,interval<client_timer_interval,null));
         
         }
         return result;
        }
        catch(SQLException ex)
        {
            throw new Exception(ex);
        }
        
      
        
    }/*/
 
    @Override
    public ArrayList<MonitoringW.CommonProjectInfo> getCommonProjectInfo() throws Exception {
         ArrayList<MonitoringW.CommonProjectInfo> result = new ArrayList<>();
        // System.out.println("getCommonProjectInfo ");
         try(Connection db = DataSourceSettings.dataSource.getConnection()) {
        
         //   db.setAutoCommit(false);
             PreparedStatement pst = db.prepareStatement("SELECT "
                     + "t.id, "
                     + "t.orig_title,"
                     + "t.rus_title,"
                     + "t.status,"
                     + "COALESCE(t.clients_count,0)clients_count , "
                     + "COALESCE(t.total_percent/t.clients_count,0)total_percent,"
                     + " t.time_create, "
                     + " COALESCE(t.errors_count,0)errors_count "
                     + " FROM (" 
                     + " (SELECT id,rus_title,orig_title,status,time_create FROM projects WHERE status!='error') AS a " 
                     + " LEFT JOIN " 
                     + " (SELECT project_id,COUNT(*) clients_count  FROM clients_projects GROUP BY project_id) AS b ON a.id = b.project_id  " 
                     + "  LEFT JOIN " 
                     + " (SELECT project_id, SUM(percent) total_percent   FROM (SELECT project_id,(case when status IN ('done' ,'removing','removed') then 1 else percent end)as percent FROM clients_projects)as t_inner GROUP BY project_id) AS c ON a.id = c.project_id  "
                     + " LEFT JOIN  " 
                     +" (SELECT   project_id,COUNT(*) errors_count   FROM clients_projects GROUP BY project_id,status HAVING status = 'error') AS d ON a.id = d.project_id "
                      + " )as t");
          
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
 
                result.add(new MonitoringW.CommonProjectInfo(rs.getInt(1), 
                        rs.getString(2), 
                        rs.getString(3),
                        rs.getDouble(6), 
                        rs.getInt(5),
                        0,
                        rs.getTimestamp(7).getTime(),
                        rs.getString(4)));
                
            }
         //   System.out.println("getCommonProjectInfo size: "+result.size());
            return result;
    
        }
        catch(Exception ex)
        {
            System.out.println("ERROR:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

    
    @Override
    public ArrayList<MonitoringW.CommonErrorInfo> getCommonErrorInfo() throws Exception {
         ArrayList<MonitoringW.CommonErrorInfo> result = new ArrayList<>();
         try(Connection db = DataSourceSettings.dataSource.getConnection()) {
        
            //db.setAutoCommit(false);
            PreparedStatement pst = db.prepareStatement("SELECT id,rus_title,orig_title,folder,"
                   + "(select COUNT(*) FROM clients_projects WHERE project_id=projects.id),error FROM projects WHERE status='error'");

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                result.add(new MonitoringW.CommonErrorInfo(rs.getInt(1), rs.getString(2),rs.getString(3),rs.getString(4), rs.getInt(5),rs.getString(6)));
                
            }
           
            return result;
    
        }
        catch(Exception ex)
        {   
            System.out.println("ERROR getCommonErrorInfo:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

   
    @Override
    public ArrayList<MonitoringW.CommonProjectInfo> updateProjectInfo() throws Exception {
        return null;
        
        
    }
    
   // static String  rootDir = "D:\\work\\pcp_projects";
    static String  rootDir =  "/home/mm_dcp/kino" ;
  // static String  rootDir = System.getProperty("user.home");
   @Override
   public FileListView.DirItem filesDown(String dirpath,boolean dir_only,final String...filter_ext) throws Exception
   {
       
 
        File fileParent;
        try {
            if(dirpath==null){
                    System.out.println(Files.isReadable(new File(rootDir).toPath()));
                  fileParent = new File(rootDir);
                 //System.out.println("filesDown: "+rootDir+" ,"+fileParent);
                  
             }else {
                fileParent = new File(dirpath);
            }
            File[] files =  fileParent.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                    if(pathname.isDirectory())
                        return true;
                    for(String filter:filter_ext){
                        if(pathname.getName().endsWith("."+filter))
                            return true;
                    }
                    return false;
            }

        
            });
             TreeSet<FileListView.FileItem> resSet = new TreeSet<>(new Comparator<FileListView.FileItem>() {

                @Override
                public int compare(FileListView.FileItem o1, FileListView.FileItem o2) {
                     int compare = Boolean.compare(o1.is_dir, o2.is_dir);
                    if(compare!=0)
                        return compare;
                    return o1.name.compareTo(o2.name);
                }
            });

            for(File file:files){
               //  System.out.println("FILE:"+file.getAbsolutePath());
                if(dir_only && !file.isDirectory())
                    continue;
                Path path = file.toPath();
                if (Files.isHidden(path) || !Files.isReadable(path) || Files.isSymbolicLink(path))
                    continue;
                resSet.add(new FileListView.FileItem(file.getName(),file.isDirectory()));

            }
             FileListView.DirItem dir = new FileListView.DirItem(fileParent.getAbsolutePath(),new ArrayList<>(resSet));
             if(fileParent.getAbsolutePath().equals(rootDir))
                dir.is_root=true;
            return dir;
        }catch(Exception  e){
            System.out.println("ERROR:!!!"+e.getLocalizedMessage());
            throw new Exception(e);
        
        }
   
   }
   @Override
   public FileListView.DirItem filesUp(String dirpath,boolean dir_only,final String...filter_ext) throws Exception{
        /*String homeDir = System.getProperty("user.home");
         
        if(fileItem.parentPath.equals(homeDir))
            return null;*/
       
        File parent  = new File(dirpath).getParentFile();
        File[] files =  parent.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                    if(pathname.isDirectory())
                        return true;
                    for(String filter:filter_ext){
                        if(pathname.getName().endsWith("."+filter))
                            return true;
                    }
                    return false;
            }

        
            });
        TreeSet<FileListView.FileItem> resSet = new TreeSet<>(new Comparator<FileListView.FileItem>() {

            @Override
            public int compare(FileListView.FileItem o1, FileListView.FileItem o2) {
                int compare = Boolean.compare(o1.is_dir, o2.is_dir);
                if(compare!=0)
                    return compare;
                return o1.name.compareTo(o2.name);
            }
        });
        
  
        for(File f:files){
            if(dir_only && !f.isDirectory())
                continue;
              Path path = f.toPath();
            if (Files.isHidden(path) || !Files.isReadable(path) || Files.isSymbolicLink(path))
                continue;
                    resSet.add(new FileListView.FileItem(f.getName(),f.isDirectory()));
             
        
        }
        FileListView.DirItem dir = new FileListView.DirItem(parent.getAbsolutePath(),new ArrayList<>(resSet));
        if(parent.getAbsolutePath().equals(rootDir))
            dir.is_root=true;
        
        return dir;
   
   }

    @Override
    public ArrayList<ArrayList<String>> getCountriesAndCitys() throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) {
            ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>(2);
         ArrayList<String> countryList = new ArrayList<>();
         ArrayList<String> cityList = new ArrayList<>();
         result.add(countryList);
         result.add(cityList);
         PreparedStatement pst = db.prepareStatement("SELECT DISTINCT country FROM clients ORDER BY country");
         ResultSet rs = pst.executeQuery();
         while(rs.next())
         {
             countryList.add(rs.getString(1));
         }
         
         pst = db.prepareStatement("SELECT DISTINCT city FROM clients ORDER BY city");
         rs = pst.executeQuery();
         while(rs.next())
         {
             cityList.add(rs.getString(1));
         }
         return result;
        }
        catch(SQLException ex)
        {
            throw new Exception(ex);
        } 
    }

    @Override
    public int addNewContact(int client_id, TheaterItem.ContactData contact) throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) 
         {
            

            PreparedStatement pst_contacts = db.prepareStatement("INSERT INTO contacts (client_id,fio, post,  phone, mail)" 
                +"VALUES (?,?,?,?,?) RETURNING id");
            pst_contacts.setInt(1, client_id);
            pst_contacts.setString(2, trim(contact.fio));
            pst_contacts.setString(3, trim(contact.post));
            pst_contacts.setString(4, trim(contact.phone));
            pst_contacts.setString(5, trim(contact.email));
            ResultSet rs = pst_contacts.executeQuery();
            rs.next();
  
            return rs.getInt(1);
            
        }
        catch(SQLException ex)
        {
            throw new Exception(ex);
        }
    }

    
    static String trim(String str){
        return str==null?null:str.trim();
    }
    @Override
    public int addNewTheater(TheaterItem theater) throws Exception {
        
         try{
             UUID.fromString(trim(theater.main.uniqIdent));
         }
         catch(Exception ex){
         
             throw new Exception("Введен некорректный UUID");
         }
             
             
          
          try(Connection db = DataSourceSettings.dataSource.getConnection()) {
 
        
//         PreparedStatement pst = db.prepareStatement("INSERT INTO clients ("
//                    + "name,country, city, index, street, house, n_contract, contract_date,unique_ident)"
//                    + " VALUES (?,?,?,?,?,?,?,?,?) RETURNING id ");
              
                PreparedStatement pst = db.prepareStatement("INSERT INTO clients ("
                    + "name,country, city, index, street, house, n_contract, contract_date,unique_ident,"
                        + " urid_country, urid_city,urid_index,urid_street, urid_house, urid_phone, urid_fax, urid_mail, " 
                        + " urid_name,urid_director_fio,urid_director_fio_rd,inn,kpp, ogrn, rs, bank, bank_bik, hdd1, hdd2, bios_pass"
                        + ")"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,"
                        + "?,?,?,?,?,?,?,?,?,?,"
                        + "?,?,?,?,?,?,?,?,?) RETURNING id ");
                     
            /*id, city, name, country, index, street, house, n_contract, contract_date, 
            unique_ident, time_last_active, urid_country, urid_city, urid_index, 
            urid_street, urid_home, urid_phone, urid_fax, urid_mail, inn, 
            kpp, ogrn, rs, bank, bank_bik, hdd1, hdd2, bios_pass)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 
            ?, ?, ?, ?, ?, 
            ?, ?, ?, ?, ?, ?, 
            ?, ?, ?, ?, ?, ?, ?, ?);*/
            int index = 1;
            pst.setString(index++, trim(theater.main.name));
            pst.setString(index++, trim(theater.address.county));
            pst.setString(index++, trim(theater.address.city));

            pst.setString(index++, trim(theater.address.index));
            pst.setString(index++, trim(theater.address.street));
            pst.setString(index++, trim(theater.address.house));

            pst.setString(index++, trim(theater.main.contractNumber));
            pst.setDate(index++, new Date(theater.main.contractDate.getTime()));
            pst.setString(index++, trim(theater.main.uniqIdent));
            
     
            pst.setString(index++, trim(theater.uridAdress.county));
            pst.setString(index++, trim(theater.uridAdress.city));

            pst.setString(index++, trim(theater.uridAdress.index));
            pst.setString(index++, trim(theater.uridAdress.street));
            pst.setString(index++, trim(theater.uridAdress.house));
            
            pst.setString(index++, trim(theater.uridAdress.phone));
            pst.setString(index++, trim(theater.uridAdress.fax));
            pst.setString(index++, trim(theater.uridAdress.mail));
            
            pst.setString(index++, trim(theater.uridInfo.name));
            pst.setString(index++, trim(theater.uridInfo.dir_fio));
            pst.setString(index++, trim(theater.uridInfo.dir_fio_rd));
            
            pst.setString(index++, trim(theater.uridInfo.inn));
            pst.setString(index++, trim(theater.uridInfo.kpp));
            
            pst.setString(index++, trim(theater.uridInfo.ogrn));
            pst.setString(index++, trim(theater.uridInfo.rs));
            pst.setString(index++, trim(theater.uridInfo.bank));
                 
            pst.setString(index++, trim(theater.uridInfo.bik));
            pst.setString(index++, trim(theater.main.hdd1));
            pst.setString(index++, trim(theater.main.hdd2));
            pst.setString(index++, trim(theater.main.biospass));
            ResultSet rs = pst.executeQuery();
            
            rs.next();
            return rs.getInt(1);
    
        }
        catch(SQLException ex)
        {
            throw new Exception(ex);
        }
    }
     @Override
    public void updateTheaterInfo(TheaterItem updated) throws Exception {
        
           System.out.println("updateTheaterInfo with "+updated.id);
           try(Connection db = DataSourceSettings.dataSource.getConnection()) {
 
               
           PreparedStatement pst = db.prepareStatement("UPDATE  clients SET ("
                    + "name,country, city, index, street, house, n_contract, contract_date,"
                   + " urid_country, urid_city,urid_index,urid_street, urid_house, urid_phone, urid_fax, urid_mail, " 
                   + " urid_name,urid_director_fio,urid_director_fio_rd,inn,kpp, ogrn, rs, bank, bank_bik,n_server, hdd1, hdd2, bios_pass"
                   + ")"
                    + " = (?,?,?,?,?,?,?,?,?,?,"
                   + "     ?,?,?,?,?,?,?,?,?,?,"
                   + "     ?,?,?,?,?,?,?,?,?) WHERE id = ? ");
            int index = 1;
            pst.setString(index++, trim(updated.main.name));
            pst.setString(index++, trim(updated.address.county));
            pst.setString(index++, trim(updated.address.city));

            pst.setString(index++, trim(updated.address.index));
            pst.setString(index++, trim(updated.address.street));
            pst.setString(index++, trim(updated.address.house));

            pst.setString(index++, trim(updated.main.contractNumber));
            pst.setDate(index++, new Date(updated.main.contractDate.getTime()));
            pst.setString(index++, trim(updated.uridAdress.county));
            pst.setString(index++, trim(updated.uridAdress.city));
 
            pst.setString(index++, trim(updated.uridAdress.index));
            pst.setString(index++, trim(updated.uridAdress.street));
            pst.setString(index++, trim(updated.uridAdress.house));
       
            pst.setString(index++, trim(updated.uridAdress.phone));
            pst.setString(index++, trim(updated.uridAdress.fax));
            pst.setString(index++, trim(updated.uridAdress.mail));
       
            pst.setString(index++, trim(updated.uridInfo.name));
            pst.setString(index++, trim(updated.uridInfo.dir_fio));
            pst.setString(index++, trim(updated.uridInfo.dir_fio_rd));
  
            pst.setString(index++, trim(updated.uridInfo.inn));
            pst.setString(index++, trim(updated.uridInfo.kpp));
   
            pst.setString(index++, trim(updated.uridInfo.ogrn));
            pst.setString(index++, trim(updated.uridInfo.rs));
            pst.setString(index++, trim(updated.uridInfo.bank));
        
            pst.setString(index++, trim(updated.uridInfo.bik));
            pst.setString(index++, trim(updated.main.n_server)); 
            pst.setString(index++, trim(updated.main.hdd1));
            pst.setString(index++, trim(updated.main.hdd2));
            pst.setString(index++, trim(updated.main.biospass));
            
            
            pst.setInt(index++, updated.id);
            int affected_rows =  pst.executeUpdate();
            System.out.println("update theater: affected rows "+affected_rows);
    
    
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: updateTheaterInfo:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

    
    @Override
    public void removeTheater(int client_id,String  client_uniq_id) throws Exception {
 
             try(Connection db = DataSourceSettings.dataSource.getConnection()) {
                 db.setAutoCommit(false);
        
                PreparedStatement pst = db.prepareStatement("DELETE FROM clients_projects_init WHERE client_uniq_id = ?");
                pst.setString(1, client_uniq_id);
                pst.executeUpdate();
                
                pst = db.prepareStatement("DELETE FROM clients_projects_init WHERE client_uniq_ident = ?");
                pst.setString(1, client_uniq_id);
                pst.executeUpdate();
                
                pst = db.prepareStatement("DELETE FROM contacts WHERE client_id = ?");
                pst.setInt(1, client_id);
                pst.executeUpdate();
                
                pst = db.prepareStatement("DELETE FROM clients WHERE id = ?");
                pst.setInt(1, client_id);
                pst.executeUpdate();
                
                db.commit();
    
        }
        catch(SQLException ex)
        {
            
            throw new Exception(ex);
        } 
    }

   
   
    
    @Override
    public String getProjectName(String path) throws  Exception {
         try  {
             
             //CheckNewProjectHelper.validateProject(path);
             File file = new File(path);
             if(file.getName().endsWith("ziptest")){
                 return file.getName();
             }
             return CheckNewProjectHelper.extractProjectName(new File(path));
 
         }catch(ProjectInitException ex){
             System.out.println(ex.getLocalizedMessage());
             throw  new Exception(ex);
             
         }
         
         
    }

  public static long getFolderSize(File dir) {
    long size = dir.length();
    for (File file : dir.listFiles()) {
        if (file.isFile()) {
          
            size += file.length();
        }
        else
            size += getFolderSize(file);
    }
    return size;
}
    @Override
    public Boolean registerProject(final NewSendW.NewProject project,ArrayList<String> uniq_client_ident) throws Exception {
       
            File project_dir = new File(project.project_dir);
            if(!new File(Settings.torrentsdir,project_dir.getName()+".torrent").exists())
            {
                throw  new  Exception("Не найден файл "+project.project_name+".torrent в директории торрентов.");
            }
           
            try(Connection db = DataSourceSettings.dataSource.getConnection()) {

                PreparedStatement pst = db.prepareStatement("SELECT EXISTS (SELECT * FROM projects WHERE folder=? AND status!='error' LIMIT 1)");
                pst.setString(1, project.project_dir);
                ResultSet rs = pst.executeQuery();
                rs.next();
                if(rs.getBoolean(1)==true) //already exists
                    return false;
                
                
                PreparedStatement pst_add_clients = db.prepareStatement("INSERT INTO clients_projects_init(" +
                "   project_id, client_uniq_id)" +
                "   VALUES (?, ?)");
      
                //pst_add_clients.setString(1, project.project_dir);
                
                
                pst = db.prepareStatement("INSERT INTO projects ("
                    + "rus_title,orig_title, name, status, folder,poster,poster_thumb,annotation,folder_base,size)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id ");
                pst.setString(1, project.rus_name.trim());
                pst.setString(2, project.orig_name==null?null:project.orig_name.trim());
                pst.setString(3, project.project_name.trim() );
                pst.setString(4, "check");
                pst.setString(5, project.project_dir.trim());
                
                byte[] img_bytea = null;
                byte[] thumb_bytea = null;
                if(project.poster_path!=null && !project.poster_path.isEmpty())
                {
                    File file_img = new File(project.poster_path);
                    BufferedImage img = ImageIO.read(file_img);
                    Image thumb   =  ImageUtil.squeezedImageWithRatio(img, 256, 256, Image.SCALE_SMOOTH);
                    img_bytea     = ImageUtil.imageToByteArray(img);
                    thumb_bytea   = ImageUtil.imageToByteArray(thumb);
                }
                pst.setBytes(6, img_bytea);
                pst.setBytes(7, thumb_bytea);
                pst.setString(8,project.anotation==null?null: project.anotation.trim());
                
                pst.setString(9, project_dir.getName());
                long folderSize = getFolderSize(project_dir);
                 pst.setLong(10, folderSize);
                rs = pst.executeQuery();
                rs.next();
                final int id = rs.getInt(1);
                project.id = id;
                pst_add_clients.setInt(1, id);
                for(String clinet_id :uniq_client_ident)
                {
                    pst_add_clients.setString(2, clinet_id);
                    pst_add_clients.execute();
                }
                Thread thread = new Thread(new ProjectInitialisation(project));
                thread.start(); 
                return true;
            }
            catch(Exception ex)
            {
                throw new Exception(ex);
            }
        
       
    }

    @Override
    public void debugDelayRequest(int delay) throws Exception {
        Thread.sleep(delay); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PagingLoadResult<ConreteProjectInfo.ConcreteProject> getConcreteProjects(int project_id, int total_size, PagingLoadConfig config) throws Exception {
         
        try(Connection conn=DataSourceSettings.dataSource.getConnection()){
            //Thread.sleep(10000);
             StringBuilder sb = new StringBuilder("SELECT "
                    + "a.id,"  // 1
                    + "c.name,"//2
                    + "c.country,"//3
                    + "c.city,"//4
                    + "a.speed, "//5
                    + "a.time_left,"//6
                    + "a.status,"//7
                    + "a.download_server_command,"//8
                    + "a.download_client_state, "//9
                    + "a.percent, "//10
                    + "a.error, "//11
                    + "a.time_accept,"//12
                    + " COALESCE(EXTRACT(EPOCH FROM (now() AT TIME ZONE 'UTC' - time_last_active AT TIME ZONE 'UTC'))::bigint ,"+Long.MAX_VALUE+") last_update, "//13
                    + " a.time_done,"//14
                     + " a.avg_speed "//15
                    );//9
                    
            if(total_size<0)
            {
                sb.append(",count(*) OVER() AS full_count ");//16
            }
            sb.append(" FROM clients_projects a, clients c ");
         
            String userSort = getSortString(config);
            if(!userSort.isEmpty())
                userSort+=",";
            //System.out.println("Sort info = "+userSort);
            
            
            sb.append(" WHERE    a.client_uniq_ident = c.unique_ident AND a.project_id =  ").append(project_id);
            sb.append(" ORDER BY  ").append(userSort).append(" a.id ");
            sb.append(" OFFSET ").append(config.getOffset());
            sb.append(" LIMIT ").append(config.getLimit());
            
            PreparedStatement pst = conn.prepareStatement(sb.toString());
          
      
            ArrayList<ConreteProjectInfo.ConcreteProject> result = new ArrayList<ConreteProjectInfo.ConcreteProject>();
   
            ResultSet rs = pst.executeQuery();
          
            while(rs.next()){
                if(total_size<0)
                    total_size = rs.getInt(16);
                long avg_speed = rs.getLong(15);
                long last_time_active = rs.getLong(13);
                boolean is_active = (last_time_active <= (60*1));
                long time_done = rs.getTimestamp(14)==null?0:rs.getTimestamp(14).getTime();
                ConreteProjectInfo.ConcreteProject concreteProject = new ConreteProjectInfo.ConcreteProject(
                        rs.getInt(1), 
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        is_active==false?0:rs.getInt(5),
                        avg_speed,
                        rs.getTimestamp(12)==null?0:rs.getTimestamp(12).getTime(),
                        is_active==false?-1:rs.getLong(6),
                        rs.getString(7),
                        rs.getBoolean(8),
                        rs.getBoolean(9),
                        rs.getDouble(10),
                        is_active,
                        time_done);
                if("error".equals(concreteProject.status))
                    concreteProject.error = rs.getString(11);
                result.add(concreteProject);
            }
            return new PagingLoadResultBean<>(result,result.isEmpty()?0:total_size, config.getOffset());
            
        } catch(Exception ex){
 
            System.out.println("ERROR:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        
        }
        
    }

    @Override
    public ConreteProjectInfo.ConcreteInfo getProject(int id) throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) {
             
                
               PreparedStatement pst = db.prepareStatement("SELECT  name "
                       + ",(select COUNT(*) FROM clients_projects WHERE project_id=projects.id)"
                       +" ,(select COUNT(*) FROM clients_projects WHERE project_id=projects.id AND status='done')"
                       + ",(select COUNT(*) FROM clients_projects WHERE project_id=projects.id AND status='error')"
                       + " FROM projects  WHERE id = "+id);
 
                ResultSet rs = pst.executeQuery();
                if(!rs.next())
                    return null;
                return new ConreteProjectInfo.ConcreteInfo(rs.getString(1),rs.getInt(2),rs.getInt(3),rs.getInt(4));
          
            }
            catch(SQLException  ex)
            {
                System.out.println("Error:"+ex.getLocalizedMessage());
                throw new Exception(ex);
            }
    }

    @Override
    public void setClientProjectDownloadStatus(int id_clients_projects,boolean download) throws Exception {
        try(Connection db = DataSourceSettings.dataSource.getConnection()) {
             
                PreparedStatement pst = db.prepareStatement("UPDATE clients_projects " +
                "   SET  download_server_command = ? " +
                " WHERE id = ?");
                pst.setBoolean(1, download);      
                pst.setInt(2, id_clients_projects);
                pst.executeUpdate();
   
          
            }
            catch(SQLException  ex)
            {
                System.out.println("Error:"+ex.getLocalizedMessage());
                throw new Exception(ex);
            }
    }

    @Override
    public TheaterW.TheaterMainData getTheaterMainData(int id) throws Exception {
        
        try(Connection db = DataSourceSettings.dataSource.getConnection()) 
        {

                StringBuilder query = new StringBuilder();
                query.append("SELECT  country, city, name, unique_ident ")
                        .append(" FROM clients ")
                .append(" WHERE id = ")
                .append(id);
       
                
             PreparedStatement pst = db.prepareStatement(query.toString());
        ResultSet rs = pst.executeQuery();
        if(!rs.next())
            return null;
        
        
         return new TheaterW.TheaterMainData(id,rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4));
        }
        catch(SQLException ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

    @Override
    public ArrayList<CleantProjectsWidget.Item> getClientsNotRemovedProjects(String clinet_uniq_ident) throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) 
        {

            
             ArrayList<CleantProjectsWidget.Item> result = new ArrayList<>();
          
             StringBuilder query = new StringBuilder();
             query.append("SELECT a.id,b.name,a.time_done,b.rus_title,b.size,a.status,a.percent  " 
                     +" FROM clients_projects a,projects b WHERE a.project_id = b.id AND a.status NOT IN ('removing','removed') "
                     + " AND a.client_uniq_ident = ?");
             
             PreparedStatement pst =  db.prepareStatement(query.toString());
             pst.setString(1, clinet_uniq_ident);
             
             ResultSet rs = pst.executeQuery();
             
             while(rs.next())
                 result.add(new CleantProjectsWidget.Item(rs.getInt(1),rs.getString(2),rs.getTimestamp(3)==null?0:rs.getTimestamp(3).getTime(),rs.getLong(5),rs.getString(6)));

            return result;
        
            
         
        }
        catch(SQLException ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }
     
   
    @Override
    public void markForRemove(ArrayList<Integer> id_list) throws Exception {
        try(Connection db = DataSourceSettings.dataSource.getConnection()) 
        {

          
             StringBuilder query = new StringBuilder();
             query.append("UPDATE clients_projects SET  status = 'removing' "
                     + " WHERE id=?");
             PreparedStatement pst =  db.prepareStatement(query.toString());
             for(Integer id:id_list){
                pst.setInt(1, id);
                pst.executeUpdate();
             
             }
    
         
        }
        catch(SQLException ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

    @Override
    public void addTheatersToProject(Integer project_id, ArrayList<String> clients_id_list) throws Exception {
        try(Connection db = DataSourceSettings.dataSource.getConnection()) 
        {

              PreparedStatement pst =  db.prepareStatement("SELECT status FROM projects WHERE id = '"+project_id+"'");
              ResultSet rs = pst.executeQuery();
              if(!rs.next())
                  return;
              
              String status = rs.getString(1);
              if(status.equals("error"))
                  throw new IllegalStateException("Ошибка инициализации проекта, добавить пользователей не удалось");
              PreparedStatement pst_bind_clients;
              if(status.equals("check"))
              {
                     pst_bind_clients = db.prepareStatement("INSERT INTO clients_projects_init(" +
                        "   project_id, client_uniq_id)" +
                        "   VALUES (?, ?)");
  

              
              }
              else {
                    pst_bind_clients = db.prepareStatement("INSERT INTO clients_projects("
                    + "project_id,client_uniq_ident )VALUES (?, ?)");
              
              
              }
              pst_bind_clients.setInt(1, project_id);
              for(String client_id:clients_id_list)
              {
                   pst_bind_clients.setString(2, client_id);
                   pst_bind_clients.execute();
              }
              //StringBuilder query = new StringBuilder();
            // query.append("UPDATE clients_projects SET  status = 'removing' "
            //         + " WHERE id=?");
            //  PreparedStatement pst =  db.prepareStatement(query.toString());
            // for(Integer id:id_list){
            //    pst.setInt(1, id);
            //    pst.executeUpdate();
            //  }
    
         
        }
        catch(SQLException ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }

    @Override
    public Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>> getRateLimitData(String client_uniq_ident) throws Exception {
          try (Connection  conn  =  DataSourceSettings.dataSource.getConnection()){
           
              PreparedStatement pst = conn.prepareStatement("SELECT    "
                      + "monday, "
                      + "tuesday, "
                      + "wednesday, "
                      + "thursday, "
                      + "friday, "
                      + "saturday, "
                      + "sunday, " +
                      " confirm " +
                      "  FROM rate_limit_schedule WHERE clinet_uniq_ident = ?");
              pst.setString(1, client_uniq_ident);
              ResultSet rs = pst.executeQuery();
              if(!rs.next()){
                  Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>> map = new HashMap<>();
                  for(RateSchedulerDialog.Day day:RateSchedulerDialog.Day.values()){
                        ArrayList<RateSchedulerDialog.SpeedLimit> data_array = new ArrayList<>(24);
                        map.put(day, data_array);
                        for(int j=0;j<24;++j)
                        {
                             data_array.add( new RateSchedulerDialog.SpeedLimit(day.ordinal(),j));
                        }
                  }
                  return map;
              }
              else {
                  Long[] array1 = (Long[])rs.getArray(1).getArray();
                  Long[] array2 = (Long[])rs.getArray(2).getArray();
                  Long[] array3 = (Long[]) rs.getArray(3).getArray();
                  Long[] array4 = (Long[]) rs.getArray(4).getArray();
                  Long[] array5 = (Long[]) rs.getArray(5).getArray();
                  Long[] array6 = (Long[]) rs.getArray(6).getArray();
                  Long[] array7 = (Long[]) rs.getArray(7).getArray();
                  
                  Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>> map = new HashMap<>();
                  map.put(RateSchedulerDialog.Day.MONDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.MONDAY, array1));
                  map.put(RateSchedulerDialog.Day.TUESDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.TUESDAY, array2));
                  map.put(RateSchedulerDialog.Day.WEDNESDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.WEDNESDAY, array3));
                  map.put(RateSchedulerDialog.Day.THURSDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.THURSDAY, array4));
                  map.put(RateSchedulerDialog.Day.FRIDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.FRIDAY, array5));
                  map.put(RateSchedulerDialog.Day.SATURDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.SATURDAY, array6));
                  map.put(RateSchedulerDialog.Day.SUNDAY,RateSchedulerDialog.SpeedLimit.arrayToList(RateSchedulerDialog.Day.SUNDAY, array7));
                   
                          
              
                  return map;
              }
            
              
        }
         catch(SQLException ex){
             System.out.println("ERROR:"+ex.getLocalizedMessage());
             throw new Exception(ex);
         }
         
    }

    @Override
    public void setRateLimitData(String client_uniq_ident, Map<RateSchedulerDialog.Day, ArrayList<RateSchedulerDialog.SpeedLimit>> data) throws Exception {
        try (Connection  conn  =  DataSourceSettings.dataSource.getConnection()){
            PreparedStatement pst = conn.prepareStatement("SELECT  EXISTS (  " +
            "  SELECT * FROM rate_limit_schedule WHERE clinet_uniq_ident=?)");
            pst.setString(1, client_uniq_ident);
            ResultSet rs = pst.executeQuery();
            rs.next();
            boolean exists = rs.getBoolean(1);
            if(!exists){
              pst = conn.prepareStatement("INSERT INTO rate_limit_schedule( " +
                    "             monday,"
                    + " tuesday, "
                    + " wednesday,"
                    + " thursday, "
                    + " friday,"
                    + " saturday,"
                    + " sunday,  " 
                    + " clinet_uniq_ident,"
                    + " confirm) " +
                    "    VALUES ( ?, ?, ?, ?, ?, ?, ?,  " +
                    "            ?, false);");
                                    
            }
            else {
                 pst = conn.prepareStatement("UPDATE   rate_limit_schedule SET ( "  
                    + "  monday,"
                    + " tuesday, "
                    + " wednesday,"
                    + " thursday, "
                    + " friday,"
                    + " saturday,"
                    + " sunday,  " 
                    + " confirm) = " +
                    "      ( ?, ?, ?, ?, ?, ?, ?,  " +
                    "         false) WHERE clinet_uniq_ident=?;");
            
            
            }
             int index = 0;
            for(RateSchedulerDialog.Day day:RateSchedulerDialog.Day.values()){
                Array array = conn.createArrayOf("bigint",RateSchedulerDialog.SpeedLimit.listToArray(data.get(day)));
               pst.setArray(++index, array);

           }
           pst.setString(++index, client_uniq_ident);
     
           pst.executeUpdate();   
            
            
            
            
            
            
        }  catch(SQLException ex){
             System.out.println("ERROR:"+ex.getLocalizedMessage());
             throw new Exception(ex);
         }
    }

    @Override
    public void removeContact( int contract_id) throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) 
         {
            

            PreparedStatement pst_contacts = db.prepareStatement("DELETE FROM contacts WHERE id = ?");

            pst_contacts.setInt(1, contract_id);
 
            pst_contacts.executeUpdate();
           
            
        }
        catch(SQLException ex)
        {
            throw new Exception(ex);
        }
    }

    @Override
    public void updateContact(TheaterItem.ContactData contact) throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) 
         {

            PreparedStatement pst_contacts = db.prepareStatement("UPDATE contacts SET (fio, post,  phone, mail) = (?,?,?,?) WHERE id = ?");
            pst_contacts.setString(1, contact.fio);
            pst_contacts.setString(2, contact.post);
            pst_contacts.setString(3, contact.phone);
            pst_contacts.setString(4, contact.email);
            pst_contacts.setInt(5, contact.id);
 
            
             int executeUpdate = pst_contacts.executeUpdate();
             System.out.println("executeUpdate "+executeUpdate);
            
        }
        catch(Throwable ex)
        {
            System.out.println(ex.getLocalizedMessage());
                    
            throw new Exception(ex);
        }
    }

    @Override
    public ArrayList<CurrentProjectsGrid.ProjectItem> getCurrentProjectForTheater(String clinet_uniq_ident) throws Exception {
         try(Connection db = DataSourceSettings.dataSource.getConnection()) 
        {
 
             ArrayList<CurrentProjectsGrid.ProjectItem> result = new ArrayList<>();
          
             StringBuilder query = new StringBuilder();
             query.append("SELECT a.id,"
                     + "b.rus_title,"
                     + "a.status,"
                     + "a.percent,"
                      + "a.time_accept,"
                     + "a.time_left,"
                     + "a.speed,"
                     + "a.download_server_command "
                     + " FROM clients_projects a,projects b WHERE a.project_id = b.id "
                     + " AND a.status NOT IN ('removing','removed','done') "
                     + " AND a.client_uniq_ident = ?");
             PreparedStatement pst =  db.prepareStatement(query.toString());
             pst.setString(1, clinet_uniq_ident);
             
             ResultSet rs = pst.executeQuery();
             
             while(rs.next())
             {
                int id = rs.getInt(1);
                String rus_name = rs.getString(2);
                String status = rs.getString(3);
                double percent = rs.getDouble(4);
                 Timestamp timestamp = rs.getTimestamp(5);
                   
               long time_accept = (timestamp==null?0:timestamp.getTime());
                long time_left  = rs.getLong(6);
                long spped  = rs.getLong(7);
                boolean is_downloading = rs.getBoolean(8);
                result.add(new CurrentProjectsGrid.ProjectItem(id, rus_name, status, time_accept, time_left, spped, is_downloading));
             }

            return result;
        
            
         
        }
        catch(Exception ex)
        {
            System.out.println("Error:"+ex.getLocalizedMessage());
            throw new Exception(ex);
        }
    }



   
 
   
    
}
