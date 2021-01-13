package com.imooc.controller.interceptor;

import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求: 在Controller执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入postHandle, 被拦截...");

        // 从header中获取前端传入的Token => 之所以存到header中, 是因为需要和业务实体解耦合
        String headerUserId = request.getHeader("headerUserId");
        String headerUserToken = request.getHeader("headerUserToken");

        // 校验必填
        if(StringUtils.isBlank(headerUserId) || StringUtils.isBlank(headerUserToken)){
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }

        // 校验Token是否正确
        String redisUserToken = redisOperator.get(REDIS_USER_TOKEN + ":" + headerUserId);
        if(StringUtils.isBlank(redisUserToken)){
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }
        if(!headerUserToken.equals(redisUserToken)){
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("账号可能在异地登录..."));
            return false;
        }

        // false: 请求被拦截, true: 请求放行
        return true;
    }

    /**
     * 回写Result到页面
     * @param response
     * @param result
     */
    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json");

        OutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(JsonUtils.objectToJson(result).getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拦截: 在Controller执行之后, 但在视图渲染之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 拦截: 在视图渲染之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
