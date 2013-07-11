package hudson.plugins.concurrent_login;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Hashtable;
import java.util.Enumeration;

public class SessionManager implements HttpSessionBindingListener {
	private static SessionManager sessionManager = null;
	private static Hashtable sessionUsers = new Hashtable();
	private static String sess_id = null;

	private SessionManager() {
		super();
	}

	public static synchronized SessionManager getInstance() {
		if (sessionManager == null) {
			sessionManager = new SessionManager();
		}
		return sessionManager;
	}

	public boolean isValid(String userID) {
		return true;
	}

	public boolean isLogin(String sessionID) {
		boolean isLogin = false;
		Enumeration e = sessionUsers.keys();
		String key = "";
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			if (sessionID.equals(key)) {
				isLogin = true;
			}
		}
		return isLogin;
	}

	public boolean isUsing(String userID) {
		boolean isUsing = false;
		Enumeration e = sessionUsers.keys();
		String key = "";
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			if (userID.equals(sessionUsers.get(key))) {
				isUsing = true;
			}
		}
		return isUsing;
	}

	public void setSession(HttpSession session, String userID) {
		sess_id = session.getId();
		sessionUsers.put(session.getId(), userID);
		session.setAttribute("customsession", this.getInstance());
		session.setMaxInactiveInterval(60); // unit: second
	}

	public void deleteSession(HttpSession session) {
		session.removeAttribute("customsession");
		// sessionUsers.remove(sess_id);
	}

	public void valueBound(HttpSessionBindingEvent event) {
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		sessionUsers.remove(event.getSession().getId());
	}

	public String getUserID(String sessionID) {
		return (String) sessionUsers.get(sessionID);
	}

	public int getUserCount() {
		return sessionUsers.size();
	}

	public String getID() {
		return sess_id;
	}
};