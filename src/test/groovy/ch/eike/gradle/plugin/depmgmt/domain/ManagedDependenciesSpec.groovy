package ch.eike.gradle.plugin.depmgmt.domain

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class ManagedDependenciesSpec extends Specification {

    @Subject
    private final managedDependencies = new ManagedDependencies()

    def "adding a group and configuring it"() {
        when:
        final group = managedDependencies.group("test") {
            configuration = "xyz"
            version = "a.b.c"
            classifier = "uvw"

        }
        then:
        managedDependencies["test"].is(group)
        and:
        group.id == "test"
        and:
        group.configuration == "xyz"
        and:
        group.version == "a.b.c"
        and:
        group.classifier == "uvw"
    }

    @Unroll
    def "configuring an artifact from #descriptor with params #params"() {
        when:
        final artifact = managedDependencies.invokeMethod(descriptor, {
            final target = delegate
            params.each{ k, v -> target.setProperty(k, v) }
        })

        then:
        artifact instanceof ManagedArtifact
        and:
        artifact.id == "xy"
        and:
        artifact.configuration == "bla"
        and:
        artifact.version == "a.b.c"
        and:
        artifact.classifier == "uvw"
        and:
        artifact.parent.is(managedDependencies["test"])
        and:
        artifact.parent['xy'].is(artifact)
        where:
        descriptor          | params
        'test:xy'           | [version: 'a.b.c', classifier: 'uvw', configuration: 'bla']
        'test:xy:a.b.c'     | [classifier: 'uvw', configuration: 'bla']
        'test:xy:a.b.c:uvw' | [configuration: 'bla']
    }

    def "GetAllArtifacts"() {

    }
}
