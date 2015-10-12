package jrds.webapp.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Role{	
	public enum RoleList{ USER, OPERATOR, ADMIN}
	public RoleList value();
}
