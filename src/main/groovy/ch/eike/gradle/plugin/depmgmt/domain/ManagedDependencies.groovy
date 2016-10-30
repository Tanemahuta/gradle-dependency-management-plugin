package ch.eike.gradle.plugin.depmgmt.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.util.function.Function

/**
 * The container for all managed dependencies in a tree.
 * @author Christian Heike <christian.heike@icloud.com>
 */
//@CompileStatic
@EqualsAndHashCode(includeFields = true)
@ToString(includeNames = true, includePackage = false, ignoreNulls = true, includeFields = true)
class ManagedDependencies implements DescriptorConsumer<DependencyTreeItem>, Function<String, ManagedGroup> {

    private final DescriptorParser<DependencyTreeItem> parser = new DescriptorParser<>(this)
    private final ChildrenContainer<ManagedGroup> childrenContainer = new ChildrenContainer<>(this)

    @Override
    DependencyTreeItem consume(final Queue<String> descriptorParts) {
        def result = childrenContainer.getOrCreate(descriptorParts.poll())
        if (!descriptorParts.isEmpty()) {
            result = result.consume(descriptorParts)
        }
        result
    }

    @Override
    ManagedGroup apply(final String id) {
        new ManagedGroup(id)
    }

    /**
     * Define a managed group.
     * @param id the groupId
     * @param c the configuration closure to be used
     * @return the group defined
     */
    ManagedGroup group(final String id, @DelegatesTo(ManagedGroup) final Closure c) {
        if (id.contains(":")) {
            throw new IllegalArgumentException("A group id may not contain a ':'.")
        }
        (ManagedGroup)parser.consume(id, c)
    }

    ManagedGroup getAt(final String id) {
        childrenContainer[id]
    }

    /**
     * Helper groovy method for defining groups easily:
     * {@code depMgmt.'myGroup' { ... }}
     * @param name the name of the group (method name)
     * @param args arguments (in this case the configuration closure)
     * @return @see #group(String, Closure)
     */
    def methodMissing(final String name, Object args) {
        parser.consume(name, args)
    }

    /**
     * @return an unmodifiable {@link Collection} of {@link ManagedArtifact}s defined in this container
     */
    Collection<ManagedArtifact> getAllArtifacts() {
        Collections.unmodifiableCollection(childrenContainer.children.collect {
            it.artifacts
        }.flatten() as List<ManagedArtifact>)
    }

}
