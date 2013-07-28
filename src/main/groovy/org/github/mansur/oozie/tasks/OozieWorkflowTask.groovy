package org.github.mansur.oozie.tasks

import org.apache.commons.io.FileUtils
import org.github.mansur.oozie.beans.Workflow
import org.github.mansur.oozie.builders.WorkFlowBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @author Muhammad Ashraf
 * @since 7/27/13
 */
class OozieWorkflowTask extends DefaultTask {

    @Input String workflowName
    @Input String start
    @Input String end
    @Input String namespace = 'uri:oozie:workflow:0.1'
    @Input HashMap<String, Object> common
    @Input HashMap<String, Object> jobXML
    @Input List<HashMap<String, Object>> workflowActions
    @Input File outputDir = project.buildDir

    OozieWorkflowTask() {
        description = "Generates Ozzie workflow"
        group = "Oozie"
    }

    @TaskAction
    void start() {
        generateWorkflow()
    }

    private void generateWorkflow() {
        def wf = new Workflow()
        wf.name = workflowName
        wf.start = start
        wf.end = end
        wf.namespace = namespace
        wf.jobXML = jobXML
        wf.actions = workflowActions
        String xml
        def builder = new WorkFlowBuilder()
        try {
            xml = builder.buildWorkflow(wf)
        } catch (Exception e) {
            throw new GradleException(e.message)
        }
        FileUtils.forceMkdir(outputDir)
        def outputFile = new File(outputDir.absolutePath + File.separator + workflowName + ".xml")
        FileUtils.writeStringToFile(outputFile, xml)
    }
}
