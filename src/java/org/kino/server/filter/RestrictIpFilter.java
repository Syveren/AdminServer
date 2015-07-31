/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kirio
 */
 
public class RestrictIpFilter implements Filter {
  private FilterConfig config;
  // the regex must define whole string to match - for example a substring without .* will not match
  // note the double backslashes that need to be present in Java code but not in web.xml
  private String IP_FULL_ACCESS_REG_EXP = "127.0.0.1";
  // private String IP_REGEX = "172\\.20\\..*";
 private String FULL_ACCESS_BACKDOOR_SECRET = null;
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.config = filterConfig;
    // optionally you can get regex from init parameter overwriting the class' private variable
    IP_FULL_ACCESS_REG_EXP = config.getInitParameter("IP_FULL_ACCESS_REG_EXP");
    FULL_ACCESS_BACKDOOR_SECRET = config.getInitParameter("FULL_ACCESS_BACKDOOR_SECRET");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    String ip = request.getRemoteAddr();
 
    if (response instanceof HttpServletResponse) {
 
        HttpServletResponse httpResp = (HttpServletResponse) response;
        if (ip.matches(IP_FULL_ACCESS_REG_EXP)) {
            chain.doFilter(request, response);
        }
        else {
            HttpServletRequest httpReq = (HttpServletRequest) request;
            
            if(httpReq.getRequestURI().matches(".*/api"))
                chain.doFilter(request, response);
            else  if(httpReq.getRequestURI().matches(".*/(clientupdate|clientversion|contractgenerator|gettorrent)"))
                chain.doFilter(request, response);
            else {
                HttpSession session = httpReq.getSession(); 
                if(Boolean.TRUE.equals(session.getAttribute("tmp_access_granted")))
                {
                    chain.doFilter(request, response);
                    return;
                }
                
                if(FULL_ACCESS_BACKDOOR_SECRET!=null 
                        && FULL_ACCESS_BACKDOOR_SECRET.equalsIgnoreCase(httpReq.getParameter("secret"))){
                    chain.doFilter(request, response);
                    session.setAttribute("tmp_access_granted",true);
                    session.setMaxInactiveInterval(30 * 60);//30 min
                    
                }
                else {
                    httpResp.sendError(HttpServletResponse.SC_FORBIDDEN,"403 Forbidden: You shall not pass!");
                   
                }
            
            }
        
        }
            
      
            
        
    }
  }

    @Override
    public void destroy() {
         
    }
 
}
 