package org.github.mansur.oozie.tasks

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
        wf.name = getWorkflowName()
        wf.start = getStart()
        wf.end = getEnd()
        wf.namespace = getNamespace()
        wf.jobXML = this.getJobXML()
        wf.actions = getWorkflowActions()
        wf.common = getCommon()
        def builder = new WorkFlowBuilder()
        generateFlow(builder, wf)
        generateJobXML(builder, wf)

    }

    private void generateJobXML(WorkFlowBuilder builder, Workflow wf) {
        def jobXML = builder.buildJobXML(wf.jobXML)
        if (jobXML != null) {
            def outputFile = new File(getOutputDir().absolutePath + File.separator + getWorkflowName() + "-config.xml")
            outputFile.parentFile.mkdirs()
            println("generating oozie job xml : file://$outputFile")
            outputFile.write(jobXML)
        }
    }

    private void generateFlow(WorkFlowBuilder builder, Workflow wf) {
        String xml

        try {
            xml = builder.buildWorkflow(wf)
        } catch (Exception e) {
            throw new GradleException(e.message, e)
        }

        def outputFile = new File(getOutputDir().absolutePath + File.separator + getWorkflowName() + ".xml")
        outputFile.parentFile.mkdirs()
        println("generating oozie workflow: file://$outputFile")
        outputFile.write(xml)
    }
}
