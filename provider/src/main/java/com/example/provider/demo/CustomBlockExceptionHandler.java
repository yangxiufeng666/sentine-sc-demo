package com.example.provider.demo;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Mr.Yangxiufeng
 * @date 2020-12-16
 * @time 16:06
 */
@Component
@Slf4j
public class CustomBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        log.error("block " , e );
        if (e instanceof FlowException){
            log.error("流量太大被限流啦，请求URL是{}，资源信息：{}",httpServletRequest.getRequestURI(),e.getRule());
        }else if (e instanceof DegradeException){
            log.error("服务被降级啦，请求URL是{}，资源信息：{}",httpServletRequest.getRequestURI(),e.getRule());
        }else if (e instanceof ParamFlowException){
            ParamFlowException ex = (ParamFlowException) e;
            log.error("ParamFlowException 参数热点限流，资源名:{}，参数:{}，资源信息:{}", ex.getResourceName(), ex.getLimitParam(), JSON.toJSONString(ex.getRule()));
        }else if (e instanceof AuthorityException) {
            log.error("AuthorityException 授权规则，资源信息：{}" , JSON.toJSONString(e.getRule()));
        }
        String json = "{\"code\": 429, \"msg\": \"系统繁忙，请稍后再试\"}";
        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.print(json);
        out.flush();
        out.close();
    }
}

