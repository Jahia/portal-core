package org.jahia.modules.portal.sitesettings.form.constraint;

import java.util.List;
import javax.jcr.RepositoryException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kevan on 03/02/14.
 */
public class PortalTemplateValidator implements ConstraintValidator<PortalTemplate, String> {
	private static final Logger logger = LoggerFactory.getLogger(PortalTemplateValidator.class);

	private PortalService portalService;

	@Override
	public void initialize(PortalTemplate portalTemplate) {
		//TODO Autowired not working in constraint validator, need to investigate why.
		portalService = (PortalService) SpringContextSingleton.getBean("portalService");
	}

	@Override
	public boolean isValid(String portalTemplatePath, ConstraintValidatorContext constraintValidatorContext) {
		if(StringUtils.isEmpty(portalTemplatePath))
			return true;

		try {
			JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentUserSession();
			JCRNodeWrapper portalTabTemplate = portalService.getPortalTabTemplate(portalTemplatePath, session);
			if(portalTabTemplate != null){
				return true;
			}
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
}
