package ch.eike.gradle.plugin.depmgmt.finder

import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact
import groovy.transform.CompileStatic

/**
 * {@link AbstractManagedDependencyFinder} for {@link Map} notations.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
class MapManagedDependencyFinder extends AbstractManagedDependencyFinder<Map<String, String>> {

    @Override
    protected Class<Map<String, String>> getAppliesTo() {
        Map
    }

    @Override
    protected boolean doResolve(
            final Map<String, String> notation,
            final Collection<ManagedArtifact> definedArtifacts, final Collection<ManagedArtifact> candidates) {
        final foundArtifacts = definedArtifacts.findAll {
            notation.group == null || notation.group == it.groupId &&
                    notation.name == null || notation.name == it.id &&
                    notation.version == null || notation.version == it.version &&
                    notation.classifier == null || notation.classifier == it.classifier
        }
        candidates.addAll(foundArtifacts)
        return foundArtifacts.isEmpty()
    }
}
