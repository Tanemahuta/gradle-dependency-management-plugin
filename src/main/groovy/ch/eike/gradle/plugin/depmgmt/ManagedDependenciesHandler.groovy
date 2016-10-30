package ch.eike.gradle.plugin.depmgmt

import ch.eike.gradle.plugin.depmgmt.domain.ManagedDependencies
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
/**
 * The replacement {@link DependencyHandler} which supports management
 * and resolving through managed dependencies.
 *
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
@Slf4j
class ManagedDependenciesHandler implements DependencyHandler {

    /**
     * The delegate which will be used to access the original functionality
     */
    @Delegate
    private final DependencyHandler decorated
    private final ManagedDependencies managedDependencies
    private final ManagedDependenciesResolver dependencyResolver

    ManagedDependenciesHandler(final DependencyHandler decorated) {
        this.decorated = decorated;
        this.managedDependencies = new ManagedDependencies()
        this.dependencyResolver = new ManagedDependenciesResolver(this.managedDependencies)
    }

    /**
     * @return the dependency management section
     */
    ManagedDependencies getManagement() {
        return managedDependencies
    }

    /**
     * Configures the {@link ManagedDependencies}
     * @param config the configuration closure applied to the dependencies
     * @return the dependencies
     */
    ManagedDependencies management(final Closure config) {
        config?.delegate = managedDependencies
        config.resolveStrategy = Closure.DELEGATE_FIRST
        config?.call()
        managedDependencies
    }

    /**
     * @see ManagedDependenciesHandler#managed(Object, Closure)
     */
    Dependency managed(Object dependencyNotation) {
        managed(dependencyNotation, null)
    }

    /**
     * Add a managed dependency to the default configuration set for the dependency.
     * @param dependencyNotation the notation
     * @param configClosure the configuration closure to be used
     * @return the dependency
     */
    Dependency managed(Object dependencyNotation, Closure configClosure) {
        withResolved(dependencyNotation, configClosure) {
            decorated.add(configuration, notation, configure)
        }
    }

    @Override
    Dependency add(String configurationName, Object dependencyNotation) {
        add(configurationName, dependencyNotation)
    }

    @Override
    Dependency add(String configurationName, Object dependencyNotation, Closure configClosure) {
        withResolved(dependencyNotation, configClosure) {
            decorated.add(configurationName, notation, configure)
        }
    }

    @Override
    Dependency create(Object dependencyNotation) {
        create(dependencyNotation, null)
    }

    @Override
    Dependency create(Object dependencyNotation, Closure configureClosure) {
        withResolved(dependencyNotation, configureClosure) {
            create(notation, configure)
        }
    }

    @Override
    Dependency module(Object notation) {
        module(notation, null)
    }

    @Override
    Dependency module(Object dependencyNotation, Closure configureClosure) {
        withResolved(dependencyNotation, configureClosure) {
            decorated.module(notation, configure)
        }
    }

    private <T> T withResolved(Object dependencyNotation, Closure configClosure,
                               @DelegatesTo(ManagedDependenciesResolver.ResolvedDependency) Closure<T> application) {
        final resolved = dependencyResolver.resolve(dependencyNotation, configClosure)
        application.delegate = resolved
        application.call()
    }

    def methodMissing(String name, Object args) {
        final Object[] argsArray = (Object[]) args;
        final notation = argsArray[0]
        final c = (Closure)(argsArray.length > 1 && argsArray[1] instanceof Closure ? argsArray[1] : null)
        add(name, notation, c)
    }

}
