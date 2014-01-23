package org.jahia.modules.portal.sitesettings.form.constraint;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 30/12/13
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */

public class PortalTemplatesValidator implements ConstraintValidator<PortalTemplates, String> {
    private static final Logger logger = LoggerFactory.getLogger(PortalTemplatesValidator.class);


    private PortalService portalService;

    @Override
    public void initialize(PortalTemplates portalTemplates) {
        //TODO Autowired not working in constraint validator, need to investigate why.
        portalService = (PortalService) SpringContextSingleton.getBean("portalService");
    }

    @Override
    public boolean isValid(String portalRootTemplate, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtils.isEmpty(portalRootTemplate))
            return true;

        try {
            JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentUserSession();
            List<JCRNodeWrapper> portalTabTemplates = portalService.getPortalTabTemplates(portalRootTemplate, session);
            if(portalTabTemplates.size() > 0){
                return true;
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }
}
