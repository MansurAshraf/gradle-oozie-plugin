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

import com.google.common.base.Preconditions
import org.github.mansur.oozie.beans.Workflow
import groovy.xml.MarkupBuilder

/**
 * @author Muhammad Ashraf
 * @since 7/24/13
 */
class WorkFlowBuilder {
    HashMap<String, Object> registry = [
            "java": new JavaBuilder(),
            "fs": new FSBuilder(),
            "ssh": new SSHBuilder(),
            "mapreduce": new MapReduceBuilder(),
            "shell": new ShellBuilder(),
    ]

    def String buildWorkflow(Workflow wf) {
        def actions = wf.actions
        def graph = createDAG(actions)
        def writer = new StringWriter()
        def workflow = new MarkupBuilder(writer)
        workflow.'workflow-app'('xmlns': "xmlns=$wf.namespace", name: "$wf.name") {
            graph.each {
                def action = findAction(it.toString(), actions)
                def type = action.get("type")
                def builder = findBuilder(type)
                builder.buildXML(workflow, action, wf.common)
            }
        }
        writer.toString()
    }

    /**
     * Creates Direct Acyclic Graph of all the actions
     * @param actions
     * @return
     */
    private List<DirectedGraph.Node> createDAG(List<HashMap<String, Object>> actions) {
        def graph = new DirectedGraph();
        HashMap<String, DirectedGraph.Node> nodesMap = getNodeMap(actions)
        def nodes = nodesMap.values()
        nodes.each {
            def nodeName = it.toString()
            def action = findAction(nodeName, actions)
            def toNodeName = action.get("ok")
            def toNode = nodesMap.get(toNodeName)
            if (toNode != null) {
                it.addEdge(toNode)
            }
            graph.addNode(it)
        }
        graph.sort()
    }

    private HashMap<String, Object> findAction(String name, List<HashMap<String, Object>> actions) {
        actions.find { name == it.get("name") }
    }

    def Object findBuilder(String type) {
        def builder = registry.get(type)
        if (builder == null) {
            throw new IllegalArgumentException(String.format("Invalid action type %s, supported action types are %s", type, registry.keySet().toString()))
        }
        builder
    }

    private HashMap<String, DirectedGraph.Node> getNodeMap(List<HashMap<String, Object>> actions) {
        def nodesMap = new HashMap<String, DirectedGraph.Node>()
        actions.each {
            String name = it.get("name")
            Preconditions.checkArgument(name != null && name.length() > 1, "Found action without a name!")
            def node = new DirectedGraph.Node(name)
            nodesMap.put(name, node)
        }
        nodesMap
    }
}
