package com.cloudbees.jenkins.support.impl;

import com.cloudbees.jenkins.support.SupportTestUtils;
import hudson.slaves.DumbSlave;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class DumpExportTableTest {
  @Rule public JenkinsRule j = new JenkinsRule();
  
  @Test
  public void testAddContents() throws Exception {
    // Given
    DumbSlave onlineSlave = j.createOnlineSlave();

    // When
    String dumpTableString = SupportTestUtils.invokeComponentToString(new DumpExportTable());

    // Then
    assertFalse("Should have dumped the export table.",
            dumpTableString.isEmpty());

    List<String> output = new ArrayList<String>(Arrays.asList(dumpTableString.split("\n")));
    assertThat(output, hasItems(containsString("hudson.remoting.ExportTable")));
  }
}