package com.dqmall.common;

/**
 * @program: dqmall
 * @description: 常量类
 * @author: 唐庆阳
 * @create: 2019-11-09 21:00
 **/
public class Const {
    public static final String CURRENT_USER="currentUser";

    public static final String EMAIL="email";

    public static final String USERNAME="username";

    public interface Role{
        int ROLE_CONSUMER=0;
        int ROLE_ADMIN=1;
    }
}
