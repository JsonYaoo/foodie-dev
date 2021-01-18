package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";

    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    @GetMapping("/sso/hello")
    @ResponseBody
    public String hello(){
        return "Hello world~~~";
    }

    /**
     * 验证是否已经登录, 来决定是登录还是重定向换区user会话
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response){
        model.addAttribute("returnUrl", returnUrl);

        // 1、获取userTicket门票, 如果cookie中能够获取到, 证明用户登录过, 此时签发一个一次性的临时门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        // 2、验证userTicket通过, 则创建临时票据并回跳
        if(verifyUserTicket(userTicket)){
            return "redirect:" + returnUrl + "?tmpTicket=" + createTmpTicket();
        }

        // 3. 否则, 代表没有登录过, 去往登录页面
        return "login";
    }

    /**
     * 验证userTicket
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket) {
        // 0、验证CAS门票不能为空
        if(StringUtils.isBlank(userTicket)){
            return false;
        }

        // 1、验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }

        return true;
    }

    /**
     * CAS统一登录接口: 登录页提交登录表单
     *      目的:
     *         1. 登录后创建用户的全局会话(分布式会话) -> redis_user_token:userId : 用户信息 + uniqueToken
     *         2. 创建用户全局门票, 用以表示在CAS客户端登录过, 并已经登录 -> userTicket:
     *         3. 创建用户临时票据, 用于回跳回传, 通过消费临时票据获取分布式用户会话信息 -> tmpTicket:
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        model.addAttribute("returnUrl", returnUrl);

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));
        if (userResult == null) {
            model.addAttribute("errmsg", "用户名或密码不正确");
            return "login";
        }

        // 2. 生成用户token, 存入redis分布式会话
        UsersVO usersVO = new UsersVO();
        String uniqueToken = UUID.randomUUID().toString().trim();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO));

        // 3. 生成全局门票, 代表用户在CAS已登录
        String userTicket = UUID.randomUUID().toString().trim();

        // 4. 用户全局门票放到CAS域的Cookie中
        setCookie(COOKIE_USER_TICKET, userTicket, response);

        // 5. 全局门票关联userId: 代表这个用户有门票了, 可以在各个景区游玩
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());

        // 6. 生成临时票据, 是CAS端签发的一个一次性的临时ticket, 用于给请求端通过消费来获取用户分布式会话
        String tmpTicket = createTmpTicket();

        // 7. 回跳到请求端: 重定向到returnUrl
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    /**
     * 创建临时票据
     * @return
     */
    private String createTmpTicket(){
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            // 设置Redis缓存: redis_tmp_ticket: xxx : 加密后的xxx
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tmpTicket;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {

        // 使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 0. 如果临时票据OK，则需要销毁，并且拿到CAS端cookie中的全局userTicket，以此再获取用户会话
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        } else {
            // 销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        // 1. 验证并且获取用户的userTicket
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 验证成功，返回OK，携带用户会话
        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }

    /**
     * 设置Cookie
     * @param key
     * @param value
     * @param response
     */
    private void setCookie(String key, String value, HttpServletResponse response){
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 获取Cookie
     * @param request
     * @param key
     * @return
     */
    private String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || StringUtils.isBlank(key)){
            return null;
        }

        String cookieValue = null;
        for(int i = 0; i < cookies.length; i++){
            if(key.equals(cookies[i].getName())){
                cookieValue = cookies[i].getValue();
                break;
            }
        }

        return cookieValue;
    }

    /**
     * 删除Cookie
     * @param key
     * @param response
     */
    private void deleteCookie(String key, HttpServletResponse response){
        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    /**
     * 注销登录
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response){
        // 0、获取CAS中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        // 1、清除userTicket票据, redis & cookie
        deleteCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        // 2. 清除用户全局会话(分布式会话)
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        // 3. 返回ok
        return IMOOCJSONResult.ok();
    }

}
