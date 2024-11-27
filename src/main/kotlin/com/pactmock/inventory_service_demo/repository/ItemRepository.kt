package com.pactmock.inventory_service_demo.repository

import com.pactmock.inventory_service_demo.model.Item
interface ItemRepository {
    fun findAll(): List<Item>
    fun findById(id: Long): Item?
    fun updateStock(id: Long, quantity: Int): Boolean
} 