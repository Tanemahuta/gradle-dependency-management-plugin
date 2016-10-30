package ch.eike.gradle.plugin.depmgmt.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * An artifact under dependency management.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@EqualsAndHashCode(includeFields = true, callSuper = true)
@ToString(includeNames = true, ignoreNulls = true, includePackage = false, includeSuper = true)
@CompileStatic
class ManagedArtifact extends DependencyTreeItem implements DescriptorConsumer<ManagedArtifact> {

    /**
     * Creates a new artifact with the given identifier.
     * @param id the identifier
     */
    ManagedArtifact(final String id, final ManagedGroup group) {
        super(id, group)
    }

    @Override
    ManagedArtifact consume(Queue<String> descriptorParts) {
        if (!descriptorParts.isEmpty()) {
            version = descriptorParts.poll()
        }
        if (!descriptorParts.isEmpty()) {
            classifier = descriptorParts.poll()
        }
        if (!descriptorParts.isEmpty()) {
            throw new IllegalArgumentException("Found remaining descriptors: ${descriptorParts}.")
        }
        this
    }

    /**
     * @return the group id of the artifact
     */
    String getGroupId() { parent?.id }

    /**
     * @return a gradle compatible dependency notation for this
     */
    Map<String, String> getNotation() {
        [
                group     : groupId,
                name      : id,
                version   : version,
                classifier: classifier
        ]
    }

}
