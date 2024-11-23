package com.pactmock.inventory_service_demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/inventory")
class InventoryController {

    val stockData = mutableMapOf<Long, Int>(1L to 50, 2L to 0, 3L to 20) // Exemple de données en mémoire

    @GetMapping("/product/{productId}")
    fun getProductStock(@PathVariable productId: Long): ResponseEntity<StockResponse> {
        val quantity = stockData[productId] ?: 0
        return ResponseEntity.ok(StockResponse(productId, quantity))
    }
}

data class StockResponse(val productId: Long, val quantity: Int)