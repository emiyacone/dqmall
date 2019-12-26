package com.dqmall.controller.backend;

import com.dqmall.common.Const;
import com.dqmall.common.ServerResponse;
import com.dqmall.pojo.User;
import com.dqmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @program: dqmall
 * @description: 管理员控制类
 * @author: 唐庆阳
 * @create: 2019-12-14 13:38
 **/
@Controller
@RequestMapping("/manager/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>  login(String username, String password, HttpSession session)
    {
        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            User user=response.getData();
            if(user.getRole()== Const.Role.ROLE_ADMIN)
            {
                //说明登陆的是管理员
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }
            else{
                return ServerResponse.createByErrorMessage("不是管理员无法登录");
            }

        }
        return response;
    }
}
