package com.pactmock.inventory_service_demo.model

data class Item(
    val id: Long,
    val name: String,
    val description: String,
    var stockCount: Int,
    val price: Double
) 