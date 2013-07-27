package org.github.mansur.oozie

/**
 * @author Muhammad Ashraf
 * @since 7/27/13
 */
class SAMPLE_XML {
    def static EXPECTED_FLOW = """
<workflow-app xmlns='xmlns=uri:oozie:workflow:0.1' name='oozie_flow'>
  <start to='start_node' />
  <action name='shell_to_prod'>
    <shell xmlns='uri:oozie:shell-action:0.1'>
      <job-tracker>http://jobtracker</job-tracker>
      <name-node>http://namenode</name-node>
      <prepare>
        <delete path='/tmp/workDir' />
        <mkdir path='/tmp/workDir' />
      </prepare>
      <job-xml>dev_prop.xml</job-xml>
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
      <exec>ssh test@localhost</exec>
      <argument>input</argument>
      <argument>output</argument>
      <argument>cache.txt</argument>
      <env-var>java_home</env-var>
      <file>file1.txt</file>
      <file>file2.txt</file>
      <archive>job.tar</archive>
      <capture-output />
    </shell>
    <ok to='fork_flow' />
    <error to='fail' />
  </action>
  <fork name='fork_flow'>
    <path start='move_files' />
    <path start='mahout_fp_growth' />
  </fork>
  <action name='move_files'>
    <fs>
      <delete path='hdfs://foo:9000/usr/tucu/temp-data' />
      <mkdir path='archives/\${wf:id()}' />
      <move source='\${jobInput}' target='archives/\${wf:id()}/processed-input' />
      <move source='\${jobInput}' target='archives/\${wf:id()}/raw-input' />
      <chmod path='\${jobOutput}' permissions='-rwxrw-rw-' dir-files='true' />
    </fs>
    <ok to='join_flow' />
    <error to='fail' />
  </action>
  <action name='mahout_fp_growth'>
    <java>
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
      <main-class>some.random.class</main-class>
      <arg>--input</arg>
      <arg>/cart</arg>
      <arg>--output</arg>
      <arg>--maxheapSize</arg>
      <arg>50</arg>
    </java>
    <ok to='join_flow' />
    <error to='fail' />
  </action>
  <join name='join_flow' to='pig_job' />
  <action name='pig_job'>
    <pig>
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
      <script>first.pig</script>
      <param>--input</param>
      <param>/cart</param>
      <param>--output</param>
      <param>--maxheapSize</param>
      <param>50</param>
    </pig>
    <ok to='flow_decision' />
    <error to='fail' />
  </action>
  <decision name='flow_decision'>
    <switch>
      <case to='end'>some condition</case>
      <case to='first_map_reduce'>some other condition</case>
      <default to='end' />
    </switch>
  </decision>
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
  <kill name='fail'>
    <message>workflow failed!</message>
  </kill>
  <end name='end_node' />
</workflow-app>
"""
}
