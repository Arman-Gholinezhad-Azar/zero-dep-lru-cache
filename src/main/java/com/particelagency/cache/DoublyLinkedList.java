/*
   Copyright 2026 Arman Gholinezhad Azar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/
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
  
  //recomposition of exisitng nodes methods
  void moveNodeToHead(Node<K,V> node){
    removeNode(node);
    addNode(node);
  }

  Node<K,V> removeLastNode(){
    if(this.size == 0){
      return null;
    }

    Node<K,V> lastNode = this.tail.getPrev();
    removeNode(lastNode);
    
    return lastNode;
  }
  void clear() {
    this.head.setNext(this.tail);
    this.tail.setPrev(this.head);
    this.size = 0;
  }
}
