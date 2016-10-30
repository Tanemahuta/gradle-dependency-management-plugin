package ch.eike.gradle.plugin.depmgmt.domain

/**
 * Created by cheike on 30.10.16.
 */
interface DescriptorConsumer<T> {

    T consume(Queue<String> descriptorParts)

}