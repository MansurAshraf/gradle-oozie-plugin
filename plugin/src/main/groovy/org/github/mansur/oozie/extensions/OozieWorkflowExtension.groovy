package org.github.mansur.oozie.extensions

/**
 * @author Muhammad Ashraf
 * @since 7/27/13
 */
class OozieWorkflowExtension {
    String name
    String start
    String end
    String namespace
    HashMap<String, Object> common
    HashMap<String, Object> jobXML
    List<HashMap<String, Object>> actions
    File outputDir
}
