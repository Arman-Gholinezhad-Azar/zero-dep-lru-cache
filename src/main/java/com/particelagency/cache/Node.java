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

/*
 * An internal building block representing a single entry in the cache.
 * Holds key-value data and pointers for bidirectional list traversal.
 * Package-private to prevent leaking raw node references outside the cache engine.
*/

final class Node<K,V>{
  
  private final K key;
  private V value;
  private Node<K,V> next;
  private Node<K,V> prev;

  Node(K key, V value){
    this.key = key;
    this.value = value;
  }

  //Getters
  K getKey(){
    return this.key;
  }

  V getValue(){
    return this.value;
  }

  Node<K,V> getNext(){
    return this.next;
  }

  Node<K,V> getPrev(){
    return this.prev;
  }

  //Setter
  void setValue(V newValue){
    this.value = newValue;
  }

  void setNext(Node<K,V> next){
    this.next = next;
  }

  void setPrev(Node<K,V> prev){
    this.prev = prev;
  }

}
