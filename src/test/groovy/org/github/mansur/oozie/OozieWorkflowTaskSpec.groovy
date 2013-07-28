package org.github.mansur.oozie

import org.github.mansur.oozie.tasks.OozieWorkflowTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Muhammad Ashraf
 * @since 7/27/13
 */
class OozieWorkflowTaskSpec extends Specification {

    static final TASK_NAME = "oozieTask"
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Add oozie task"() {
        expect:
        project.tasks.findByName(TASK_NAME) == null

        when:
        project.task(TASK_NAME, type: OozieWorkflowTask) {
            workflowName = "some_oozie_flow"
            start = "start_node"
            end = "end_node"
            jobXML = [key: "value"]
            workflowActions = ["action1", "action2"]
            outputDir = new File("/tmp")
        }

        then:
        def task = project.tasks.findByName(TASK_NAME)
        task != null
        task.description == "Generates Ozzie workflow"
        task.workflowName == "some_oozie_flow"
        task.start == "start_node"
        task.end == "end_node"
        task.jobXML == [key: "value"]
        task.workflowActions == ["action1", "action2"]
        task.outputDir == new File("/tmp")
    }

}
