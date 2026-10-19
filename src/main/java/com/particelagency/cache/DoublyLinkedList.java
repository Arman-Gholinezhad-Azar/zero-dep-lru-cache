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
  
  //getter
  int getSize(){
    return this.size;
  }

  //addressing new pointer addresses when adding a node between head and tail
  void addNode(Node<K,V> node){
    Node<K,V> oldNode = this.head.getNext();

    this.head.setNext(node);
    node.setPrev(this.head);
    node.setNext(oldNode);
    oldNode.setPrev(node);

    this.size++;
  }

  //addressing new pointer addresses when removing a node
  void removeNode(Node<K,V> node){
    Node<K,V> prevNode = node.getPrev();
    Node<K,V> nextNode = node.getNext();

    prevNode.setNext(nextNode);
    nextNode.setPrev(prevNode);

    node.setNext(null);
    node.setPrev(null);

    this.size--;
  }
}
