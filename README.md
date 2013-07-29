# Introduction
gradle-oozie-plugin provides a simple Groovy DSL for [Apache oozie workflow](http://oozie.apache.org/) because creating
flows in XML causes serious brian damage!

# Installation
```
maven repo: http://repository-uncommon-configuration.forge.cloudbees.com/release/
groupId: org.github.mansur.oozie
artifactId: gradle-oozie-plugin
version: 0.1
```
# Supported workflow actions
Following workflow actions are supported 

1. [java](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.2.7_Java_Action)
2. [mapreduce](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.2.2_Map-Reduce_Action)
3. [pig](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.2.3_Pig_Action)
4. [ssh](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.2.5_Ssh_Action)
5. [fs](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.2.4_Fs_HDFS_action)
6. [shell](http://oozie.apache.org/docs/3.3.0/DG_ShellActionExtension.html)
    
In addition following decision nodes are also supported

 1. [start](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.1.1_Start_Control_Node)
 2. [end](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.1.2_End_Control_Node)
 3. [kill](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.1.3_Kill_Control_Node)
 4. [decision](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.1.4_Decision_Control_Node)
 5. [frok and join](http://oozie.apache.org/docs/3.3.0/WorkflowFunctionalSpec.html#a3.1.5_Fork_and_Join_Control_Nodes)
    
# Useage

```grrovy

buildscript {
    repositories {
        maven {
            url "http://repository-uncommon-configuration.forge.cloudbees.com/release/"
        }
        mavenCentral()
    }

    dependencies {
        classpath 'org.github.mansur.oozie:gradle-oozie-plugin:0.1'

    }

}
apply plugin: 'oozie'

```


This is how a mapreduce flow looks like in XML

```
<action name='first_map_reduce'>
    <map-reduce>
      <job-tracker>http://jobtracker</job-tracker>
      <name-node>http://namenode</name-node>
      <prepare>
        <delete path='http://jobtracker/pattern' />
      </prepare>
      <job-xml>job.xml</job-xml>
      <configuration>
        <property>
          <name>mapred.map.output.compress</name>
          <value>false</value>
        </property>
        <property>
          <name>mapred.job.queue.name</name>
          <value>queuename</value>
        </property>
      </configuration>
    </map-reduce>
    <ok to='end' />
    <error to='fail' />
  </action>
```

and this is the same flow in groovy dsl

```
oozie {
first_map_reduce = [
            name: "first_map_reduce",
            type: "mapreduce",
            delete: ["${jobTracker}/pattern"],
            mainClass: "some.random.class",
            jobXML: "job.xml",
            ok: "end",
            error: "fail",
            configuration: [
                    "mapred.map.output.compress": "false",
                    "mapred.job.queue.name": "queuename"
            ]
    ]
}
```

see [build.gradle](https://github.com/MuhammadAshraf/gradle-oozie-plugin/blob/master/example/build.gradle) for a complete workflow

    
