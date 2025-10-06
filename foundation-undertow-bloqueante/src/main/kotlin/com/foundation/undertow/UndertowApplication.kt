package com.foundation.undertow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class UndertowApplication

fun main(args: Array<String>) {
    runApplication<UndertowApplication>(*args)
}
