package com.incorta.webapi;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ApiKeyFilter implements Filter {

    private final String apiKey;

    public ApiKeyFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        String auth = httpServletRequest.getHeader("Authorization");
        String method = httpServletRequest.getMethod();

        //Handles CORS request....
        //FIXME: find a better way to handle CORS requests
        if (method.equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }
        
        if (apiKey.contentEquals(auth)) {
            chain.doFilter(request, response);

        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing
    }

    @Override
    public void destroy() {
        //do nothing
    }
}
