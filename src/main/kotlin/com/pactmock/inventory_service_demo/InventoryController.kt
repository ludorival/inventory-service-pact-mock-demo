package com.pactmock.inventory_service_demo

import com.pactmock.inventory_service_demo.model.Item
import com.pactmock.inventory_service_demo.repository.ItemRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/v1")
class InventoryController(private val itemRepository: ItemRepository) {

    @GetMapping("/items")
    fun getItems(): ResponseEntity<List<Item>> {
        return ResponseEntity.ok(itemRepository.findAll())
    }

    @PostMapping("/book")
    fun bookItem(@RequestBody request: BookingRequest): ResponseEntity<BookingResponse> {
        val item = itemRepository.findById(request.itemId)
        return when {
            item == null -> ResponseEntity.status(404).body(BookingResponse(false, "Item not found"))
            item.stockCount < request.quantity -> ResponseEntity.ok(BookingResponse(false, "Out of stock"))
            else -> {
                itemRepository.updateStock(request.itemId, item.stockCount - request.quantity)
                ResponseEntity.ok(BookingResponse(true, "Successfully booked ${request.quantity} items"))
            }
        }
    }

    @PostMapping("/release")
    fun releaseItem(@RequestBody request: ReleaseRequest): ResponseEntity<ReleaseResponse> {
        val item = itemRepository.findById(request.itemId)
        return when {
            item == null -> ResponseEntity.notFound().build()
            else -> {
                itemRepository.updateStock(request.itemId, item.stockCount + request.quantity)
                ResponseEntity.ok(ReleaseResponse(true, "Successfully released ${request.quantity} items"))
            }
        }
    }
}


data class BookingRequest(val itemId: Long, val quantity: Int)
data class BookingResponse(val success: Boolean, val message: String)
data class ReleaseRequest(val itemId: Long, val quantity: Int)
data class ReleaseResponse(val success: Boolean, val message: String)