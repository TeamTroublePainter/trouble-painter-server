package com.xorker.draw.support.auth

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class SecurityExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException::class)
    fun handleException(ex: AuthenticationException): String {
        return "{}"
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleException(ex: AccessDeniedException): String {
        return "{}"
    }
}
