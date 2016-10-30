package ch.eike.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by cheike on 30.10.16.
 */
class ManagedDependenciesPluginSpec extends Specification {

    private final project = ProjectBuilder.builder().build()

    @Unroll
    def "test apply and use managed dependencies from descriptor #descriptor"() {
        setup:
        project.apply plugin: ManagedDependenciesPlugin
        final matcher = {
            it.group == 'org.slf4j' && it.name == 'slf4j-api' && it.version == '1.0.4'
        }
        project.configurations {
            compile
            funny
        }

        when:
        project.dependencies.management {
            'org.slf4j:slf4j-api:1.0.4:classic' {
                configuration = 'funny'
            }
        }
        and:
        project.dependencies.compile descriptor
        and:
        project.dependencies.managed descriptor


        then:
        project.configurations.compile.dependencies.any(matcher)
        and:
        project.configurations.funny.dependencies.any(matcher)

        where:
        descriptor << [
                'org.slf4j',
                'slf4j-api',
                'org.slf4j:slf4j-api',
                'org.slf4j:slf4j-api:1.0.4',
                'org.slf4j:slf4j-api:1.0.4:classic',
                [group: 'org.slf4j'],
                [name: 'slf4j-api'],
                [group: 'org.slf4j', name: 'slf4j-api', version: '1.0.4'],
                [group: 'org.slf4j', name: 'slf4j-api', version: '1.0.4', classifier: 'classic']
        ]
    }

}
