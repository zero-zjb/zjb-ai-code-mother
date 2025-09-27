package com.zjb.zjbaicodemother.aop;

import cn.hutool.core.util.StrUtil;
import com.zjb.zjbaicodemother.annotation.AuthCheck;
import com.zjb.zjbaicodemother.exception.BusinessException;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.model.entity.User;
import com.zjb.zjbaicodemother.model.enums.UserRoleEnum;
import com.zjb.zjbaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return 执行结果
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //1.获取权限校验规则
        String mustRole = authCheck.mustRole();
        //2.获取当前登录用户
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User loginUser = userService.getLoginUser(request);
        //3.校验权限
        if(StrUtil.isBlank(mustRole)){
            return joinPoint.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if(userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户未登录");
        }
        UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if(roleEnum == UserRoleEnum.ADMIN && userRoleEnum != UserRoleEnum.ADMIN){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户无权限");
        }
        return joinPoint.proceed();
    }
}
