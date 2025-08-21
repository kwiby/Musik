import 'dart:developer';

class CircularDoublyLinkedList {
  int _size = 0;
  Node? _head; // The head is on the left end, so the last node would be on the right end.

  // Method to add a node after a specific other node value.
  void addAfter(Map<String, dynamic>? previousSongData, List<dynamic> newSongData) {
    Node newNode = Node(newSongData);

    if (_head == null && previousSongData == null) {
      newNode.next = newNode;
      newNode.prev = newNode;

      _head = newNode;
    } else if (_head != null && previousSongData != null) { // Assumes previousSongData is contained in the linked list!
      Node currentNode = _head!;

      while (currentNode.value[0]['id'].toString() != previousSongData['id'].toString()) {
        currentNode = currentNode.next!;
      }

      newNode.next = currentNode.next; // Set the new node's next node to be the next of the current.
      newNode.prev = currentNode; // Set the new node's previous node to be the current node.

      currentNode.next!.prev = newNode; // Set the next node's previous to the new node.
      currentNode.next = newNode; // Set the current node's next to the new node.
    } else {
      log("Error in adding a song after the previous {circular_doubly_linked_list.dart LINE 23}!");
    }

    _size++;
  }

  // Method to add a node at the start (left side -> new head).
  void addStart(List<dynamic> value) {
    Node newNode = Node(value);

    if (_head == null) { // If the linked list IS empty.
      newNode.next = newNode; // Being circular, set the next of the head to the head itself.
      newNode.prev = newNode; // Being circular, set the previous of the head to the head itself.

      _head = newNode; // Set the first head to the new node.
    } else { // If the linked list is NOT empty.
      Node last = _head!.prev!; // Set the last node to the previous of the head.

      newNode.next = _head; // Set the next of the new node to be the head.
      newNode.prev = last; // Set the previous of the new node to be the last node.
      _head!.prev = newNode; // Set the previous of the head to the new node.
      last.next = newNode; // Set the next of the last node to the new node.

      _head = newNode; // Set the new head to the new node.
    }

    _size++;
  }

  // Method to add a node to the end (right side).
  void addEnd(List<dynamic> value) {
    Node newNode = Node(value);

    if (_head == null) { // If the linked list IS empty.
      newNode.next = newNode; // Being circular, set the next of the head to the head itself.
      newNode.prev = newNode; // Being circular, set the previous of the head to the head itself.

      _head = newNode;
    } else { // If the linked list is NOT empty.
      Node last = _head!.prev!; // Set the last node to the previous of the head.

      newNode.next = _head; // Set the next of the new node to be the head.
      newNode.prev = last; // Set the previous of the new node to be the last node.
      last.next = newNode; // Set the next of the last node to the new node.
      _head!.prev = newNode; // Set the previous of the head to the new node.
    }

    _size++;
  }

  // Method to get the value of the first node (head).
  Node? getStart() {
    return _head; // Just return the value of the head.
  }

  // Method to get the value of the last node.
  Node? getEnd() {
    return _head?.prev; // Return the value of the previous node of the head (which would be the last node).
  }

  void remove(List<dynamic> songData) {
    if (_head == null) return;

    Node currentNode = _head!;
    do {
      if (currentNode.value[0]['id'].toString() == songData[0]['id'].toString()) { // If the current node's value (only song data) IS equal to the target value to remove.
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

  void clear() {
    if (_head == null) return;

    Node? currentNode = _head;
    do {
      Node? nextNode = currentNode!.next;

      currentNode.next = null;
      currentNode.prev = null;

      currentNode = nextNode;
    } while (currentNode != _head);

    _head = null;
    _size = 0;
  }

  int get size {
    return _size;
  }

  bool get isEmpty {
    return _size == 0;
  }

  void printStartingFromHead() {
    if (_head == null) return;

    Node? currentNode = _head;
    do {
      log('Debug Print: ${currentNode!.value}');

      currentNode = currentNode.next;
    } while (currentNode != _head);
  }
}

class Node {
  final List<dynamic> value;
  Node? next;
  Node? prev;

  Node(this.value);
}