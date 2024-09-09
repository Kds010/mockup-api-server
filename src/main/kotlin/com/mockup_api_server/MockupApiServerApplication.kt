package com.mockup_api_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class MockupApiServerApplication

fun main(args: Array<String>) {
	runApplication<MockupApiServerApplication>(*args)
}
