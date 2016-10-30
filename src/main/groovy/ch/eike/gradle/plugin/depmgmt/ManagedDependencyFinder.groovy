package ch.eike.gradle.plugin.depmgmt

import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact

/**
 * A single managed dependency resolver.
 *
 * @author Christian Heike <christian.heike@icloud.com>
 */
interface ManagedDependencyFinder {

    static final int DEFAULT_ORDER_ID = 0

    /**
     * Resolve the provided notation from the defined {@link ManagedArtifact}s.
     * @param notation the notation to be resolved
     * @param definedArtifacts the artifacts defined
     * @param candidates the candidates of the artifacts
     * @return true if to continue resolving, false otherwise
     */
    boolean resolve(Object notation, Collection<ManagedArtifact> definedArtifacts, Collection<ManagedArtifact> candidates)

    /**
     * @return the order (the lower the better) to use this in resolving
     */
    int getOrderId()

}