package com.cloudbees.jenkins.support.impl;

import com.cloudbees.jenkins.support.SupportTestUtils;
import hudson.model.Slave;
import hudson.slaves.DumbSlave;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

public class DumpExportTableTest {
  @Rule public JenkinsRule j = new JenkinsRule();
  
  @Test
  public void testAddContents() throws Exception {
    // Given
    DumbSlave onlineSlave = j.createOnlineSlave();
    DumpExportTable dumpExportTable = new DumpExportTable();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    // When
    dumpExportTable.addContents(SupportTestUtils.createContainerFromOutputStream(baos));
    
    // Then
    assertFalse("Should have dumped the export table.",
            baos.toString().isEmpty());

    List<String> output = new ArrayList<String>(Arrays.asList(baos.toString().split("\n")));
    assertThat(output, hasItems(containsString("hudson.remoting.ExportTable")));
  }
}