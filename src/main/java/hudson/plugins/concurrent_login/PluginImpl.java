package hudson.plugins.concurrent_login;

import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.XmlFile;
import hudson.model.User;
import hudson.util.FormValidation;
import hudson.util.MultipartFormDataParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Entry point of a plugin.
 * 
 * <p>
 * There must be one {@link Plugin} class in each plugin. See javadoc of
 * {@link Plugin} for more about what can be done on this class.
 * 
 * @author Kohsuke Kawaguchi
 */
public class PluginImpl extends Plugin {

  transient PluginWrapper wrapper;

  transient final Logger logger = Logger.getLogger("com.skcomms.jenkins");

  @Override
  public void start() throws Exception {
    super.start();
    
    load();
    //userInfo();
    
    logger.log(Level.INFO, "Start PluginImpl class");
  }
}