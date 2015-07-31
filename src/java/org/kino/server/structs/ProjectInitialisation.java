/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.structs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.kino.client.broadcast.NewSendW;
 


/**
 *
 * @author kirio
 */
public class ProjectInitialisation implements Runnable{

    //static private final ConcurrentLinkedQueue<Integer> checkingProject = new ConcurrentLinkedQueue<Integer>();
    
    final NewSendW.NewProject project; 
    public ProjectInitialisation(final NewSendW.NewProject project) 
    {
        this.project = project;
   
    }
    
    
     
    @Override
    public void run() {
  
           try(Connection db = DataSourceSettings.dataSource.getConnection()) {
               
               
               
            System.out.println("begin check project: " +project.project_dir );   
            
    
            PreparedStatement pst = db.prepareStatement("UPDATE  projects set (status,error)=(?,?)"
                               + " WHERE id ='"+project.id+"'");
            
                               
            PreparedStatement  pst_remove_clients = db.prepareStatement("DELETE FROM  clients_projects_init WHERE " +
                "   project_id = '"+project.id+"' RETURNING client_uniq_id");
             
      
             if(project.project_dir.endsWith("ziptest")){
                
             }
             else {
             
            try {
                CheckNewProjectHelper.validateProject(project.project_dir);
                StringBuilder sb_file_mismatch = new StringBuilder();
                boolean  check = CheckNewProjectHelper.check(project,sb_file_mismatch);
                if(!check) // hash mismatch
                {
                   System.out.println("check summ missmatch for file: " +sb_file_mismatch.toString() );   
                
                    pst.setString(1, "error");
                    pst.setString(2, "Контрольная сумма не совпадает у файла:'"
                            +sb_file_mismatch.toString()+"'");
                  
                    pst.executeUpdate();
                    pst_remove_clients.executeQuery();
                    return;
                }
            }
            catch(CheckNewProjectHelper.ProjectInitException e)
            { 
                 System.out.println("ProjectInitException'"
                            +e.getLocalizedMessage()+"'");   
                pst.setString(1, "error");
                pst.setString(2, e.getLocalizedMessage());
                pst.executeUpdate();
                pst_remove_clients.executeQuery();
                return;
            }
             }  
            
          
                
            try{
            db.setAutoCommit(false);
         

            pst.setString(1, "ready");
            pst.setString(2, null);
            pst.executeUpdate();


                // if(uniq_client_ident!=null)
            ResultSet clients_rs = pst_remove_clients.executeQuery();
            PreparedStatement pst_bind_clients = db.prepareStatement("INSERT INTO clients_projects("
            + "client_uniq_ident, project_id)VALUES (?, ?)");

            while(clients_rs.next())
            {   
                String uniq_ident = clients_rs.getString(1);
                pst_bind_clients.setString(1, uniq_ident);
                pst_bind_clients.setInt(2, project.id);
                pst_bind_clients.execute();
            }

           // }


            db.commit();
            }
            catch(SQLException ex)
            {
                 // rollback
                 db.rollback();
                 db.setAutoCommit(true);
                 System.out.println("SQLException'"+ex.getLocalizedMessage()+"'");   
                 pst.setString(1, "error");
                 pst.setString(2, ex.getMessage());
                 pst.executeUpdate(); 
                 pst_remove_clients.executeQuery();
                 db.commit();
            }
           
            
 
        }catch(SQLException ex)
        {  
 
            System.out.println("SEVERE ERROR:"+ex.getLocalizedMessage());
          //  throw new Exception(ex);
        }
        catch(Throwable e){
            System.out.println("SEVERE ERROR:"+e.getLocalizedMessage());
             e.printStackTrace();
            
         }
    }
    
    
}
