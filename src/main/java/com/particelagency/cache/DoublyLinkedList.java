package com.particelagency.cache;

final class DoublyLinkedList<K,V>{
  private final Node<K,V> head;
  private final Node<K,V> tail;
  private int size;

  DoublyLinkedList(){
    this.head = new Node<K,V>(null, null);
    this.tail = new Node<K,V>(null, null);

    this.head.setNext(this.tail);
    this.tail.setPrev(this.head);

    this.size = 0;
  }
}
