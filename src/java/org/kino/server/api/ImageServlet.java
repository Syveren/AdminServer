/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kino.server.api;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.kino.server.structs.DataSourceSettings;
import org.kino.server.structs.ImageUtil;

/**
 *
 * @author kirio
 */
public class ImageServlet extends HttpServlet {

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
        
        response.setContentType("image/png");
        try(ServletOutputStream os = response.getOutputStream()){
        //Connection conn = null;
     
              
               
                String path = request.getParameter("path");
                Integer project_id =  tryParseInt(request.getParameter("project_id")); 
           
                if(path==null && project_id==null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                } 
                File file;
                
                
                byte[] bytes;
            
            if(path!=null){
                file = new File(path);
                 BufferedImage img = ImageIO.read(file);
                 Image thumb =  ImageUtil.squeezedImageWithRatio(img, 500, 500, Image.SCALE_SMOOTH);
                 bytes = ImageUtil.imageToByteArray(thumb);
            }    
            else {
                
                   try (Connection conn = DataSourceSettings.dataSource.getConnection())
                    {
             
                        PreparedStatement pst = conn.prepareStatement("SELECT poster_thumb FROM projects WHERE id=?");
                        pst.setInt(1, project_id);
                        ResultSet rs = pst.executeQuery();
                        if(!rs.next()){
                            file = new File(path);
                            BufferedImage img = ImageIO.read(file);
                            Image thumb =  ImageUtil.squeezedImageWithRatio(img, 30, 30, Image.SCALE_SMOOTH);
                            bytes = ImageUtil.imageToByteArray(thumb);
                        }
                        else 
                            bytes = rs.getBytes(1);
                        
                       // file = new File("src/java/org/kino/client/img/plus.png");
                    }
            }
                
             
    
           
            //Image thumb =  ImageUtil.squeezedImageWithRatio(img, 32, 32, Image.SCALE_SMOOTH);
            os.write(bytes); 
            /* conn =  DataSourceHelper.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT image_byte from ttx_images"
            +" WHERE id_doc = ? AND image = ? LIMIT 1");
            st.setInt(1, Integer.parseInt(iddoc));
            st.setString(2,imgid);
            ResultSet rs = st.executeQuery();
            if(!rs.next())
            {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
            "Изображение с указанными идентификаторами отсутствует.");
            return;
            }*/
            // byte[] result = rs.getBytes(1);
            // os.write(result); 
           
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
