/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.kino.server.structs.DataSourceSettings;
import org.kino.server.structs.Settings;

/**
 *
 * @author kirio
 */
public class GetTorrent extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
      private static Integer tryParseInt(String num){
        if(num==null)
            return null;
        try{
            return  Integer.valueOf(num);
        }catch(NumberFormatException e){
            return null;
        }
        
    }
      
      
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer project_id =  tryParseInt(request.getParameter("project")); 
        if(project_id==null) 
        {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
        } 
          
        try (Connection conn = DataSourceSettings.dataSource.getConnection())
        {

            PreparedStatement pst = conn.prepareStatement("SELECT folder_base FROM projects WHERE id=?");
            pst.setInt(1, project_id);
            ResultSet rs = pst.executeQuery();
            if(!rs.next()){
                 response.sendError(HttpServletResponse.SC_NOT_FOUND,"project not found");
                 System.out.println("project with id="+project_id+" not found");
                 return;
            }
            
            String name = rs.getString(1)+".torrent";
            //name = new File(Settings.torrentsdir).listFiles()[0].getName();
            File file = new File(Settings.torrentsdir+name);
            if(!file.exists())
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"torrent not found");
                System.out.println("torrent with name "+file.toString()+" not found");
                 return;
            }
            
            
 
            response.setContentType("application/octet-stream");
            response.setContentLength((int) file.length());
            response.setHeader( "Content-Disposition",
            String.format("attachment; filename=\"%s\"", file.getName()));
            
            
            try (OutputStream out = response.getOutputStream()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                out.flush();
            }
           
        }
        catch(Exception ex){
            System.out.println("ERROR:"+ex);
        }
        
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
