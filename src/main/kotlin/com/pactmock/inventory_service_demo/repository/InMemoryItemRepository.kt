package com.pactmock.inventory_service_demo.repository

import com.pactmock.inventory_service_demo.model.Item
import org.springframework.stereotype.Repository

@Repository
class InMemoryItemRepository : ItemRepository {
    private val items = mutableMapOf(
        1L to Item(1L, "Laptop", "High-performance laptop", 50, 999.99),
        2L to Item(2L, "Smartphone", "Latest model smartphone", 30, 699.99),
        3L to Item(3L, "Tablet", "10-inch tablet", 20, 499.99),
        4L to Item(4L, "Headphones", "Wireless noise-canceling headphones", 100, 199.99),
        5L to Item(5L, "Smartwatch", "Fitness tracking smartwatch", 45, 249.99)
    )

    override fun findAll(): List<Item> = items.values.toList()

    override fun findById(id: Long): Item? = items[id]

    override fun updateStock(id: Long, quantity: Int): Boolean {
        val item = items[id] ?: return false
        item.stockCount = quantity
        return true
    }

    override fun save(item: Item): Item {
        items[item.id] = item
        return item
    }

    override fun clear() {
        items.clear()
    }
} 