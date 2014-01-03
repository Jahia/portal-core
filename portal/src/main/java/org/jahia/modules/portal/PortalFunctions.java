package org.jahia.modules.portal;

import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.utils.i18n.Messages;

import javax.jcr.nodetype.NodeTypeIterator;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 02/01/14
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class PortalFunctions {
    public static NodeTypeIterator getNodeTypes(){
        return NodeTypeRegistry.getInstance().getAllNodeTypes();
    }

    public static String getI18nNodetype(JahiaTemplatesPackage pkg, String key, Locale locale){
        return Messages.get(pkg, key, locale);
    }

}
