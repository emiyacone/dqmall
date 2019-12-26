package com.dqmall.controller.protal;

import com.dqmall.common.Const;
import com.dqmall.common.ResponseCode;
import com.dqmall.common.ServerResponse;
import com.dqmall.pojo.User;
import com.dqmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @program: dqmall
 * @description: 用户控制类
 * @author: 唐庆阳
 * @create: 2019-09-20 09:12
 **/
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
/**
 *@Description 用户登录
 *@methodname login
 *@params [username, password]
 *@return java.lang.Object
 *@author 唐庆阳
 *@Date 2019-09-20
 *@other
 */
@RequestMapping(value = "login.do",method = RequestMethod.POST)
@ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
    ServerResponse<User> response=iUserService.login(username,password);
    if(response.isSuccess())
    {
        session.setAttribute(Const.CURRENT_USER,response.getData());
    }
    return response;
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user)
    {
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkvalid(String str,String type)
    {
        return iUserService.checkvalid(str,type);
    }


    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken)
    {
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value = "update_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_infomation(HttpSession session,User user)
    {
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
        {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response=iUserService.updateInfomation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_infomation(HttpSession session)
    {
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登陆status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
