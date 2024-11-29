package com.xorker.draw

import com.xorker.draw.exception.MaxRoomException
import com.xorker.draw.support.logging.logger
import com.xorker.draw.version.ApiMinVersion
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "개발용 API")
@Profile("local", "sandbox")
@RestController
class TestController {

    val logger = logger()

    @Operation(hidden = true)
    @GetMapping("/test/ping")
    fun test(): String {
        MDC.put("test", "test")
        logger.info("Call Test")
        return "pong"
    }

    @Operation(summary = "강업 테스트")
    @ApiMinVersion(androidVersion = "99.99.99", iosVersion = "99.99.99")
    @GetMapping("/test/force-update")
    fun forceUpdate() {}

    @Operation(summary = "일반 에러 처리")
    @GetMapping("/test/error/client")
    fun testError() {
        throw MaxRoomException
    }
}
