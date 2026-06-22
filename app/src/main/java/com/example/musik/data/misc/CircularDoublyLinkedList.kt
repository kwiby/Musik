package com.example.musik.data.misc

/*
class CircularDoublyLinkedList {
	class Node(val id: Int) {
		lateinit var next: Node
		lateinit var prev: Node
	}

	private var _head: Node? = null
	private var _tail: Node? = null
	private var _size: Int = 0

	private val isHeadAndTailNotNull get() = _head != null && _tail != null

	val isEmpty get() = _head == null && _tail == null

	fun addAfter() {
		// TODO: Finish implementation

	}

	fun addStart(newId: Int) {
		val newNode = Node(newId)

		if (isEmpty) {
			newNode.next = newNode
			newNode.prev = newNode

			_tail = newNode
		} else {
			check(isHeadAndTailNotNull) // Check works in production by default

			newNode.next = _head!!
			newNode.prev = _tail!!
			_head!!.prev = newNode
			_tail!!.next = newNode
		}
		_head = newNode

		_size++
	}

	fun addEnd(newId: Int) {
		val newNode = Node(newId)

		if (isEmpty) {
			newNode.next = newNode
			newNode.prev = newNode

			_head = newNode
		} else {
			check(isHeadAndTailNotNull)

			newNode.next = _head!!
			newNode.prev = _tail!!
			_head!!.prev = newNode
			_tail!!.next = newNode
		}
		_tail = newNode

		_size++
	}

	fun remove(id: Int) {
		fun removeNode(node: Node) {
			check(_head != _tail)

			node.next.prev = node.prev
			node.prev.next = node.next

			if (node == _head) {
				_head = node.next
			} else if (node == _tail) {
				_tail = node.prev
			}

			_size--
		}

		check(isHeadAndTailNotNull)
		if (_size == 1) {
			_head = null
			_tail = null

			_size--
		} else {
			var curNode = _head!!
			while (curNode != _tail) {
				if (curNode.id == id) {
					removeNode(curNode)

					break
				} else {
					curNode = curNode.next

					if (curNode.id == id) {
						removeNode(curNode)

						break
					}
				}
			}
		}
	}

	fun clear() {
		// TODO: Finish implementation
	}

	fun swap() {
		// TODO: Finish implementation
	}

	fun getNode(): Node? {
		// TODO: Finish implementation
	}

	fun getRandomNode(): Node? {
		// TODO: Finish implementation
	}

	fun getStart(): Node? {
		// TODO: Finish implementation
	}

	fun getEnd(): Node? {
		// TODO: Finish implementation
	}
}
 */