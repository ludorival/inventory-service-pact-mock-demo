package com.pactmock.inventory_service_demo

import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/v1")
class InventoryController {

    data class Item(
        val id: Long,
        val name: String,
        val description: String,
        var stockCount: Int,
        val price: Double
    )

    private val items = mutableMapOf(
        1L to Item(1L, "Laptop", "High-performance laptop", 50, 999.99),
        2L to Item(2L, "Smartphone", "Latest model smartphone", 30, 699.99),
        3L to Item(3L, "Tablet", "10-inch tablet", 20, 499.99),
        4L to Item(4L, "Headphones", "Wireless noise-canceling headphones", 100, 199.99),
        5L to Item(5L, "Smartwatch", "Fitness tracking smartwatch", 45, 249.99)
    )

    @GetMapping("/items")
    fun getItems(): ResponseEntity<List<Item>> {
        return ResponseEntity.ok(items.values.toList())
    }


    @PostMapping("/book")
    fun bookItem(@RequestBody request: BookingRequest): ResponseEntity<BookingResponse> {
        val item = items[request.itemId]
        return when {
            item == null -> ResponseEntity.notFound().build()
            item.stockCount < request.quantity -> ResponseEntity.status(409).body(BookingResponse(false, "Insufficient stock"))
            else -> {
                item.stockCount -= request.quantity
                ResponseEntity.ok(BookingResponse(true, "Successfully booked ${request.quantity} items"))
            }
        }
    }

    @PostMapping("/release")
    fun releaseItem(@RequestBody request: ReleaseRequest): ResponseEntity<ReleaseResponse> {
        val item = items[request.itemId]
        return when {
            item == null -> ResponseEntity.notFound().build()
            else -> {
                item.stockCount += request.quantity
                ResponseEntity.ok(ReleaseResponse(true, "Successfully released ${request.quantity} items"))
            }
        }
    }
}

data class BookingRequest(val itemId: Long, val quantity: Int)
data class BookingResponse(val success: Boolean, val message: String)
data class ReleaseRequest(val itemId: Long, val quantity: Int)
data class ReleaseResponse(val success: Boolean, val message: String)