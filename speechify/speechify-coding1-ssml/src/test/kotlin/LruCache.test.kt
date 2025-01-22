package com.speechify

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests the [createLRUCache] function.
 * Tests can be run in the IDE, or command-line with `./gradlew test`.
 */
class LRUCacheProviderTest {
  @Test
  fun `get should return value for existing key`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 10))
    lruCache.set("foo", "bar")
    assertEquals("bar", lruCache.get("foo"))
  }

  @Test
  fun `get should return null for non-existent key`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 10))
    lruCache.set("foo", "bar")
    assertNull(lruCache.get("bar"))
    assertNull(lruCache.get(""))
  }

  @Test
  fun `get should return value for many existing keys`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 10))
    lruCache.set("foo", "foo")
    lruCache.set("baz", "baz")
    assertEquals("foo", lruCache.get("foo"))
    assertEquals("baz", lruCache.get("baz"))
  }

  @Test
  fun `get should return null for key not fitting maxItemsCount`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 1))
    lruCache.set("foo", "bar")
    lruCache.set("baz", "bar")
    assertNull(lruCache.get("foo"))
    assertEquals("bar", lruCache.get("baz"))
  }

  @Test
  fun `get should return value for recreated key after it was previously removed`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 1))
    lruCache.set("foo", "bar")
    lruCache.set("baz", "bar")
    lruCache.set("foo", "bar")
    assertEquals("bar", lruCache.get("foo"))
    assertNull(lruCache.get("baz"))
  }

  @Test
  fun `set replaces existing value`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 10))
    lruCache.set("key", "firstValue")
    lruCache.set("key", "secondValue")
    assertEquals(
      expected = "secondValue",
      actual = lruCache.get("key"),
    )
  }

  @Test
  fun `number of keys present is cache limit with set to replace existing value for one of the keys`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 3))
    lruCache.set("bax", "par")
    lruCache.set("foo", "bar1")
    lruCache.set("foo", "bar2")
    lruCache.set("foo", "bar3")
    lruCache.set("baz", "bar")

    assertEquals("bar3", lruCache.get("foo"))
    assertEquals("par", lruCache.get("bax"))
    assertEquals("bar", lruCache.get("baz"))
  }

  @Test
  fun `set should remove oldest key on reaching maxItemsCount if no get or has been used`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 1))
    lruCache.set("foo", "bar")
    lruCache.set("baz", "bar")
    assertEquals(null, lruCache.get("foo"))
    assertEquals("bar", lruCache.get("baz"))
  }

  @Test
  fun `set should remove least recently used key on reaching maxItemsCount`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 2))
    lruCache.set("foo", "bar")
    lruCache.set("bar", "bar")
    lruCache.get("foo")
    lruCache.set("baz", "bar")

    assertEquals("bar", lruCache.get("foo"))
    assertEquals("bar", lruCache.get("foo"))

    assertNull(lruCache.get("bar"))

    assertEquals("bar", lruCache.get("baz"))
  }

  @Test
  fun `Item is considered accessed when 'get' is called`() {
    val lruCache = createLRUCache<String>(CacheLimits(maxItemsCount = 2))
    lruCache.set("1key", "1value")
    lruCache.set("2key", "2value")

    lruCache.get("1key")
    lruCache.set("3key", "3value")

    assertEquals("1value", lruCache.get("1key"))
  }
}
