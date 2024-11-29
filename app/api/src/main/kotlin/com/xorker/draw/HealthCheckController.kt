package com.xorker.draw

import com.xorker.draw.support.logging.logger
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "개발용 API")
@RestController
class HealthCheckController {
    val log = logger()

    @Operation(hidden = true)
    @GetMapping("/ping")
    fun ping(): String {
        return "pong"
    }
}
