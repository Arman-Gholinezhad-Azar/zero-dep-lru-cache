# Particel LRU Cache

A lightweight, zero-dependency Least Recently Used (LRU) cache implementation written in pure Java. It provides strict O(1) constant-time guarantees for all core operations (reads, writes, updates, and evictions) without relying on standard JDK collections like `LinkedHashMap`.

## Architecture

The cache achieves O(1) performance by composing two foundational data structures:

* **`HashMap<K, Node<K, V>>`**: Maps keys directly to their node's memory address, bypassing the need to search the list sequentially.
* **`DoublyLinkedList<K, V>`**: Maintains the chronological access order (recency) of the nodes.

When a cache hit occurs, the `HashMap` locates the node in O(1) time, and the `DoublyLinkedList` splices it to the head of the list in O(1) time. When the cache reaches capacity, the node adjacent to the tail is evicted, and its key is subsequently removed from the map.

## Implementation Notes

* **Sentinel Nodes:** The internal `DoublyLinkedList` is initialized with dummy `head` and `tail` sentinels. This guarantees every inserted node has a non-null `prev` and `next` reference, eliminating null checks and conditional branching during pointer manipulation.
* **State Ownership:** The `getSize()` method and eviction thresholds delegate to `map.size()` rather than maintaining a redundant counter in the linked list. This treats the hash table as the single source of truth for entry existence, preventing state drift.
* **Fail-Fast Boundaries:** Standard operations explicitly reject `null` keys via `Objects.requireNonNull` to prevent state corruption. Cache misses return `null` via standard conditional checks, avoiding the heavy CPU overhead of exception handling for routine control flow.
* **Package-Private Encapsulation:** Internal structures (`Node` and `DoublyLinkedList`) are package-private, preventing external consumers from tampering with raw memory references.

## Usage

### Standalone Cache

```java
import com.particelagency.cache.CacheStorage;

public class CacheExample {
    public static void main(String[] args) {
        // Initialize cache with a maximum capacity of 3
        CacheStorage<String, String> cache = new CacheStorage<>(3);

        cache.put("k1", "value1");
        cache.put("k2", "value2");
        cache.put("k3", "value3");

        // Accessing "k1" promotes it to the head (Most Recently Used)
        String val = cache.get("k1"); 

        // Inserting a 4th element triggers capacity limits.
        // "k2" is evicted because it is now the Least Recently Used.
        cache.put("k4", "value4");

        System.out.println(cache.get("k2")); // Output: null
        System.out.println(cache.get("k1")); // Output: value1
    }
}

```

### Read-Through Cache Pattern

This implementation is typically used as a fast L1 buffer in front of a slower persistence layer (e.g., a relational database).

```java
import com.particelagency.cache.CacheStorage;

public class UserService {
    private final CacheStorage<Long, User> userCache;
    private final UserRepository repository;

    public UserService(UserRepository repository, int capacity) {
        this.repository = repository;
        this.userCache = new CacheStorage<>(capacity);
    }

    public User getUser(Long userId) {
        // 1. Check fast in-memory cache
        User cachedUser = this.userCache.get(userId);
        if (cachedUser != null) {
            return cachedUser;
        }

        // 2. Fall back to database on cache miss
        User dbUser = this.repository.findById(userId);

        // 3. Populate cache for subsequent reads
        if (dbUser != null) {
            this.userCache.put(userId, dbUser);
        }

        return dbUser;
    }
}

```

## Complexity Guarantees

| Operation | Time Complexity | Auxiliary Space |
| --- | --- | --- |
| `get(K key)` | O(1) | O(1) |
| `put(K key, V value)` | O(1) | O(1) |
| `remove(K key)` | O(1) | O(1) |
| `getSize()` | O(1) | O(1) |
| `clear()` | O(1) | O(1) |
