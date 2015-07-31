/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.api;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.kino.server.structs.DataSourceSettings;

/**
 *
 * @author kirio
 */
public class contractgenerator extends HttpServlet {

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
    private final static SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy");
    private final static SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
    String nullToEmpty(String str){
        return str==null?"":str;
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        Integer id = (tryParseInt(request.getParameter("id")));
        if(id==null)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"parametr id requared");
            return;
        }
        
        HashMap<String,String> replacementMap = new HashMap<>(30);
        try(Connection db = DataSourceSettings.dataSource.getConnection()) {
 
            StringBuilder query = new StringBuilder();
            query.append("SELECT n_server,hdd1, hdd2,n_contract,contract_date,"  // 1-9
                + " country, city, index, street, house, "//10-14
                + " urid_country, urid_city, urid_index, urid_street, urid_house,urid_phone,urid_fax,urid_mail, "//15-22
                + " urid_name,urid_director_fio,urid_director_fio_rd,inn, kpp, ogrn,rs,bank,bank_bik ")//23-31
                .append(" FROM clients WHERE id = ?")
                .append(" LIMIT 1");
            PreparedStatement pst = db.prepareStatement(query.toString());
          
 
            pst.setInt(1, id);
        
            ResultSet rs = pst.executeQuery();
            if(!rs.next())
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"project not found");
                return;
            }
            int rindex = 1;
            
            String n_server = rs.getString(rindex++);
            String hdd1 = rs.getString(rindex++);
            String hdd2 = rs.getString(rindex++);
            String n_contract = rs.getString(rindex++);
            Date contract_date = rs.getDate(rindex++);
            String country = rs.getString(rindex++);
            String city = rs.getString(rindex++);
            String index = rs.getString(rindex++);
            String street = rs.getString(rindex++);
            String house = rs.getString(rindex++);
            String urid_country = rs.getString(rindex++);
            String urid_city = rs.getString(rindex++);
            String urid_index = rs.getString(rindex++);
            String urid_street = rs.getString(rindex++);
            String urid_house = rs.getString(rindex++);
            String urid_phone = rs.getString(rindex++);
            String urid_fax = rs.getString(rindex++);
            String urid_mail = rs.getString(rindex++);
            String urid_name = rs.getString(rindex++);
            String urid_director_fio = rs.getString(rindex++);
            String urid_director_fio_rd = rs.getString(rindex++);
          
            String inn = rs.getString(rindex++);
            String kpp = rs.getString(rindex++);
            String ogrn = rs.getString(rindex++);
            String ras_schet = rs.getString(rindex++);
            String bank = rs.getString(rindex++);
            String bank_bik = rs.getString(rindex++);
            
            replacementMap.put("{Server_number}", nullToEmpty(n_server));
            replacementMap.put("{HDD1}", nullToEmpty(hdd1));
            replacementMap.put("{HDD2}", nullToEmpty(hdd2));
            replacementMap.put("{Adr_inst_Country}", nullToEmpty(country));
            replacementMap.put("{Adr_inst_City}", nullToEmpty(city));
            replacementMap.put("{Adr_inst_ZIP}", nullToEmpty(index));
            replacementMap.put("{Adr_inst_Street}", nullToEmpty(street));
            replacementMap.put("{Adr_inst_bld}", nullToEmpty(house));
            replacementMap.put("{Adr_reg_Country}", nullToEmpty(urid_country));
            replacementMap.put("{Adr_reg_City}", nullToEmpty(urid_city));
            replacementMap.put("{Adr_reg_ZIP}", nullToEmpty(urid_index));
            replacementMap.put("{Adr_reg_Street}",nullToEmpty( urid_street));
            replacementMap.put("{Adr_reg_bld}", nullToEmpty(urid_house));
            replacementMap.put("{Adr_reg_tel}", nullToEmpty(urid_phone));
            replacementMap.put("{Adr_reg_fax}", nullToEmpty(urid_fax));
            replacementMap.put("{Adr_reg_email}", nullToEmpty(urid_mail));
            replacementMap.put("{Comp_name}", nullToEmpty(urid_name));
            replacementMap.put("{Comp_dir_name}", nullToEmpty(urid_director_fio));
            replacementMap.put("{Comp_dir_name_rod}", nullToEmpty(urid_director_fio_rd));
            replacementMap.put("{Comp_INN}", nullToEmpty(inn));
            replacementMap.put("{Comp_KPP}", nullToEmpty(kpp));
            replacementMap.put("{Comp_OGRN}", nullToEmpty(ogrn));
            replacementMap.put("{Comp_acc}", nullToEmpty(ras_schet));
            replacementMap.put("{Comp_bank}", nullToEmpty(bank));
            replacementMap.put("{Comp_BIK}", nullToEmpty(bank_bik));

            replacementMap.put("{Contract_number}", nullToEmpty(n_contract));
            replacementMap.put("{Contract_date}",contract_date==null?"": sdf_date.format(contract_date));
            
         }catch(SQLException ex)
         {
             response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,ex.getLocalizedMessage());
             return;
         }         
        Date currentDate = new Date();
        replacementMap.put("{Текущая дата}", sdf_date.format(currentDate));
        replacementMap.put("{Год}", sdf_year.format(currentDate));
       
        // Пишем во временный буфер. Можно было бы сразу писать в responce.out, но тогда
        // мы не установим размер файла
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
     
            
            try(InputStream srcis =  new FileInputStream(template_url)){
                System.out.println("is:"+srcis);    
                writeDocxTemplate(srcis, baos, replacementMap);
            }
            catch(Throwable ex)
            {
                System.out.println("ERROR:"+ex.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,ex.getLocalizedMessage());
                return;

            }

            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            //response.setContentType("application/octet-stream");;
            response.setHeader( "Content-Disposition",String.format("attachment; filename=\"%s\"", "contract.docx"));
            response.setContentLength((int) baos.size());
       
            try(OutputStream out = response.getOutputStream())
            {
               out.write(baos.toByteArray());

            }
        
        }

        
        
    }
    
    
    private static final String template_url = "/opt/jboss-eap-6.3/modules/system/layers/base/org/mosfilm/main/contract_template.docx";
    
    static private long replaceInParagraphs(Map<String, String> replacements, List<XWPFParagraph> xwpfParagraphs) {
    long count = 0;
    for (XWPFParagraph paragraph : xwpfParagraphs) {
      List<XWPFRun> runs = paragraph.getRuns();

      for (Map.Entry<String, String> replPair : replacements.entrySet()) {    
        String find = replPair.getKey();
        String repl = replPair.getValue();
        TextSegement found = paragraph.searchText(find, new PositionInParagraph());
        if ( found != null ) {
          count++;
          if ( found.getBeginRun() == found.getEndRun() ) {
            // whole search string is in one Run
            XWPFRun run = runs.get(found.getBeginRun());
            String runText = run.getText(run.getTextPosition());
            String replaced = runText.replace(find, repl);
            run.setText(replaced, 0);
          } else {
            // The search string spans over more than one Run
            // Put the Strings together
            StringBuilder b = new StringBuilder();
            for (int runPos = found.getBeginRun(); runPos <= found.getEndRun(); runPos++) {
              XWPFRun run = runs.get(runPos);
              b.append(run.getText(run.getTextPosition()));
            }                       
            String connectedRuns = b.toString();
            String replaced = connectedRuns.replace(find, repl);

            // The first Run receives the replaced String of all connected Runs
            XWPFRun partOne = runs.get(found.getBeginRun());
            partOne.setText(replaced, 0);
            // Removing the text in the other Runs.
            for (int runPos = found.getBeginRun()+1; runPos <= found.getEndRun(); runPos++) {
              XWPFRun partNext = runs.get(runPos);
              partNext.setText("", 0);
            }                          
          }
        }
      }      
    }
    return count;
  }
   
   
  static void writeDocxTemplate(InputStream src,OutputStream dststrem,Map<String,String> replacementMap) throws InvalidFormatException, IOException{
        XWPFDocument doc = new XWPFDocument(src); 
        replaceInParagraphs(replacementMap,doc.getParagraphs()); 
        for (XWPFTable tbl : doc.getTables()) {
           for (XWPFTableRow row : tbl.getRows()) {
              for (XWPFTableCell cell : row.getTableCells()) {
                      replaceInParagraphs(replacementMap,cell.getParagraphs());

              }
           }
        }
       
        doc.write(dststrem);
        
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
