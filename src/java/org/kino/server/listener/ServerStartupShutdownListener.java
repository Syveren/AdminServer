/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author kirio
 */
public class ServerStartupShutdownListener implements ServletContextListener{

    //static ExecutorService timer_vpnip_realip_loader = null;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
   
       // timer_vpnip_realip_loader =  Executors.newSingleThreadExecutor();
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
      /* timer_vpnip_realip_loader.shutdown();
       try{
            timer_vpnip_realip_loader.awaitTermination(10, TimeUnit.SECONDS);
       }catch(InterruptedException e){}*/
    }
    
    
    
    
    
    private class VpnIpAndReilIpLoader {
        
    
    }
}
 