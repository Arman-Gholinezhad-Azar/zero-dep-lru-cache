package com.particelagency.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CacheStorage<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final DoublyLinkedList<K, V> list;

    public CacheStorage(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
        this.list = new DoublyLinkedList<>();
    }

    public V get(K key) {
        Objects.requireNonNull(key, "cache key cannot be null");

        Node<K, V> node = this.map.get(key);
        
        //cache miss
        if (node == null) {
            return null;
        }

        //cache hit
        this.list.moveNodeToHead(node);
        return node.getValue();
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "cache key cannot be null");

        Node<K, V> existingNode = this.map.get(key);

        //key already exists update payload and promote to head
        if (existingNode != null) {
            existingNode.setValue(value);
            this.list.moveNodeToHead(existingNode);
            return;
        }

        //new entry evict oldest node if at maximum capacity
        if (this.map.size() >= this.capacity) {
            evictLeastRecentlyUsed();
        }

        Node<K, V> newNode = new Node<>(key, value);
        this.list.addNode(newNode);
        this.map.put(key, newNode);
    }

    public void remove(K key) {
        Objects.requireNonNull(key, "cache key cannot be null");

        Node<K, V> node = this.map.remove(key);
        if (node != null) {
            this.list.removeNode(node);
        }
    }

    public int getSize() {
        return this.map.size();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void clear() {
        this.map.clear();
        this.list.clear();
    }

    private void evictLeastRecentlyUsed() {
        Node<K, V> lruNode = this.list.removeLastNode();
        if (lruNode != null) {
            this.map.remove(lruNode.getKey());
        }
    }
}
