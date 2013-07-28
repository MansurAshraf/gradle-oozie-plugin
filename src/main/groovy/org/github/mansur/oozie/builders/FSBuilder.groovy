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

package org.github.mansur.oozie.builders

import groovy.xml.MarkupBuilder

/**
 * @author Muhammad Ashraf
 * @since 7/24/13
 */
class FSBuilder extends BaseBuilder {
    def buildXML(MarkupBuilder xml, HashMap<String, Object> action, HashMap<String, Object> common) {
        HashMap<String, Object> map = getMergedProperties(common, action)
        xml.action(name: map.get(NAME)) {
            'fs' {
                addDeleteOrDir(xml, map.get(DELETE), DELETE)
                addDeleteOrDir(xml, map.get(MKDIR), MKDIR)
                addMove(xml, map.get(MOVE))
                addChmod(xml, map.get(CHMOD))
            }
            addOkOrError(xml, map, "ok")
            addOkOrError(xml, map, "error")
        }
    }

    private def addMove(MarkupBuilder xml, List<HashMap<String, String>> nodes) {
        if (nodes != null) {
            nodes.each {
                xml.move(source: it.get("source"), target: it.get("target"))
            }
        }
    }

    private def addChmod(MarkupBuilder xml, List<HashMap<String, String>> nodes) {
        if (nodes != null) {
            nodes.each {
                xml.chmod(path: it.get("path"), permissions: it.get("permissions"), 'dir-files': it.get("dir_files"))
            }
        }
    }

}
