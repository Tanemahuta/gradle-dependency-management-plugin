package ch.eike.gradle.plugin.depmgmt.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.util.function.Function

/**
 * A group containing managed artifactMap under dependency management.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@EqualsAndHashCode(includeFields = true, callSuper = true)
@ToString(includeNames = true, includeFields = true, includePackage = false, ignoreNulls = true, includeSuper = true)
@CompileStatic
class ManagedGroup extends DependencyTreeItem implements DescriptorConsumer<ManagedArtifact>, Function<String, ManagedArtifact> {

    private final DescriptorParser<ManagedArtifact> descriptorParser = new DescriptorParser<ManagedArtifact>(this)
    private final ChildrenContainer<ManagedArtifact> childrenContainer = new ChildrenContainer<ManagedArtifact>(this)

    /**
     * Creates a group with the given identifier.
     * @param id the identifier
     */
    ManagedGroup(final String id) {
        super(id, null)
    }

    @Override
    ManagedArtifact consume(final Queue<String> descriptorParts) {
        def result = childrenContainer.getOrCreate(descriptorParts.poll())
        if (!descriptorParts.isEmpty()) {
           result = result.consume(descriptorParts)
        }
        result
    }

    @Override
    ManagedArtifact apply(final String id) {
        new ManagedArtifact(id, this)
    }

    /**
     * Configure a {@link ManagedArtifact}.
     * @param name the artifact id
     * @param c the configuration closure
     * @return the configured managed artifact
     */
    ManagedArtifact artifact(final String name, @DelegatesTo(ManagedArtifact) final Closure c) {
        if (name.contains(":")) {
            throw new IllegalArgumentException("Name should not contain ':'.")
        }
        descriptorParser.consume(name, c)
    }

    ManagedArtifact getAt(final String id) {
        childrenContainer[id]
    }

    def methodMissing(final String name, final Object args) {
        descriptorParser.consume(name, args)
    }

    /**
     * @return a list of managed artifactMap
     */
    Collection<ManagedArtifact> getArtifacts() {
        childrenContainer.children
    }

}
