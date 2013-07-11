package hudson.plugins.concurrent_login;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jenkins.model.Jenkins;

import hudson.Extension;
import hudson.model.PageDecorator;
import hudson.model.Descriptor.FormException;

import net.sf.json.JSONObject;

import org.acegisecurity.Authentication;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

@Extension
public class UserInfoDecorator extends PageDecorator {
	/**
	 * To persist global configuration information, simply store it in a field
	 * and call save().
	 * 
	 * <p>
	 * If you don't want fields to be persisted, use <tt>transient</tt>.
	 */
	private boolean useInterceptConcurrentLogin;

	private String sess_id;
	private String alreadyLoginID;

	final Logger logger = Logger.getLogger("hudson.plugins.concurrent_login");

	public UserInfoDecorator() {
		logger.log(Level.INFO, "Loading UserInfoDecorator...");
		load();
	}

	/**
	 * Used by <tt>footer.jelly</tt> to control session behavior.
	 * 
	 * @param HttpServletRequest
	 *            request
	 * @param HttpServletResponse
	 *            response
	 * @param String
	 *            loginUserID
	 * 
	 */
	public void requestInfo(HttpServletRequest request,
			HttpServletResponse response, String loginUserID) {
		if (getuseInterceptConcurrentLogin()) {
			alreadyLoginID = null;

			HttpSession session = request.getSession(false);
			sess_id = session.getId();

			SessionManager sessionManager = SessionManager.getInstance();

			if (!loginUserID.equals("anonymous")) {
				// logger.log(Level.INFO, "******** sess_id ... " + sess_id +
				// " / isLogin ... " + sessionManager.isLogin(sess_id));
				if (!sessionManager.isLogin(sess_id)) {
					// Note: Session Check: Jenkins(O), Custom(X)
					if (!sessionManager.isUsing(loginUserID)) {
						sessionManager.setSession(session, loginUserID);
						// logger.log(Level.INFO, "******** setSession ... " +
						// loginUserID + " / getID ... " +
						// sessionManager.getID());
					} else {
						alreadyLoginID = loginUserID;
						// logger.log(Level.INFO,
						// "******** Already using ID ... " + loginUserID);
						try {
							response.sendRedirect("/jenkins/logout");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public String getAlreadyLoginCheck() {
		return alreadyLoginID;
	}

	public String getSessId() {
		return sess_id;
	}

	@Exported
	public String getName() {
		return auth().getName();
	}

	private Authentication auth() {
		return Jenkins.getAuthentication();
	}

	@Override
	public boolean configure(StaplerRequest req, JSONObject formData)
			throws FormException {
		// To persist global configuration information,
		// set that to properties and call save().
		useInterceptConcurrentLogin = formData
				.getBoolean("useInterceptConcurrentLogin");
		// ^Can also use req.bindJSON(this, formData);
		// (easier when there are many fields; need set* methods for this, like
		// setuseApply)
		save();
		return super.configure(req, formData);
	}

	/**
	 * This method returns true if the global configuration says we should use
	 * concurrent login
	 * 
	 * The method name is bit awkward because global.jelly calls this method to
	 * determine the initial state of the checkbox by the naming convention.
	 */
	public boolean getuseInterceptConcurrentLogin() {
		return useInterceptConcurrentLogin;
	}
}
