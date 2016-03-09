package jrds.webapp;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.*;

import jrds.Configuration;
import jrds.HostsList;
import jrds.PropertiesManager;

public abstract class JrdsServlet extends HttpServlet {
    static final private Logger logger = LogManager.getLogger(JrdsServlet.class);

    protected HostsList getHostsList() {
        return Configuration.get().getHostsList();
    }

    protected PropertiesManager getPropertiesManager() {
        return Configuration.get().getPropertiesManager();
    }

    protected ParamsBean getParamsBean(HttpServletRequest request, String... restPath) {
        return new ParamsBean(request, getHostsList(), restPath);
    }

    protected boolean allowed(ParamsBean params, Set<String> roles) {
        if(getPropertiesManager().security) {
            if(roles.contains("ANONYMOUS"))
                return true;
            if(logger.isTraceEnabled()) {
                logger.trace("Checking if roles " + params.getRoles() + " in roles " + roles);
                logger.trace("Disjoint: " +  Collections.disjoint(roles, params.getRoles()));
            }
            return ! Collections.disjoint(roles, params.getRoles());
        }
        return true;
    }

    protected boolean allowed(ParamsBean params, ACL acl, HttpServletRequest req, HttpServletResponse res) {
        if(getPropertiesManager().security) {				
            boolean allowed = acl.check(params);
            logger.trace(jrds.Util.delayedFormatString("Looking if ACL %s allow access to %s", acl, req.getServletPath()));
            if(! allowed) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        return true;
    }

}
