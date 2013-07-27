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
abstract class BaseBuilder {


    public static final String NAME = "name"
    public static final String JOB_TRACKER = "jobTracker"
    public static final String NAME_NODE = "namenode"
    public static final String DELETE = "delete"
    public static final String MKDIR = "mkdir"
    public static final String CONFIGURATION = "configuration"
    public static final String JOB_XML = "jobXML"
    public static final String FILE = 'file'
    public static final String ARCHIVE = 'archive'
    public static final String SCRIPT = 'script'
    public static final String HOST = 'host'
    public static final String COMMAND = 'command'
    public static final String ARGS = "args"
    public static final String MOVE = "move"
    public static final String CHMOD = "chmod"
    public static final String MAIN_CLASS = "mainClass"
    public static final String JAVA_OPTS = "javaOpts"
    public static final String EXEC = 'exec'
    public static final String ENV_VAR = "envVar"
    public static final String MESSAGE = "message"


    protected HashMap<String, Object> getMergedProperties(HashMap<String, Object> common, HashMap<String, Object> action) {
        def map = new HashMap<String, Object>(common)
        map.putAll(action)
        map
    }

    protected void addNode(HashMap<String, Object> map, MarkupBuilder xml, String node, String attr) {
        if (map.containsKey(attr)) {
            xml."$node"(map.get(attr))
        }
    }

    protected void addConfiguration(MarkupBuilder xml, HashMap<String, Object> map) {
        if (map.containsKey(CONFIGURATION)) {
            HashMap<String, String> config = map.get(CONFIGURATION)
            config.each { k, v ->
                xml.property {
                    name(k)
                    value(v)
                }

            }
        }
    }

    protected def addPrepareNodes(MarkupBuilder xml, List<String> deletes, List<String> dirs) {
        if (deletes != null || dirs != null) {
            xml.prepare {
                addDeleteOrDir(xml, deletes, DELETE)
                addDeleteOrDir(xml, dirs, MKDIR)
            }
        }
    }

    protected def addDeleteOrDir(MarkupBuilder xml, List<String> nodes, String attr) {
        if (nodes != null) {
            nodes.each {
                xml."$attr"(path: it)
            }
        }
    }

    protected def addList(MarkupBuilder xml, HashMap<String, Object> map, String node, String attr) {
        if (map.get(attr) != null) {
            def params = map.get(attr)
            params.each {
                xml."$node"(it)
            }
        }
    }

    def addCaptureOutput(MarkupBuilder xml, HashMap<String, Object> map) {
        if (map.containsKey("captureOutput") && map.get("captureOutput") == true) {
            xml.'capture-output'()
        }
    }

    def addOkOrError(MarkupBuilder xml, HashMap<String, Object> map, String node) {
        xml."$node"(to: map.get(node))
    }
}
