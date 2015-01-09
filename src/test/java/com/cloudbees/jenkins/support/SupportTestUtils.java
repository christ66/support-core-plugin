package com.cloudbees.jenkins.support;

import com.cloudbees.jenkins.support.api.Container;
import com.cloudbees.jenkins.support.api.Content;
import edu.umd.cs.findbugs.annotations.CheckForNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: schristou88
 * Date: 1/8/15
 * Time: 4:35 PM
 */
public class SupportTestUtils {

  /**
   * Create a container from an output stream. Test units can use this to generate a  
   */
  public static Container createContainerFromOutputStream(final OutputStream os) {
    return new Container() {
      @Override
      public void add(@CheckForNull Content content) {
        try {
          content.writeTo(os);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
  }
}