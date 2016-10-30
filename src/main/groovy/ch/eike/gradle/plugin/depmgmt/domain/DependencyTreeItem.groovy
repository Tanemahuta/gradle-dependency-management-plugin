package ch.eike.gradle.plugin.depmgmt.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleDependency

/**
 * Created by cheike on 18.10.16.
 */
@CompileStatic
@EqualsAndHashCode(includeFields = true)
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
abstract class DependencyTreeItem {

    /**
     * The default configuration to be set
     */
    static final DEFAULT_CONFIGURATION = 'compile'

    /**
     * the parent item
     */
    protected final DependencyTreeItem parent

    /**
     * the identifier of the dependency item
     */
    final String id
    /**
     * the version of the dependency item
     */
    String version
    /**
     * the classifier of the dependency item
     */
    String classifier
    /**
     * the configuration to be used by default (defaults to {@link DependencyTreeItem#DEFAULT_CONFIGURATION})
     */
    String configuration = DEFAULT_CONFIGURATION
    /**
     * {@code true} if to enforce the version of this dependency (defaults to {@code false}
     */
    boolean force = false
    /**
     * Add enhanced configuration using a {@link ModuleDependency}
     */
    Closure configure

    /**
     * Create a new item using a parent ot inherit the configuration values
     * @param id the identifier of the element
     * @param parent the parent to inherit from
     */
    DependencyTreeItem(final String id, final DependencyTreeItem parent) {
        this.id = id
        this.parent = parent
        this.version = parent?.version
        this.classifier = parent?.classifier
        this.configuration = parent?.configuration ?: this.configuration
        this.force = parent?.force
    }

    void configure(@DelegatesTo(ModuleDependency) Closure configure) {
        this.configure = configure
    }

    protected static Closure extractClosureFromArgs(Object args) {
        final arguments = args instanceof Object[] ? args as List : [args]
        if (!arguments.isEmpty() && arguments.first() instanceof Closure) {
            return (Closure) arguments.first()
        }
        null
    }

}
