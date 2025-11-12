package com.pactmock.inventory_service_demo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.junit.jupiter.api.Assertions.*
import com.pactmock.inventory_service_demo.repository.InMemoryItemRepository

class InventoryControllerTests {
    private lateinit var controller: InventoryController

    @BeforeEach
    fun setup() {
        controller = InventoryController(InMemoryItemRepository())
    }

    @Test
    fun `getItems should return all items`() {
        val response = controller.getItems()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(5, response.body?.size)

        val firstItem = response.body?.first()
        assertNotNull(firstItem)
        assertEquals(1L, firstItem?.id)
        assertEquals("Laptop", firstItem?.name)
        assertEquals(50, firstItem?.stockCount)
    }

    @Test
    fun `bookItem should successfully book when stock is available`() {
        val request = BookingRequest(1L, 5)
        val response = controller.bookItem(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body?.success == true)
        assertEquals("Booked successfully", response.body?.message)

        // Verify stock was reduced
        val updatedItem = controller.getItems().body?.find { it.id == 1L }
        assertEquals(45, updatedItem?.stockCount)
    }

    @Test
    fun `bookItem should return 404 when item doesn't exist`() {
        val request = BookingRequest(999L, 1)
        val response = controller.bookItem(request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `bookItem should return 200 with Out of stock message when insufficient stock`() {
        val request = BookingRequest(1L, 1000)
        val response = controller.bookItem(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertFalse(response.body?.success == true)
        assertEquals("Out of stock", response.body?.message)
    }

    @Test
    fun `releaseItem should successfully release items`() {
        // First book some items
        controller.bookItem(BookingRequest(1L, 10))

        // Then release them
        val request = ReleaseRequest(1L, 5)
        val response = controller.releaseItem(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body?.success == true)
        assertEquals("Released successfully", response.body?.message)

        // Verify stock was increased
        val updatedItem = controller.getItems().body?.find { it.id == 1L }
        assertEquals(45, updatedItem?.stockCount)
    }

    @Test
    fun `releaseItem should return 404 when item doesn't exist`() {
        val request = ReleaseRequest(999L, 1)
        val response = controller.releaseItem(request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}
