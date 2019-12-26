package com.dqmall.service.impl;

import com.dqmall.common.Const;
import com.dqmall.common.ServerResponse;
import com.dqmall.common.TokenCache;
import com.dqmall.dao.UserMapper;
import com.dqmall.pojo.User;
import com.dqmall.service.IUserService;
import com.dqmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @program: dqmall
 * @description: 用户服务实现类
 * @author: 唐庆阳
 * @create: 2019-09-22 20:51
 **/
@Service("iUserService")
public class IUserServiceimpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    public static final  String TOKEN_PREFIX="token_";

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultcont=userMapper.checkUsername(username);
        if(resultcont==0)
        {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码MD5
        String md5password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectlogin(username,md5password);
        if(user==null)
        {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    //注册
    public ServerResponse<String> register(User user){
        ServerResponse validResponse=this.checkvalid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess())
        {
            return validResponse;
        }
        validResponse=this.checkvalid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess())
        {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CONSUMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int  resultcont=userMapper.insert(user);
        if(resultcont==0)
        {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return  ServerResponse.createBySuccessMessage("注册成功");
    }
//用户注册校验
    public ServerResponse<String> checkvalid(String str,String type)
    {
        if(StringUtils.isNotBlank(type))
        {
            if(Const.USERNAME.equals(type))
            {
                int resultcont=userMapper.checkUsername(str);
                if(resultcont>0)
                {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type))
            {
                int resultcont=userMapper.checkEmail(str);
                if(resultcont>0)
                {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }

        }
        else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username)
    {
        ServerResponse validResponse=this.checkvalid(username,Const.USERNAME);
        if(validResponse.isSuccess())
        {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question))
        {
            return ServerResponse.createBySuccess(username);
        }
        return ServerResponse.createByErrorMessage("找回密码的值是空的");

    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer)
    {
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0)
        {
            //说明问题及问题答案是这个用户的，并且是正确的
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken))
        {
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse=this.checkvalid(username,Const.USERNAME);
        if(validResponse.isSuccess())
        {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token=TokenCache.getKey(TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token))
        {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个Count(1),如果不指定id，那么结果就是true啦count>0
        int ResultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(ResultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0)
        {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInfomation(User user){
        //username是不能被更新的
        //email也要进行一个校验，校验新的邮箱是否存在，并且存在的email相同的话，不能是我们这个用户的
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email在尝试更新");
        }
        User updateuser=new User();
        updateuser.setId(user.getId());
        updateuser.setEmail(user.getEmail());
        updateuser.setPhone(user.getPhone());
        updateuser.setQuestion(user.getQuestion());
        updateuser.setAnswer(user.getAnswer());

        int updateCount=userMapper.updateByPrimaryKeySelective(updateuser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateuser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
