package com.speechify

/**
 * A Least Recently Used (LRU) cache is a type of cache that, when a need arises to constrain its size, evicts the
 * items whose last use was the furthest in the past.
 *
 * For this particular implementation, the size constraint is set at [CacheLimits.maxItemsCount].
 * An item is considered accessed whenever `get`, or `set` methods are called with its key.
 *
 * This LRU cache should ensure the constraint by checking the cache size at the time of each new insertion.
 * In the case where the cache has reached its limit, the item "least recently accessed" will be removed.
 * The implementation should be performant when storing big number of items.
 * It is OK for the removal of items to happen in the same thread that called the function (OK to block the return from
 * the function for a cleanup, when necessary).
 *
 * Tests are provided in [com.speechify.LRUCacheProviderTest] (`src/test/kotlin/LruCache.test.kt`) to validate your
 * implementation.
 *
 * You may:
 *  - Read online API references for Kotlin standard library or JVM collections.
 * You must not:
 *  - Read guides about how to code an LRU cache.
 */
interface LRUCache<T> {
    fun get(key: String): T?
    fun set(key: String, value: T)
}

data class CacheLimits(
    /**
     * @property maxItemsCount
     * Maximum count of items (*inclusive*) that this cache is allowed to contain.
     */
    val maxItemsCount: Int
)


fun <T> createLRUCache(options: CacheLimits): LRUCache<T> {
    return LRUCacheImpl(options)
}

class LRUCacheImpl<T>(
    private val options: CacheLimits
) : LRUCache<T> {

    private val cache = object : LinkedHashMap<String, T>(options.maxItemsCount, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, T>?): Boolean {
            return size > options.maxItemsCount
        }
    }

    override fun get(key: String): T? {
        return cache[key]
    }

    override fun set(key: String, value: T) {
        cache[key] = value
    }
}
