package ch.eike.gradle.plugin.depmgmt.domain

import groovy.transform.CompileStatic

/**
 * Parser for a descriptor, which delegates the consumption to {@link DescriptorConsumer}.
 * @author cheike
 */
@CompileStatic
class DescriptorParser<RESULT> {

    private final DescriptorConsumer<RESULT> consumer

    DescriptorParser(final DescriptorConsumer<RESULT> consumer) {
        this.consumer = consumer
    }

    RESULT consume(final String descriptor, final Object args) {
        final parts = (descriptor.split(":") as List<String>) as Queue<String>
        final result = consumer.consume(parts)
        final c = extractClosureFromArgs(args)
        c?.delegate = result
        c?.call()
        result
    }

    private static Closure extractClosureFromArgs(Object args) {
        final arguments = args instanceof Object[] ? args as List : [args]
        if (!arguments.isEmpty() && arguments.first() instanceof Closure) {
            return (Closure) arguments.first()
        }
        null
    }

}
