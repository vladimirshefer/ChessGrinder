package com.chessgrinder.chessgrinder.telementry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TracingAspect {

    private final Tracer tracer;

    public TracingAspect(OpenTelemetry openTelemetry) {
        tracer = openTelemetry.getTracer("TracingAspect");
    }

    @Around("execution(* com.chessgrinder.chessgrinder.repositories..*(..))")
    public Object traceRepositories(ProceedingJoinPoint joinPoint) throws Throwable {
        return proceed(joinPoint);
    }

    @Around("execution(* com.chessgrinder.chessgrinder.controller..*(..))")
    public Object traceControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        return proceed(joinPoint);
    }

    private Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        String spanName = joinPoint.getSignature().toShortString();
        Span span = tracer.spanBuilder(spanName).setSpanKind(SpanKind.SERVER).startSpan();
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            span.recordException(throwable);
            throw throwable;
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;
            span.end();
            if (elapsedTime > 1000) {
                log.info("Trace {} took {} ms", spanName, elapsedTime);
            }
            if (elapsedTime > 3000) {
                log.warn("Trace {} took {} ms", spanName, elapsedTime);
            }
        }
    }
}
