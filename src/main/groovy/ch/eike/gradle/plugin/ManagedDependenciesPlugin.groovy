package ch.eike.gradle.plugin

import ch.eike.gradle.plugin.depmgmt.ManagedDependenciesHandler
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.project.DefaultProject

/**
 * A gradle {@link Plugin} for managing.
 * Usage:
 * <blockquote>
 *     <pre>
 *         apply plugin: 'dependency-management'
 *         dependencies {
 *             management {
 *                 'org.slf4j' {
 *                      version = '1.0.0'
 *                      configuration = 'compile'
 *                      configure {
 *                          exclude group: 'org.apache', module: 'commons'
 *                      }
 *                      artifact 'slf4j-api'
 *                      'slf4j-simple-impl' {
 *                          version = '1.0.1'
 *                          configuration = 'runtime'
 *                      }
 *                 }
 *                 'org.slf4j:slf4j-funny-impl:1.2.3' {
 *                     configuration = 'compileOnly'
 *                 }
 *             }
 *         }
 *     </pre>
 * </blockquote>
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
@Slf4j
class ManagedDependenciesPlugin implements Plugin<DefaultProject> {

    @Override
    void apply(final DefaultProject project) {
        log.debug("Replacing original dependency handler on {}...", project)
        final DependencyHandler original = project.getDependencies()
        final ManagedDependenciesHandler replacement = new ManagedDependenciesHandler(original)
        project.setDependencyHandler(replacement)
        log.info("Replaced dependency handler {} on project {}", original, project)
    }
}
