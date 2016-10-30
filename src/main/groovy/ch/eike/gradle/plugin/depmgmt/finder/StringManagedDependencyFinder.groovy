package ch.eike.gradle.plugin.depmgmt.finder

import ch.eike.gradle.plugin.depmgmt.domain.ManagedArtifact
import groovy.transform.CompileStatic

/**
 * A {@link AbstractManagedDependencyFinder} for {@link String} notations.
 * @author Christian Heike <christian.heike@icloud.com>
 */
@CompileStatic
class StringManagedDependencyFinder extends AbstractManagedDependencyFinder<String> {

    @Override
    protected Class<String> getAppliesTo() {
        return String
    }

    @Override
    protected boolean doResolve(
            final String notation,
            final Collection<ManagedArtifact> definedArtifacts, final Collection<ManagedArtifact> candidates) {
        final foundCandidates = findCandidates(notation.split(":") as List, definedArtifacts)
        candidates.addAll(foundCandidates)
        return foundCandidates.isEmpty()
    }

    private static Collection<ManagedArtifact> findCandidates(
            final List<String> notationParts, final Collection<ManagedArtifact> definedArtifacts) {
        switch (notationParts?.size() ?: 0) {
            case 1:
                return definedArtifacts.findAll {
                    it.groupId == notationParts[0] ||
                            it.id == notationParts[0]
                }
            case 2:
                return definedArtifacts.findAll {
                    it.groupId == notationParts[0] &&
                            it.id == notationParts[1]
                }
            case 3:
                return definedArtifacts.findAll {
                    it.groupId == notationParts[0] &&
                            it.id == notationParts[1] &&
                            it.version == notationParts[2]
                }
            case 4:
                return definedArtifacts.findAll {
                    it.groupId == notationParts[0] &&
                            it.id == notationParts[1] &&
                            it.version == notationParts[2] &&
                            it.classifier == notationParts[3]
                }
            default:
                return []
        }
    }

}
