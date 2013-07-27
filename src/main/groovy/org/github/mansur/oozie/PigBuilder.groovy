/*
 * Copyright 2013. Muhammad Ashraf
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.github.mansur.oozie

import groovy.xml.MarkupBuilder

/**
 * @author Muhammad Ashraf
 * @since 7/24/13
 */
class PigBuilder extends BaseBuilder {

    def buildXML(MarkupBuilder xml, HashMap<String, Object> action, HashMap<String, Object> common) {
        HashMap<String, Object> map = getMergedProperties(common, action)
        xml.action(name: map.get(NAME)) {
            'pig' {
                addNode(map, xml, 'job-tracker', JOB_TRACKER)
                addNode(map, xml, 'name-node', NAME_NODE)
                addPrepareNodes(xml, (List<String>) map.get(DELETE), (List<String>) map.get(MKDIR))
                addNode(map, xml, 'job-xml', JOB_XML)
                xml.configuration { addConfiguration(xml, map) }
                addNode(map, xml, SCRIPT, SCRIPT)
                addList(xml, map, "param", "params")
                addNode(map, xml, FILE, FILE)
                addNode(map, xml, ARCHIVE, ARCHIVE)
            }
            addOkOrError(xml, map, "ok")
            addOkOrError(xml, map, "error")
        }
    }


}
