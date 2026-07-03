package com.example.musik.data.misc

import android.util.Log
import com.example.musik.data.data_classes.MusicDetails

class CircularDoublyLinkedList {
	class Node(val musicDetails: MusicDetails) {
		lateinit var next: Node
		lateinit var prev: Node
	}

	private var _head: Node? = null
	private var _tail: Node? = null // Not needed, just for readability (could just do _head.prev)
	private var _size: Int = 0

	private val isHeadAndTailNotNull: Boolean get() = _head != null && _tail != null

	val isEmpty: Boolean get() = _head == null && _tail == null


	fun addStart(newMusicDetails: MusicDetails) {
		val newNode = Node(newMusicDetails)

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

	fun addEnd(newMusicDetails: MusicDetails) {
		val newNode = Node(newMusicDetails)

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

	fun addAfterAnother(musicDetailsOld: MusicDetails, musicDetailsNew: MusicDetails) {
		check(isHeadAndTailNotNull)

		val newNode = Node(musicDetailsNew)

		var curNode: Node = _head!!
		do {
			if (curNode.musicDetails == musicDetailsOld) {
				if (curNode == _tail) {
					_tail = newNode
				}

				newNode.next = curNode.next
				newNode.prev = curNode
				curNode.next.prev = newNode
				curNode.next = newNode

				_size++

				break
			} else {
				curNode = curNode.next
			}
		} while (curNode != _head && curNode.musicDetails != musicDetailsOld)
	}

	// remove() returns whether or not the execution ran correctly or not
	fun remove(musicDetails: MusicDetails): Boolean {
		check(isHeadAndTailNotNull)

		if (_size == 1) {
			if (musicDetails != _head!!.musicDetails) {
				return false
			} else {
				_head = null
				_tail = null

				_size--

				return true
			}
		} else {
			var curNode: Node = _head!!
			do {
				if (curNode.musicDetails == musicDetails) {
					curNode.next.prev = curNode.prev
					curNode.prev.next = curNode.next
					when (curNode) {
						_head -> _head = curNode.next
						_tail -> _tail = curNode.prev
					}

					_size--

					return true
				} else {
					curNode = curNode.next
				}
			} while (curNode != _head)
		}

		return false
	}

	fun swap(musicDetails1: MusicDetails, musicDetails2: MusicDetails) {
		remove(musicDetails1)
		addAfterAnother(musicDetails2, musicDetails1)
	}

	fun getNode(musicDetails: MusicDetails): Node? {
		check(isHeadAndTailNotNull)

		var curNode: Node = _head!!
		do {
			if (curNode.musicDetails == musicDetails) {
				return curNode
			} else {
				curNode = curNode.next
			}
		} while (curNode != _head)

		Log.e("CircularDoublyLinkedList", "ID does not match with any node!")
		return null
	}

	// This function gets a random node EXCLUDING the provided id
	fun getRandomNode(musicDetails: MusicDetails): Node? {
		check(isHeadAndTailNotNull)

		if (_size == 1) {
			return _head
		} else {
			var curNode: Node = _head!!
			if (curNode.musicDetails == musicDetails) {
				curNode = curNode.next
			}

			val randNum: Int = (0..<(_size - 1)).random()
			repeat(randNum) {
				curNode = curNode.next

				if (curNode.musicDetails == musicDetails) {
					curNode = curNode.next
				}
			}

			return curNode
		}
	}

	fun getStartMusicDetails(): MusicDetails {
		check(isHeadAndTailNotNull)

		return _head!!.musicDetails
	}

	fun getEndMusicDetails(): MusicDetails {
		check(isHeadAndTailNotNull)

		return _tail!!.musicDetails
	}

	fun clear() {
		_head = null
		_tail = null
		_size = 0
	}


	fun toList(): List<MusicDetails> {
		if (isEmpty) {
			return emptyList()
		} else {
			val result = mutableListOf<MusicDetails>()
			var curNode: Node = _head!!

			repeat(_size) {
				result.add(curNode.musicDetails)
				curNode = curNode.next
			}

			return result
		}
	}
}