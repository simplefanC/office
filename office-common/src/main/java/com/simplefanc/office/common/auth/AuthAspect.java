package com.simplefanc.office.common.auth;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.simplefanc.office.common.exception.EmosException;
import com.simplefanc.office.common.exception.UnauthorizedException;
import com.simplefanc.office.common.service.RedisService;
import com.simplefanc.office.common.util.JwtUtil;
import com.simplefanc.office.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class AuthAspect {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisService redisService;

    /**
     * *表示匹配任意的方法返回值 ..(两个点)表示零个或多个
     * ..表示controller包及其子包 *表示所有类 *表示所有方法 ..表示方法的任意参数个数
     */
    @Pointcut("execution(public * com.simplefanc.office.*.controller..*.*(..))")
    public void pointcut() {
    }

    /**
     * Spring AOP是不会拦截到被嵌套调用的方法
     *
     * @param
     * @return
     * @throws Throwable
     */
    //    @Around("@annotation(com.simplefanc.office.common.auth.CheckLogin)")
//    @Around("pointcut()")
//    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
//        HttpServletRequest request = this.getHttpServletRequest();
//        if (!request.getRequestURI().contains("login")) {
//            String token = request.getHeader("token");
//            try {
//                //校验token是否合法&过期
//                jwtUtil.verifierToken(token);
//            } catch (TokenExpiredException e) {
//                //客户端令牌过期，查询Redis中是否存在令牌，如果存在令牌就重新生成一个令牌给客户端
//                if (redisService.hasKey(token)) {
//                    //删除令牌
//                    redisService.del(token);
//                    //生成新的令牌
//                    int userId = jwtUtil.getUserId(token);
//                    token = jwtUtil.createToken(userId);
//                    //把新的令牌保存到Redis中
//                    redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
//                    //把新令牌绑定到线程
//                    threadLocalToken.setToken(token);
//                } else {
//                    throw new UnauthorizedException("令牌已过期");
//                }
//            } catch (Exception e) {
//                throw new UnauthorizedException("无效的令牌");
//            }
//            //如果校验成功 那么就将用户的信息到request的attribute
//            request.setAttribute("id", jwtUtil.getUserId(token));
//        }
//        //直接执行
//        return point.proceed();
//    }
    private HttpServletRequest getHttpServletRequest() {
        //从header中获取token 技巧：用静态方法获取
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

//    @Before("@annotation(com.simplefanc.office.common.auth.CheckAuthorization)")
//    public void checkAuthorization(JoinPoint point) {
//        HttpServletRequest request = this.getHttpServletRequest();
//        Object id = request.getAttribute("id");
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        Method method = signature.getMethod();
//        CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);
//        Logical logical = annotation.logical();
//        //需要的权限
//        String[] value = annotation.value();
//        //用户拥有的权限
//        Set<String> permsSet = (Set<String>) redisService.get("emos:ums:permissionSet:" + id);
//        if (Logical.OR == logical) {
//            boolean res = false;
//            for (String p : value) {
//                if (permsSet.contains(p)) {
//                    res = true;
//                    break;
//                }
//            }
//            if (!res) {
//                throw new UnauthorizedException("用户无权访问！");
//            }
//        } else if (!permsSet.containsAll(Arrays.asList(value))) {
//            throw new UnauthorizedException("用户无权访问！");
//        }
//    }

    @Around("pointcut()")
    public Object auth(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = this.getHttpServletRequest();
        String newToken = "";
        if (!request.getRequestURI().contains("login")) {
            String token = request.getHeader("token");
            newToken = checkAuthentication(token);
            //token校验成功 鉴权
            checkAuthorization(point, token);
        }
        //方法执行结果
        Object r = point.proceed();
        if (r instanceof R) {
            if (StrUtil.isNotBlank(newToken)) {
                R r1 = (R) r;
                //往响应中放置Token
                r1.put("token", newToken);
                return r1;
            }
        }
        return r;
    }

    private String checkAuthentication(String token) {
        String newToken = "";
        try {
            //校验token是否合法&过期
            jwtUtil.verifierToken(token);
        } catch (TokenExpiredException e) {
            //客户端令牌过期，查询Redis中是否存在令牌，如果存在令牌就重新生成一个令牌给客户端
            if (redisService.hasKey("emos:ums:token:" + token)) {
                //删除令牌
                redisService.del("emos:ums:token:" + token);
                //生成新的令牌
                int userId = jwtUtil.getUserId(token);
                newToken = jwtUtil.createToken(userId);
                //把新的令牌保存到Redis中
                redisService.set("emos:ums:token:" + newToken, userId + "", 10, TimeUnit.DAYS);
            } else {
                throw new UnauthorizedException("令牌已过期！");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("无效的令牌！");
        }
        return newToken;
    }

    private void checkAuthorization(ProceedingJoinPoint point, String token) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequiresPermissions annotation = method.getAnnotation(RequiresPermissions.class);
        if (annotation != null) {
            int userId = jwtUtil.getUserId(token);
            Logical logical = annotation.logical();
            //需要的权限
            String[] value = annotation.value();
            //用户拥有的权限
            Set<String> permsSet = (Set<String>) redisService.get("emos:ums:permissionSet:" + userId);
            if (Logical.OR == logical) {
                boolean res = false;
                for (String p : value) {
                    if (permsSet.contains(p)) {
                        res = true;
                        break;
                    }
                }
                if (!res) {
                    throw new EmosException("用户无权访问！");
                }
            } else if (!permsSet.containsAll(Arrays.asList(value))) {
                throw new EmosException("用户无权访问！");
            }
        }
    }
}
