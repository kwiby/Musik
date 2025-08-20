import 'dart:developer';

class CircularDoublyLinkedList<T> {
  int _size = 0;
  Node<T>? _head; // The head is on the left end, so the last node would be on the right end.

  // Method to add a node at the start (left side -> new head).
  void addStart(T value) {
    Node<T> newNode = Node(value);

    if (_head == null) { // If the linked list IS empty.
      newNode.next = newNode; // Being circular, set the next of the head to the head itself.
      newNode.prev = newNode; // Being circular, set the previous of the head to the head itself.

      _head = newNode; // Set the first head to the new node.
    } else { // If the linked list is NOT empty.
      Node<T> last = _head!.prev!; // Set the last node to the previous of the head.

      newNode.next = _head; // Set the next of the new node to be the head.
      newNode.prev = last; // Set the previous of the new node to be the last node.
      _head!.prev = newNode; // Set the previous of the head to the new node.
      last.next = newNode; // Set the next of the last node to the new node.

      _head = newNode; // Set the new head to the new node.
    }
    _size++;
  }

  // Method to add a node to the end (right side).
  void addEnd(T value) {
    Node<T> newNode = Node(value);

    if (_head == null) { // If the linked list IS empty.
      newNode.next = newNode; // Being circular, set the next of the head to the head itself.
      newNode.prev = newNode; // Being circular, set the previous of the head to the head itself.

      _head = newNode;
    } else { // If the linked list is NOT empty.
      Node<T> last = _head!.prev!; // Set the last node to the previous of the head.

      newNode.next = _head; // Set the next of the new node to be the head.
      newNode.prev = last; // Set the previous of the new node to be the last node.
      last.next = newNode; // Set the next of the last node to the new node.
      _head!.prev = newNode; // Set the previous of the head to the new node.
    }

    _size++;
  }

  // Method to get the value of the first node (head).
  T? getStart() {
    return _head?.value; // Just return the value of the head.
  }

  // Method to get the value of the last node.
  T? getEnd() {
    return _head?.prev?.value; // Return the value of the previous node of the head (which would be the last node).
  }

  void remove(T value) {
    if (_head == null) return;

    Node<T> currentNode = _head!;
    do {
      if (currentNode.value == value) { // If the current node's value IS equal to the target value to remove.
        if (currentNode == currentNode.next) { // If there is only 1 node (the head) in the linked list.
          _head = null;
        } else { // If there is more than 1 node in the linked list.
          currentNode.next!.prev = currentNode.prev; // Set the next node's previous to be the current node's previous.
          currentNode.prev!.next = currentNode.next; // Set the previous node's next to be the current node's next.

          if (currentNode == _head) { // If the head is the current node, then set the head to the current head's next (making the old head removed from the linked list).
            _head = currentNode.next;
          }
        }
        _size--;

        return;
      } else { // If the current node's value is NOT equal to the target value to remove.
        currentNode = currentNode.next!; // Set the current node to the next node.
      }
    } while (currentNode != _head); // Continue while the current node is not the head.
  }

  int get size {
    return _size;
  }

  bool get isEmpty {
    return _size == 0;
  }

  void printStartingFromHead() {
    if (_head == null) return;

    Node<T>? currentNode = _head;
    do {
      log('Debug Print: ${currentNode!.value}');

      currentNode = currentNode.next;
    } while (currentNode != _head);
  }
}

class Node<T> {
  final T value;
  Node<T>? next;
  Node<T>? prev;

  Node(this.value);
}