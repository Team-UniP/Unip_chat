package UniP_server_chat.Unip_party_chat.global.logTrace.aspect;

import UniP_server_chat.Unip_party_chat.global.logTrace.material.LogTrace;
import UniP_server_chat.Unip_party_chat.global.logTrace.material.TraceStatus;
import UniP_server_chat.Unip_party_chat.global.logTrace.pointcut.Pointcuts;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class LogTraceAspect {
    private final LogTrace logTrace;
    private final Pointcuts pointcuts;

    @Around("pointcuts.aopMethod()&&!pointcuts.excludeSendMessage()&&!pointcuts.excludeAddUser()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            //실제 로직 호출
            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}