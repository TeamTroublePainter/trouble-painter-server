package com.xorker.draw.support.auth

import com.xorker.draw.exception.UnAuthenticationException
import com.xorker.draw.exception.UnAuthorizedException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class SecurityExceptionControllerAdvice {

    @ExceptionHandler(AuthenticationException::class)
    fun handleException(ex: AuthenticationException) {
        throw UnAuthenticationException(ex)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleException(ex: AccessDeniedException) {
        throw UnAuthorizedException(ex)
    }
}
