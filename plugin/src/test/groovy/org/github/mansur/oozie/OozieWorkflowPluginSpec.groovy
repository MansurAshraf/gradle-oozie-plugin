package org.github.mansur.oozie

import org.github.mansur.oozie.plugin.OozieWorkflowPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Muhammad Ashraf
 * @since 7/27/13
 */
class OozieWorkflowPluginSpec extends Specification {

    static final String EXTENSION_NAME = 'oozie'
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Apply oozie plugin"() {
        expect:
        project.tasks.findByName(OozieWorkflowPlugin.TASK_NAME) == null

        when:
        project.apply plugin: 'oozie'

        project.oozie {
            def jobTracker = "http://jobtracker"
            def namenode = "http://namenode"

            def common_props = [
                    jobTracker: "$jobTracker",
                    namenode: "$namenode",
                    jobXML: "dev_prop.xml"
            ]

            def shell_to_prod = [
                    name: "shell_to_prod",
                    type: 'shell',
                    ok: "fork_flow",
                    error: "fail",
                    delete: ["/tmp/workDir"],
                    mkdir: ["/tmp/workDir"],
                    exec: "ssh test@localhost",
                    captureOutput: true,
                    configuration: [
                            "mapred.map.output.compress": "false",
                            "mapred.job.queue.name": "queuename"
                    ],
                    args: [
                            "input",
                            "output",
                            "cache.txt"
                    ],
                    envVar: [
                            "java_home"
                    ],
                    file: [
                            "file1.txt",
                            "file2.txt"
                    ],
                    archive: [
                            "job.tar"
                    ]
            ]

            def move_files = [
                    name: "move_files",
                    type: 'fs',
                    ok: "join_flow",
                    error: "fail",
                    delete: ["hdfs://foo:9000/usr/tucu/temp-data"],
                    mkdir: ['archives/${wf:id()}'],
                    move: [
                            [source: '${jobInput}', target: 'archives/${wf:id()}/processed-input'],
                            [source: '${jobInput}', target: 'archives/${wf:id()}/raw-input']

                    ],
                    chmod: [
                            [path: '${jobOutput}', permissions: '-rwxrw-rw-', dir_files: 'true']
                    ]
            ]

            def mahout_pfpgrowth = [
                    name: "mahout_fp_growth",
                    type: "java",
                    delete: ["${jobTracker}/pattern"],
                    mainClass: "some.random.class",
                    jobXML: "job.xml",
                    ok: "join_flow",
                    error: "fail",
                    configuration: [
                            "mapred.map.output.compress": "false",
                            "mapred.job.queue.name": "queuename"
                    ],
                    args: [
                            "--input",
                            "/cart",
                            "--output",
                            "--maxheapSize",
                            "50"
                    ]
            ]

            def fork_flow = [
                    name: "fork_flow",
                    type: "fork",
                    paths: [
                            "move_files",
                            "mahout_fp_growth"
                    ]
            ]

            def join_flow = [
                    name: "join_flow",
                    type: "join",
                    to: "pig_job"
            ]

            def pig_job = [
                    name: "pig_job",
                    type: "pig",
                    delete: ["${jobTracker}/pattern"],
                    mainClass: "some.random.class",
                    jobXML: "job.xml",
                    ok: "flow_decision",
                    error: "fail",
                    configuration: [
                            "mapred.map.output.compress": "false",
                            "mapred.job.queue.name": "queuename"
                    ],
                    script: "first.pig",
                    params: [
                            "--input",
                            "/cart",
                            "--output",
                            "--maxheapSize",
                            "50"
                    ]
            ]

            def first_map_reduce = [
                    name: "first_map_reduce",
                    type: "mapreduce",
                    delete: ["${jobTracker}/pattern"],
                    jobXML: "job.xml",
                    ok: "end",
                    error: "fail",
                    configuration: [
                            "mapred.map.output.compress": "false",
                            "mapred.job.queue.name": "queuename"
                    ]
            ]

            def flow_decision = [
                    name: "flow_decision",
                    type: "decision",
                    switch: [
                            [to: "end", if: "some condition"],
                            [to: "first_map_reduce", if: "some other condition"]
                    ],
                    default: "end"
            ]

            def fail = [
                    name: "fail",
                    type: "kill",
                    message: "workflow failed!"
            ]


            actions = [
                    shell_to_prod,
                    move_files,
                    mahout_pfpgrowth,
                    fork_flow,
                    join_flow,
                    pig_job,
                    first_map_reduce,
                    flow_decision,
                    fail]

            common = common_props
            start = "start_node"
            end = "end_node"
            name = 'oozie_flow'
            namespace = 'uri:oozie:workflow:0.1'
        }


        then:
        project.extensions.findByName(EXTENSION_NAME) != null
        Task task = project.tasks.findByName(OozieWorkflowPlugin.TASK_NAME)
        task.start == "start_node"
        task.end == "end_node"
        task.workflowName == 'oozie_flow'
        task.namespace == 'uri:oozie:workflow:0.1'
        task.common.size() == 3
        task.workflowActions.size() == 9

        and:
        task.start()
        def xml=new File("$project.buildDir/oozie_flow.xml")
        xml.exists()

    }
}
