package com.mockup_api_server.routers.user

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class DynamicObjectStore {
    private val store = ConcurrentHashMap<String, Any?>()

    fun save(key: String, value: Any?) {
        store[key] = value
    }

    fun get(key: String): Any? = store[key]

    fun getAll(): Map<String, Any?> = store.toMap()
}