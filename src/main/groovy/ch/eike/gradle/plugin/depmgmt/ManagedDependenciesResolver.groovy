package ch.eike.gradle.plugin.depmgmt

import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact
import ch.eike.gradle.plugin.depmgmt.domain.ManagedDependencies
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.gradle.api.artifacts.ExternalDependency

/**
 * The resolver for the managed dependencies.
 *
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
@Slf4j
class ManagedDependenciesResolver {

    private static Closure NOP_CLOSURE = {}
    private static final Closure FORCE_CLOSURE = {
        if (delegate instanceof ExternalDependency) {
            ((ExternalDependency)delegate).force = true
        }
    }

    private final ManagedDependencies managedDependencies
    private ServiceLoader<ManagedDependencyFinder> finderLoader

    protected ManagedDependenciesResolver(final ManagedDependencies managedDependencies) {
        this.managedDependencies = managedDependencies
    }

    @TupleConstructor
    static class ResolvedDependency {
        private Object notation
        private String configuration
        private Closure configure

        Object getNotation() { notation }
        String getConfiguration() { configuration }
        Closure getConfigure() { configure }
    }

    /**
     * Resolve the dependency notation provided using the configuration closure.
     * @param dependencyNotation the dependency notation
     * @param c the configuration closure
     * @return the resolved dependency
     */
    ResolvedDependency resolve(final Object dependencyNotation, final Closure c) {

        final Set<ManagedArtifact> candidates = []
        final List<String> usedFinders = []

        finders.find { final ManagedDependencyFinder finder ->
            usedFinders << finder.class.name
            !finder.resolve(dependencyNotation, managedDependencies.allArtifacts, candidates)
        }

        log.debug("Resolved candidates from {}: {}", candidates, usedFinders)

        if (!candidates.isEmpty()) {
            if (candidates.size() == 1) {
                final ManagedArtifact managed = candidates.first()
                return new ResolvedDependency(notation: managed.notation, configuration: managed.configuration, configure: configClosure(managed, c))
            }
            throw new IllegalArgumentException("Did not find a single candidate for '${dependencyNotation}': ${candidates.collect { ManagedArtifact art -> art.notation }}")
        }

        new ResolvedDependency(notation: dependencyNotation, configuration: (String)null, configure: c ?: NOP_CLOSURE)
    }

    /**
     * @return the {@link ManagedDependencyFinder}s to be used
     */
    protected Iterable<ManagedDependencyFinder> getFinders() {
        if (this.finderLoader == null) {
            this.finderLoader = ServiceLoader.load(ManagedDependencyFinder)
        }
        this.finderLoader.sort { it.orderId }
    }

    /**
     * Create a configuration closure to be used for configuring the dependencies
     * for a managed artifact and another configuration closure.
     * @param managedArtifact the managed artifact
     * @param other the other configuration closure
     * @return the closure
     */
    private static Closure configClosure(
            final ManagedArtifact managedArtifact, final Closure other) {
        (managedArtifact.force ? FORCE_CLOSURE : NOP_CLOSURE) >> (managedArtifact.configure ?: NOP_CLOSURE) >> (other ?: NOP_CLOSURE)
    }

}
