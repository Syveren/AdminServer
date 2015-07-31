/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

/**
 *
 * @author kirio
 */
public class ApiServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
 
 
/*
    List<Command> getCommands(int theater_id) throws SQLException{
        try(Connection db = DataSourceHelper.getConnection()) { 
            PreparedStatement pst = db.prepareStatement("SELECT id, type, json_params FROM commands "
                     + " WHERE client_id=? AND time_accept IS NULL");
            pst.setInt(1, theater_id);
            ResultSet rs = pst.executeQuery();
            ArrayList<Command> com_list = new ArrayList<>();
            while(rs.next()){
                int com_id = rs.getInt(1);
                String type = rs.getString(2);
                String param_json = rs.getString(3);
                //{"param2":"value2","param":"value"}
                HashMap<String,String> params = null;
                if(param_json!=null){
                     params = gson.fromJson(param_json, new TypeToken<HashMap<String,String>>(){}.getType());
                    
                }
                com_list.add(new Command(com_id,type, params));
            
            }
            return com_list;
       
        }
        
 
    
    }
    
    private static Integer tryParseInt(String num){
        if(num==null)
            return null;
        try{
            return  Integer.valueOf(num);
        }catch(NumberFormatException e){
            return null;
        }
        
    }
    
    private static final Gson gson = GsonHelper.instance;*/
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,"api not implemented yet");
        return;
        /*try (PrintWriter out = response.getWriter()) {
            
            System.out.println("ApiServlet, ip:"+request.getRemoteAddr()+",method:"+request.getMethod());
            Integer par_unique_ident = tryParseInt(request.getParameter(MsgParam.CLIENT_ID));
            String par_msg_type = request.getParameter(MsgParam.MSG_TYPE);
 
            if(par_unique_ident==null || par_msg_type==null)
            {
                System.out.println("ERROR: par_unique_ident==null || par_msg_type==null");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            int client_id = getClientIdByUniqueIden(par_unique_ident);
            if(client_id==0){
                System.out.println("WARNING: record with unique_ident ='"+par_unique_ident+"' not found");
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"record with unique_ident ='"+par_unique_ident+"' not found");
                return;
            }
            
            System.out.println("INFO: unique_ident='"+par_unique_ident+"' type='"+par_msg_type+"'");
            if(par_msg_type.equals(ClientRequestType.CLIENT_REQ_TYPE_CHECK))
            {
                
                // Клиент посылает запрос и напоминает о свем существовани
                // Обнволяем таблицу последнего запроса клиента
                // Проверяем есть ли для клиента команды и посылаем их если есть
                String ip = request.getRemoteAddr();
                updateClientLastCheck(client_id,ip);
                List<Command> commandList = getCommands(client_id);
                if(commandList.isEmpty())
                {
                     out.print(ServerResponceType.SERVER_RESP_TYPE_OK);
                }
                else 
                {
                    String jsomCommands = gson.toJson(commandList);
                    out.print(jsomCommands);
                
                }
             
            }
            else if(par_msg_type.equals(ClientRequestType.CLIENT_REQ_TYPE_COMM_ACCEPTED))
            {
                // Клиент посылает этот запрос. Это значит, что он принял команды и начал их обработку
                // параметром MsgParam.COMMAND_DATA он посылает идентификаторы принятых команд
                // Помечаем в бд данные команды, как принятые
                String json_commands_accepted = request.getParameter(MsgParam.COMMAND_DATA);
                if(json_commands_accepted==null)
                {
                     System.out.println("ERROR: json_commands_accepted  = "+null);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }   
                List<Integer> commandsAccepted = gson.fromJson(json_commands_accepted, new TypeToken<List<Integer>>(){}.getType());
                setCommandsAsAccepted(commandsAccepted);
                out.print(ServerResponceType.SERVER_RESP_TYPE_OK);
                
            }
            else if(par_msg_type.equals(ClientRequestType.CLIENT_REQ_TYPE_COMM_DONE))
            {  
            
                // Клиент посылает этот запрос. Это значит, что он принял обработал команды 
                // Помещаем результат в таблицу результат команд
                String json_commands_responce = request.getParameter(MsgParam.COMMAND_DATA);
                if(json_commands_responce==null)
                {
                    System.out.println("ERROR: json_commands_responce  = "+null);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }   
                List<CommandResponce> commandResponceList = gson.fromJson(json_commands_responce, new TypeToken<List<CommandResponce>>(){}.getType());
                registerCommandResponce(client_id, commandResponceList);
  
            }
            else { 
                System.out.println("ERROR: unknown type  = "+par_msg_type);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            out.flush();
           
        }catch(JsonSyntaxException e){
            System.out.println("ERROR:"+e.getLocalizedMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            
        }
        catch(IOException|SQLException e2){
            System.out.println("ERROR:"+e2.getLocalizedMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
        }*/
    }
  
    /*
    void setCommandsAsAccepted(List<Integer> commands_id)  {
        
         
        try(Connection db = DataSourceHelper.getConnection()) { 
            StringBuilder sb = new StringBuilder();
            for(Integer id:commands_id)
            {
                if(sb.length()!=0)
                    sb.append(",");
                sb.append(id);
            }
            PreparedStatement pst =  db.prepareStatement("UPDATE commands SET (time_accept)=(now()) WHERE id IN ("+sb.toString()+")");
            pst.executeUpdate();

        }
        catch(SQLException ex){
            System.out.println("ERROR:"+ex.getLocalizedMessage());
        }
         
    }
    int getClientIdByUniqueIden(int unique_ident) throws SQLException
    {
         try(Connection db = DataSourceHelper.getConnection()) 
         { 
            PreparedStatement pst =  db.prepareStatement("SELECT id FROM clients WHERE unique_ident = ?"); 
            pst.setInt(1, unique_ident);
            ResultSet rs = pst.executeQuery();
            if(rs.next())
                return rs.getInt(1);
            else 
                return 0;
           
        }
    }
    
    void saveCommandResponceParameters(int client_id,CommandResponce commandResponce,Connection db) throws SQLException{
        if(commandResponce.getError()!=null)
            return;
        
        PreparedStatement pst_update =  db.prepareStatement("UPDATE client_remote_data SET (value,date_create)=(?,now()) WHERE client_id=? AND key=?"); 
        PreparedStatement pst_insert =  db.prepareStatement("INSERT INTO client_remote_data" 
                     +"(client_id, key, value, date_create) VALUES (?,?,?,now())" );
        
        Set<Map.Entry<String, String>> entrySet = commandResponce.getResult().entrySet();
        for(Map.Entry<String, String> enry:entrySet)
        {
           // if(!enry.getKey().startsWith(Command.PrefixInfoData))
             //   continue;
            
            pst_update.setString(1, enry.getValue());
            pst_update.setInt(2, client_id);
            pst_update.setString(3, enry.getKey());
            
            int i = pst_update.executeUpdate();
            if(i==0){
                 pst_insert.setInt(1, client_id);
               pst_insert.setString(2, enry.getKey());
                pst_insert.setString(3, enry.getValue());
  
                pst_insert.execute();
            
            }
        }

 
    }
    
    void registerCommandResponce(int client_id,List<CommandResponce> commandResponceList) throws SQLException  {
       
         
         try(Connection db = DataSourceHelper.getConnection()) { 
            
            PreparedStatement pst_add =  db.prepareStatement("INSERT INTO commands_responce(" 
                    +"id,error, json_result, time_done, client_id,type) " 
                   +" SELECT a.id, ?, ?,now(),?,a.type FROM commands a WHERE a.id=?");
           // PreparedStatement pst_remove =  db.prepareStatement("DELETE FROM commands WHERE id = ?"); 
            for(CommandResponce commandResponce:commandResponceList)
            {
                try {
                    db.setAutoCommit(false);
                    pst_add.setString(1, commandResponce.getError());
                    pst_add.setString(2, gson.toJson(commandResponce.getResult()));
                    pst_add.setInt(3, client_id);
                    pst_add.setInt(4, commandResponce.getId());

                    pst_add.execute();
                    saveCommandResponceParameters(client_id, commandResponce, db);
                    db.commit();
                
                }catch(SQLException ex){
                    System.out.println("ERROR:"+ex.getLocalizedMessage());
                }
            }
        }
    }
    
    void updateClientLastCheck(Integer client_id,String ip_address) {
        try(Connection db = DataSourceHelper.getConnection()) { 
        PreparedStatement pst =  db.prepareStatement("UPDATE client_last_check SET (ip_address,time_last_active)=(?,now()) WHERE client_id=?"); 
        pst.setString(1, ip_address);
        pst.setInt(2, client_id);
        int i = pst.executeUpdate();
        if(i==0){
             pst = db.prepareStatement("INSERT INTO client_last_check  (client_id,ip_address,time_last_active) VALUES (?,?,now())"); 
             pst.setInt(1, client_id);
             pst.setString(2, ip_address);
             pst.execute();
         }
      
        }catch(SQLException e){
            System.out.println("ERROR:"+e.getLocalizedMessage());
        }
    
    
    }
    
    
    
    /*void pritFileList(PrintWriter out,File parent,int depth){
        if (!Files.isReadable(parent.toPath()) || Files.isSymbolicLink(parent.toPath())) 
              return;
        StringBuilder sb=new StringBuilder("-");
        for(int i=0;i<depth;++i)
        {
            sb.append("-");
        }
        
        out.println(sb.toString()+ parent.getName()+"<br>");    
        if(parent.isDirectory())
            for(File child:parent.listFiles())
                pritFileList(out,child,depth+1);
    
        
    }*/
    
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