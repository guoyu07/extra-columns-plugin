package jenkins.plugins.extracolumns;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.util.LogTaskListener;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvironmentVariableColumn extends ListViewColumn {

  private static final Logger LOGGER = Logger.getLogger(EnvironmentVariableColumn.class.getCanonicalName());

  private final boolean showSingleVariable;
  private final String variableName;

  @DataBoundConstructor
  public EnvironmentVariableColumn(boolean showSingleVariable, String variableName) {
    super();
    this.showSingleVariable = showSingleVariable;
    this.variableName = variableName;
  }

  public EnvironmentVariableColumn() {
    this(false, null);
  }

  public boolean isShowSingleVariable() {
    return showSingleVariable;
  }

  public String getVariableName() {
    return variableName;
  }

  public String getHeader() {
    return showSingleVariable ? variableName : "Environment Variables";
  }

  public String getEnvironmentVariables(Job job) {
    if (job == null || job.getLastBuild() == null) {
      return "";
    }

    try {
      Run r = job.getLastBuild();
      EnvVars vars = r.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));
      if (isShowSingleVariable()) {
        return vars.get(getVariableName());
      } else {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : vars.entrySet()) {
          sb.append(entry.getKey()).append("=").append(entry.getValue()).append("<br />");
        }
        return sb.toString();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "";
  }

  @Extension
  public static class DescriptorImpl extends ListViewColumnDescriptor {
    @Override
    public boolean shownByDefault() {
      return false;
    }

    @Override
    public String getDisplayName() {
      return Messages.EnvironmentVariablesColumn_DisplayName();
    }
  }
}
