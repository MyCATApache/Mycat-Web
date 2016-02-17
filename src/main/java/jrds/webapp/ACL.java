package jrds.webapp;

import org.apache.logging.log4j.*;

public abstract class ACL {
	static final protected Logger logger = LogManager.getLogger(ACL.class);

	static final public ACL ALLOWEDACL = new ACL(){
		public boolean check(ParamsBean params) {
			return true;
		}
		@Override
		public ACL join(ACL acl) {
			return acl;
		}
		@Override
		public String toString() {
			return "All allowed";
		}
	};

	static final public class AdminACL extends ACL {
		final private String adminRole;
		
		public AdminACL(String adminRole) {
			this.adminRole = adminRole;
		}
		public String getAdminRole() {
			return adminRole;
		}
		public boolean check(ParamsBean params) {
			return params.getRoles().contains(adminRole);
		}
		@Override
		public ACL join(ACL acl) {
			return acl.join(this);
		}
		@Override
		public String toString() {
			return "admin role: \"" + adminRole + "\"";
		}
	};

	public abstract boolean check(ParamsBean params);	
	public abstract ACL join(ACL acl);
}
