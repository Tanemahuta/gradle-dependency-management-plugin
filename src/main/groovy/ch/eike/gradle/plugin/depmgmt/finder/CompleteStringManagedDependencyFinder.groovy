package ch.eike.gradle.plugin.depmgmt.finder

import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact
import groovy.transform.CompileStatic

/**
 * {@link AbstractManagedDependencyFinder} for {@link Map} notations which declare complete dependency notations.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
class CompleteStringManagedDependencyFinder extends AbstractManagedDependencyFinder<String> {

    @Override
    int getOrderId() {
        Integer.MAX_VALUE
    }

    @Override
    protected Class<String> getAppliesTo() {
        String
    }

    @Override
    protected boolean doResolve(
            final String notation,
            final Collection<ManagedArtifact> definedArtifacts, final Collection<ManagedArtifact> candidates) {
        final parts = notation.split(":")
        if (parts.length > 2) {
            candidates.add(managedArtifact(parts[0], parts[1], parts[2], parts.length > 3 ? parts[3] : null))
            return false // Complete descriptors will not continue resolving
        }
        return true
    }
}
