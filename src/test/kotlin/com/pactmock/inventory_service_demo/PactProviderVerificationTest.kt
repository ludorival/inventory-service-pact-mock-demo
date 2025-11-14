package com.pactmock.inventory_service_demo

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth
import com.pactmock.inventory_service_demo.model.Item
import com.pactmock.inventory_service_demo.repository.InMemoryItemRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.TestPropertySource
import java.net.URI

/**
 * Pact Broker verification test.
 * 
 * This test loads contracts from Pact Broker (requires PACT_BROKER_URL to be configured).
 * Verification results are automatically published to Pact Broker in CI environments.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("inventory-service")
@PactBroker(
    url = "\${pactbroker.url:}",
    authentication = PactBrokerAuth(token = "\${pactbroker.auth.token:}")
)
@TestPropertySource(properties = ["server.servlet.context-path=/inventory-service"])
class PactProviderVerificationTest {

    @Autowired
    private lateinit var itemRepository: InMemoryItemRepository

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = HttpTestTarget.fromUrl(URI.create("http://localhost:$port").toURL())
        
        // Configure publishing results and provider metadata
        System.setProperty("pact.verifier.publishResults", System.getenv("CI") ?: "false")
        System.setProperty("pact.provider.version", System.getenv("GITHUB_SHA") ?: "unknown")
        System.setProperty("pact.provider.branch", System.getenv("GITHUB_BRANCH") ?: "unknown")
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    // Provider state handlers

    @State("item is available for booking")
    fun `item is available for booking`(params: Map<String, Any>) {
        val itemId = (params["itemId"] as Number).toLong()
        val quantity = (params["quantity"] as Number).toInt()
        
        // Set up item with sufficient stock for booking
        // Create or update item to ensure it exists with enough stock
        val existingItem = itemRepository.findById(itemId)
        if (existingItem != null) {
            itemRepository.updateStock(itemId, quantity + 10) // Ensure enough stock
        } else {
            itemRepository.save(Item(itemId, "Item $itemId", "Description", quantity + 10, 0.0))
        }
    }

    @State("item is booked and can be released")
    fun `item is booked and can be released`(params: Map<String, Any>) {
        val itemId = (params["itemId"] as Number).toLong()
        
        // Set up item that has been booked (reduced stock)
        // The item should exist and have some stock
        val existingItem = itemRepository.findById(itemId)
        if (existingItem != null) {
            itemRepository.updateStock(itemId, 5) // Some stock available
        } else {
            itemRepository.save(Item(itemId, "Item $itemId", "Description", 5, 0.0))
        }
    }

    @State("item is out of stock")
    fun `item is out of stock`(params: Map<String, Any>) {
        val itemId = (params["itemId"] as Number).toLong()
        val quantity = (params["quantity"] as Number).toInt()
        
        // Set up item with insufficient stock
        val existingItem = itemRepository.findById(itemId)
        if (existingItem != null) {
            itemRepository.updateStock(itemId, quantity - 1) // Less than requested
        } else {
            itemRepository.save(Item(itemId, "Item $itemId", "Description", quantity - 1, 0.0))
        }
    }

    @State("items are available")
    fun `items are available`() {
        // Clear repository and set up items to match contract expectations
        // Contract expects exactly 2 items with id=1 and id=2
        itemRepository.clear()
        itemRepository.save(Item(1L, "Item 1", "Description", 10, 0.0))
        itemRepository.save(Item(2L, "Item 2", "Description", 20, 0.0))
    }
}

