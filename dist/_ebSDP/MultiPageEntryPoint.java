/**
 * (C) Copyright 2010, 2011 upTick Pty Ltd
 *
 * Licensed under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation. You may obtain a copy of the
 * License at: http://www.gnu.org/copyleft/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package au.com.uptick.serendipity.client;

import au.com.uptick.serendipity.client.gin.SerendipityGinjector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

@SuppressWarnings("deprecation")
// @UrlPatternEntryPoint(value = "(HostFilename.html)?((&|\\\\?)locale=(en|de))?")
public abstract class MultiPageEntryPoint implements EntryPoint {

  interface GlobalResources extends ClientBundle {
    @NotStrict
    @Source("Serendipity.css")
    CssResource css();
  }

  private static SerendipityConstants constants;
  private static SerendipityMessages messages;

  private static final SerendipityGinjector ginjector = GWT.create(SerendipityGinjector.class);

  private long startTimeMillis;

  public void onModuleLoad() {

    // Defer all application initialisation code to onModuleLoad2() so that the
    // UncaughtExceptionHandler can catch any unexpected exceptions.
    Log.setUncaughtExceptionHandler();

    DeferredCommand.addCommand(new Command() {
      public void execute() {
        onModuleLoad2();
      }
    });
  }

  private void onModuleLoad2() {
    try {

      if (!Log.isLoggingEnabled()) {
        Window.alert("Logging is disabled.");
      }

      // Use a code guard e.g. "if (Log.isDebugEnabled() {...}"
      // to ensure unnecessary code is complied out when log_level=OFF
      // or any log_level higher than DEBUG
      if (Log.isDebugEnabled()) {
        startTimeMillis = System.currentTimeMillis();
      }

      // No code guard necessary as the code will be
      // complied out when log_level=OFF
      Log.debug("onModuleLoad2()");

      // inject global styles
      GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();

      // load constants
      constants = (SerendipityConstants) GWT.create(SerendipityConstants.class);

      // load messages
      messages = (SerendipityMessages) GWT.create(SerendipityMessages.class);

      // this is required by gwt-platform proxy's generator
      DelayedBindRegistry.bind(ginjector);

      // get Host Page name
      Dictionary dictionary = Dictionary.getDictionary("Pages");
      revealCurrentPlace(dictionary.get("page"));

      // hide the animated 'loading.gif'
      RootPanel.get("loading").setVisible(false);

      // Use a code guard e.g. "if (Log.isDebugEnabled() {...}"
      // to ensure unnecessary code is complied out when log_level=OFF
      // or any log_level higher than DEBUG
      if (Log.isDebugEnabled()) {
        long endTimeMillis = System.currentTimeMillis();
        float durationSeconds = (endTimeMillis - startTimeMillis) / 1000F;
        Log.debug("Duration: " + durationSeconds + " seconds");
      }

    } catch (Exception e) {
      Log.error("e: " + e);
      e.printStackTrace();

      Window.alert(e.getMessage());
    }
  }

  protected void revealCurrentPlace(String page) { }

  public static final String LOCAL_HOST = "http://127.0.0.1:8888/";
  // public static final String REMOTE_HOST = "http://crmdipity.com/";
  public static final String REMOTE_HOST = "http://127.0.0.1:8888/";

  public static String getRelativeURL(String url) {
    String realModuleBase;

    if (GWT.isScript()) {
      String moduleBase = GWT.getModuleBaseURL();

      Log.debug("GWT.isScript() is true - ModuleBaseURL: " + moduleBase);

      // Use for deployment to PRODUCTION server
      realModuleBase = REMOTE_HOST;

      // Use to test compiled browser locally
      if (moduleBase.indexOf("localhost") != -1) {
          realModuleBase = LOCAL_HOST;
      }
    } else {
      // This is the URL for GWT Hosted mode
      realModuleBase = LOCAL_HOST;
      
      Log.debug("GWT.isScript() is false - realModuleBase: " + realModuleBase);
    }

    return realModuleBase + url;
  }

  public static SerendipityConstants getConstants() {
    return constants;
  }

  public static SerendipityMessages getMessages() {
    return messages;
  }

  public static SerendipityGinjector getSerendipityGinjector() {
    return ginjector;
  }
}