package org.jahia.modules.portal.sitesettings.form.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Created by kevan on 03/02/14.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PortalTemplateValidator.class)
public @interface PortalTemplate {
	String message() default "{portal.template.constraint}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
