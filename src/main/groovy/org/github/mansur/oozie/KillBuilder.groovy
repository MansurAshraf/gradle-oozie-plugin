package org.github.mansur.oozie

import groovy.xml.MarkupBuilder

/**
 * @author Muhammad Ashraf
 * @since 7/27/13
 */
class KillBuilder extends BaseBuilder {

    def buildXML(MarkupBuilder xml, HashMap<String, Object> action, HashMap<String, Object> common) {
        HashMap<String, Object> map = getMergedProperties(common, action)
        xml.kill(name: map.get(NAME)) {
            message(map.get(MESSAGE))
        }
    }
}