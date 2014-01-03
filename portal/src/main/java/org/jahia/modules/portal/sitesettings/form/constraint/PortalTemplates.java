package org.jahia.modules.portal.sitesettings.form.constraint;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 30/12/13
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PortalTemplatesValidator.class)
public @interface PortalTemplates {
    String message() default "{portal.templates.constraint}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
