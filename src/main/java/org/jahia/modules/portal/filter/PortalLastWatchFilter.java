package org.jahia.modules.portal.filter;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.utils.Patterns;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 24/01/14
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class PortalLastWatchFilter extends AbstractFilter {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PortalLastWatchFilter.class);

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        JCRNodeWrapper portalNode = JCRContentUtils.getParentOfType(resource.getNode(), PortalConstants.JMIX_PORTAL);
        DateTime currentDateTime = new DateTime();

        portalNode.setProperty(PortalConstants.J_LASTVIEWED, currentDateTime.toCalendar(Locale.ENGLISH));
        portalNode.saveSession();

        return null;
    }

    public static class WatchedTodayCondition implements ExecutionCondition {
        public WatchedTodayCondition() {
            super();
        }

        public boolean matches(RenderContext renderContext, Resource resource) {
            boolean matches = false;

            JCRNodeWrapper portalNode = JCRContentUtils.getParentOfType(resource.getNode(), PortalConstants.JMIX_PORTAL);
            DateTime currentDateTime = new DateTime();
            String lastViewed = portalNode.getPropertyAsString(PortalConstants.J_LASTVIEWED);

            boolean firstView = StringUtils.isEmpty(lastViewed);
            if (firstView || currentDateTime.getDayOfYear() != ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(lastViewed).getDayOfYear()) {
                matches = true;
            }

            return matches;
        }

        @Override
        public String toString() {
            return "have not already being watched today";
        }
    }

    public void setApplyOnFirstWatchInTheDay(boolean apply) {
        if(apply){
            AnyOfCondition condition = new AnyOfCondition();
            condition.add(new WatchedTodayCondition());
            addCondition(condition);
        }
    }
}
