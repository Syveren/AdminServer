/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.kino.server.structs.Settings;

/**
 *
 * @author kirio
 */
public class clientupdate extends HttpServlet {
 
    boolean isClientExist(String id){
        return true;
    }
    File getLastVersionApplication() {
        File parent = new File(Settings.updatedir);
        if(!parent.exists()) 
           return null;
        File[] list = parent.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".zip");
            }
        });
        if(list.length==0)
            return null;
        if(list.length==1)
            return list[0];
        Arrays.sort(list,new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return list[0];
    }
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        OutputStream outStream = null;
        FileInputStream fis = null;
        try {
 
            File lastVersionApplication = getLastVersionApplication();
            if(lastVersionApplication==null)
                return;
            if(!lastVersionApplication.exists())
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"file not found");
                return;
            
            }
  
            outStream = response.getOutputStream();
  
            response.setHeader("Content-Disposition", "  ; filename=\""+lastVersionApplication.getName()+"\"");
            String mimetype = "application/octet-stream";
            response.setContentType(mimetype);
            response.setContentLength((int)lastVersionApplication.length());
            byte[] buffer = new byte[response.getBufferSize()];
            fis = new FileInputStream(lastVersionApplication);
            int len = fis.read(buffer);
            while(len>0)
            {  
                outStream.write(buffer,0,len);
                len = fis.read(buffer);
            }
            fis.close();
            outStream.flush();
                
       } 
       finally {
            if(outStream!=null)
                outStream.close();
            if(fis!=null)
                fis.close();
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
