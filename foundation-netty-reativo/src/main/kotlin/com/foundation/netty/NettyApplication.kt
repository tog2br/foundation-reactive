package com.foundation.netty

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NettyApplication

fun main(args: Array<String>) {
    runApplication<NettyApplication>(*args)
}
