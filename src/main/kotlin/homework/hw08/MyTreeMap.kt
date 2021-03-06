// Homework #04 (29.09 - 13.10)
// Author: Kirill Smirenko, group 271
package homework.hw08

import java.util.*

/**
 * Map implementation with AVL tree.
 * @param K The type of tree's keys (must be comparable).
 * @param V The type of tree's values.
 */
class MyTreeMap<K, V>() : MyAbstractMap<K, V> where K : Comparable<K> {
    internal var root : Node<K, V>? = null

    override fun insert(newKey : K, newValue : V) {
        root = insert(root, MyMapEntry(newKey, newValue))
    }

    override fun insert(newEntry : MyMapEntry<K, V>) {
        root = insert(root, newEntry)
    }

    override fun search(key : K) : V? = search(root, key)

    override fun remove(key : K) {
        root = remove(root, key)
    }

    override fun newClassInstance() : MyAbstractMap<K, V> = MyTreeMap()

    override fun iterator() : Iterator<MyMapEntry<K, V>> = root?.iterator() ?: EmptyIterator()

    /**
     * A Node (empty, leaf or non-leaf) for AVL tree.
     * @param entry Map entry.
     * @param left Left subtree.
     * @param right Right subtree.
     */
    internal class Node<K : Comparable<K>, V>(entry : MyMapEntry<K, V>, left : Node<K, V>?, right : Node<K, V>?)
    : Iterable<MyMapEntry<K, V>> {
        var entry : MyMapEntry<K, V> = entry
        var left : Node<K, V>? = left
        var right : Node<K, V>? = right

        /**
         * Returns the height of the node.
         */
        fun getHeight() : Int = 1 + Math.max(left?.getHeight() ?: 0, right?.getHeight() ?: 0)

        /**
         * Returns the balance (right subtree height - left subtree height) of the node.
         */
        fun getBalance() : Int = (right?.getHeight() ?: 0) - (left?.getHeight() ?: 0)

        /**
         * Returns the smallest key of the node and its subtrees.
         */
        fun findMin() : K {
            val left_ = left
            return if (left_ == null) entry.key else left_.findMin()
        }

        /**
         * Returns the biggest key of the node and its subtrees.
         */
        fun findMax() : K {
            val right_ = right
            return if (right_ == null) entry.key else right_.findMax()
        }

        @Suppress("unused")
                /**
         * Creates an AVL tree using the node as root.
         */
        fun toTree() : MyTreeMap<K, V> {
            val tree = MyTreeMap<K, V>()
            tree.root = this
            return tree
        }

        override fun iterator() : Iterator<MyMapEntry<K, V>> = NodeIterator(this)

        // ROTATIONS

        internal fun rotateSmallLeft() {
            val nodeB = right
            if (nodeB != null) {
                left = Node(entry, left, nodeB.left)
                right = nodeB.right
                entry = nodeB.entry
            }
        }

        internal fun rotateSmallRight() {
            val nodeB = left
            if (nodeB != null) {
                right = Node(entry, nodeB.right, right)
                left = nodeB.left
                entry = nodeB.entry
            }
        }

        internal fun rotateBigLeft() {
            right?.rotateSmallRight()
            this.rotateSmallLeft()
        }

        internal fun rotateBigRight() {
            left?.rotateSmallLeft()
            this.rotateSmallRight()
        }

        internal fun restoreBalance() {
            val balanceA = getBalance()
            if (balanceA == 2) {
                val balanceB = right?.getBalance() ?: 0
                if (balanceB > 0) {
                    rotateSmallLeft();
                }
                else {
                    rotateBigLeft();
                }
            }
            else if (balanceA == -2) {
                val balanceB = left?.getBalance() ?: 0
                if (balanceB <= 0) {
                    rotateSmallRight();
                }
                else {
                    rotateBigRight();
                }
            }
        }
    }

    private class NodeIterator<K : Comparable<K>, V>(private val node : Node<K, V>) : Iterator<MyMapEntry<K, V>> {
        private val lIterator = node.left?.iterator() ?: EmptyIterator()
        private val rIterator = node.right?.iterator() ?: EmptyIterator()
        private var wasObserved : Boolean = false
        private var leftHasNext : Boolean = true
            get() =
            if (field) {
                field = lIterator.hasNext(); field
            }
            else false
        private var rightHasNext : Boolean = true
            get() =
            if (field) {
                field = rIterator.hasNext(); field
            }
            else false

        override fun hasNext() : Boolean {
            if (!wasObserved) return true
            if (leftHasNext ) return true
            if (rightHasNext) return true
            return false
        }

        override fun next() : MyMapEntry<K, V> {
            if (leftHasNext ) return lIterator.next()
            if (!wasObserved) {
                wasObserved = true;
                return node.entry
            }
            if (rightHasNext ) return rIterator.next()
            throw NoSuchElementException()
        }
    }

    // main functions

    private fun <K : Comparable<K>, V> insert(node : Node<K, V>?, entryN : MyMapEntry<K, V>) : Node<K, V>? {
        if (node == null) return Node(entryN, null, null)
        if (entryN.key == node.entry.key) {
            node.entry = entryN
            return node
        }
        else if (entryN.key < node.entry.key) {
            node.left = insert(node.left, entryN)
            node.restoreBalance()
            return node
        }
        else {
            node.right = insert(node.right, entryN)
            node.restoreBalance()
            return node
        }
    }

    private fun <K : Comparable<K>, V> remove(node : Node<K, V>?, keyR : K) : Node<K, V>? {
        // empty
        if (node == null) return null
        // leaf
        if ((node.left == null) && (node.right == null)) return if (node.entry.key == keyR) null else node
        // non-leaf
        if (node.entry.key == keyR) {
            if (node.getBalance() < 0) {
                // left subtree is higher
                val nearestKey = node.left!!.findMax()
                node.entry = MyMapEntry(nearestKey, search(node, nearestKey)!!)
                node.left = remove(node.left, nearestKey)
                node.restoreBalance()
                return node
            }
            else {
                // right subtree is higher
                val nearestKey = node.right!!.findMin()
                node.entry = MyMapEntry(nearestKey, search(node, nearestKey)!!)
                node.right = remove(node.right, nearestKey)
                node.restoreBalance()
                return node
            }
        }
        else if (keyR < node.entry.key) {
            node.left = remove(node.left, keyR)
            node.restoreBalance()
            return node
        }
        else {
            node.right = remove(node.right, keyR)
            node.restoreBalance()
            return node
        }
    }

    private fun <K : Comparable<K>, V> search(node : Node<K, V>?, keyS : K) : V? {
        if (node == null) return null
        if (keyS == node.entry.key) return node.entry.value
        else if (keyS < node.entry.key) return search(node.left, keyS)
        else return search(node.right, keyS)
    }
}