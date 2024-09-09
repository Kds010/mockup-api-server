package com.mockup_api_server.routers.user

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DynamicObjectController(
    private val dynamicObjectStore: DynamicObjectStore
) {

    private val logger = LoggerFactory.getLogger(DynamicObjectController::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @GetMapping("/all/objects")
    fun getAllObject(): ResponseEntity<Map<String, Any?>>{
        val allObjects = dynamicObjectStore.getAll()
        return if (allObjects.isNotEmpty()) {
            ResponseEntity.ok(allObjects)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PostMapping("/{endpoint}")
    fun createObject(
        @PathVariable endpoint: String,
        @RequestBody jsonBody: String
    ): ResponseEntity<Any> {
        logger.info("Received POST endpoint:: $endpoint ")
        return try {
            val jsonNode: JsonNode = objectMapper.readTree(jsonBody)
            val dynamicObject = createDynamicObject(jsonNode)
            dynamicObjectStore.save(endpoint, dynamicObject)
            ResponseEntity.ok(dynamicObject)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Error creating object: ${e.message}")
        }
    }

    @GetMapping("/{endpoint}")
    fun getObject(
        @PathVariable endpoint: String
    ): ResponseEntity<Any>{
        return dynamicObjectStore.get(endpoint)?.let {
            ResponseEntity.ok(it)
        }?: ResponseEntity.noContent().build()
    }

    private fun createDynamicObject(jsonNode: JsonNode): Any? {
        return when {
            jsonNode.isObject -> {
                val map = mutableMapOf<String, Any?>()
                jsonNode.fields().forEach { (key, value) ->
                    map[key] = createDynamicObject(value)
                }
                map
            }
            jsonNode.isArray -> {
                jsonNode.map { createDynamicObject(it) }
            }
            jsonNode.isTextual -> jsonNode.asText()
            jsonNode.isNumber -> {
                if (jsonNode.isInt) jsonNode.asInt()
                else if (jsonNode.isLong) jsonNode.asLong()
                else jsonNode.asDouble()
            }
            jsonNode.isBoolean -> jsonNode.asBoolean()
            jsonNode.isNull -> null
            else -> throw IllegalArgumentException("Unsupported JSON type")
        }
    }
}