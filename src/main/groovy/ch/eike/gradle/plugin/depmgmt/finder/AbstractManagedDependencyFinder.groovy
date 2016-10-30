package ch.eike.gradle.plugin.depmgmt.finder

import ch.eike.gradle.plugin.depmgmt.ManagedDependencyFinder
import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact
import ch.eike.gradle.plugin.depmgmt.domain.ManagedGroup
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * Abstract implementation of {@link ManagedDependencyFinder}.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
@Slf4j
abstract class AbstractManagedDependencyFinder<T> implements ManagedDependencyFinder {

    /**
     * @return the notation's {@link Class} this finder applies to
     */
    protected abstract Class<T> getAppliesTo()

    /**
     * @see ManagedDependencyFinder#resolve(Object, Collection, Collection)
     */
    protected
    abstract boolean doResolve(T notation, Collection<ManagedArtifact> definedArtifacts, Collection<ManagedArtifact> candidates)

    @Override
    int getOrderId() {
        ManagedDependencyFinder.DEFAULT_ORDER_ID
    }

    @Override
    boolean resolve(Object notation, Collection<ManagedArtifact> definedArtifacts, Collection<ManagedArtifact> candidates) {
        if (!getAppliesTo()?.isInstance(notation)) {
            log.debug("Finder {} does not apply to {} for defined {}", getClass().name, notation, definedArtifacts)
            return true // The finder is not applicable
        }
        doResolve((T) notation, warnIfNull(definedArtifacts, "defined artifacts"), warnIfNull(candidates, "candidates list"))
    }

    private <T> Collection<T> warnIfNull(final Collection<T> src, final String name) {
        if (src == null) {
            log.warn("Collection {} is null in {}", name, getClass().name)
            return []
        }
        return src
    }

    /**
     * Create a managed artifact from a complete descriptor.
     * @param group
     * @param name
     * @param version
     * @param classifier
     * @return the managed artifact
     */
    protected static ManagedArtifact managedArtifact(
            final String group, final String name, final String version, final String classifier) {
        new ManagedArtifact(name, new ManagedGroup(group)).with {
            delegate.version = version
            delegate.classifier = classifier
            delegate
        }
    }

}
