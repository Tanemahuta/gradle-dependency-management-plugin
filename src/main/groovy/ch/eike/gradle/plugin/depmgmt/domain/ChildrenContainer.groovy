package ch.eike.gradle.plugin.depmgmt.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.util.function.Function

/**
 * A container for children which invokes a factory, if no child is registered
 * id is found.
 * @author cheike
 */
@EqualsAndHashCode
@ToString(includeNames = true, includeFields = true, excludes = "factory")
@CompileStatic
class ChildrenContainer<T> {

    private final Function<String, T> factory
    private final Map<String, T> children = [:]

    ChildrenContainer(final Function<String, T> factory) {
        this.factory = factory
    }

    T getOrCreate(final String id) {
        setAt(id, getAt(id) ?: factory.apply(id))
    }

    T getAt(final String id) {
        children[id]
    }

    T setAt(final String id, T item) {
        children[id] = item
        item
    }

    Collection<T> getChildren() {
        children.values()
    }

}
