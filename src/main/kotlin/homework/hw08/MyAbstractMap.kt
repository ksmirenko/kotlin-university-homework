// Homework #04 (29.09 - 13.10)
// Author: Kirill Smirenko, group 271
package homework.hw08

import java.util.*

/**
 * Abstract map interface.
 * @param K The type of map's keys (must be comparable).
 * @param V The type of map's values.
 */
interface MyAbstractMap<K, V> : Iterable<MyMapEntry<K, V>> where K : Comparable<K> {

    /**
     * Inserts a new entry ([newKey], [newValue]) into the map.
     */
    fun insert(newKey : K, newValue : V)

    /**
     * Inserts [newEntry] into the map.
     */
    fun insert(newEntry : MyMapEntry<K, V>)

    /**
     * Returns the value of the entry with [key], if found, or null otherwise.
     */
    fun search(key : K) : (V?)

    /**
     * Removes the entry with [key], if possible.
     */
    fun remove(key : K)

    /**
     * Returns whether this map contains an entry with [key]
     */
    operator fun contains(key : K) : Boolean = (search(key) != null)

    /**
     * Applies f to each of the map entries.
     * The order depends on AbstractMap implementation and cannot be guaranteed or predicted.
     */
    //fun forEach(f : (K, V) -> Unit)

    /**
     * Creates and returns a new empty instance of the same class as this object's.
     */
    fun newClassInstance() : MyAbstractMap<K, V>

    /**
     * Returns a new map constructed by uniting this map with [anotherMap].
     * In case of key collision the data from this map is used.
     */
    fun uniteWith(anotherMap : MyAbstractMap<K, V>) : MyAbstractMap<K, V> {
        val newMap = newClassInstance()
        for (entry in this) {
            newMap.insert(entry)
        }
        for (entry in anotherMap) {
            if (!this.contains(entry.key)) {
                newMap.insert(entry)
            }
        }
        return newMap
    }

    /**
     * Returns a new map constructed by intersecting this map with [anotherMap].
     * In case of key collision the data from this map is used.
     */
    fun intersectWith(anotherMap : MyAbstractMap<K, V>) : MyAbstractMap<K, V> {
        val newMap = newClassInstance()
        for (entry in this) {
            if (anotherMap.contains(entry.key)) {
                newMap.insert(entry)
            }
        }
        return newMap
    }

    /**
     * Converts an AbstractMap to list and sorts it.
     */
    fun toSortedList() : List<Pair<K, V>> {
        // <K : Comparable<K>, V> AbstractMap<K, V>.
        val list = LinkedList<Pair<K, V>>()
        for (entry in this) {
            list.add(entry.key to entry.value)
        }
        return list.sortedBy { it.first }
    }
}

data class MyMapEntry<K, V>(val key : K, val value : V) {
    override fun equals(other : Any?) : Boolean = (other is MyMapEntry<*, *>) && (key?.equals(other.key) ?: false)
    override fun hashCode() : Int = key?.hashCode() ?: 0
}

internal class EmptyIterator<A>() : Iterator<A> {
    override fun hasNext() : Boolean = false
    override fun next() : A {
        throw NoSuchElementException()
    }
}