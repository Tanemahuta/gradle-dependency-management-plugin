package ch.eike.gradle.plugin.depmgmt.finder

import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact
import groovy.transform.CompileStatic

/**
 * {@link AbstractManagedDependencyFinder} for {@link Map} notations which declare complete dependency notations.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
class CompleteMapManagedDependencyFinder extends AbstractManagedDependencyFinder<Map<String, String>> {

    @Override
    int getOrderId() {
        Integer.MAX_VALUE
    }

    @Override
    protected Class<Map<String, String>> getAppliesTo() {
        Map
    }

    @Override
    protected boolean doResolve(
            final Map<String, String> notation,
            final Collection<ManagedArtifact> definedArtifacts, final Collection<ManagedArtifact> candidates) {
        if (notation.group && notation.name && notation.version) {
            candidates.add(managedArtifact(notation.group, notation.name, notation.version, notation.classifier))
            return false // Complete descriptors will not continue resolving
        }
        return true
    }
}
