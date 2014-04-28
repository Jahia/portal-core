package org.jahia.modules.portal.tags;

import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRNodeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 02/01/14
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PortalFunctions {
    private static final Logger logger = LoggerFactory.getLogger(PortalFunctions.class);

    private static PortalService portalService;

    @Autowired(required = true)
    public void setPortalService(PortalService portalService) {
        PortalFunctions.portalService = portalService;
    }

    public static Set<JCRNodeWrapper> getUserPortalsBySite(String siteKey, Locale locale) {
        return portalService.getUserPortalsBySite(siteKey, locale);
    }
}