package com.pactmock.inventory_service_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InventoryServiceDemoApplication

fun main(args: Array<String>) {
	runApplication<InventoryServiceDemoApplication>(*args)
}
