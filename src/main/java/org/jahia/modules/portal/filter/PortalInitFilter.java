package org.jahia.modules.portal.filter;

import net.htmlparser.jericho.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.cache.AggregateCacheFilter;
import org.jahia.utils.ScriptEngineUtils;
import org.jahia.utils.WebUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 24/01/14
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class PortalInitFilter extends AbstractFilter{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PortalInitFilter.class);

    public static final String PORTAL_INIT_BASE_URL = "baseURL";
    public static final String PORTAL_INIT_DEBUG = "debug";
    public static final String PORTAL_INIT_IS_EDITABLE = "isEditable";
    public static final String PORTAL_INIT_IS_LOCKED = "isLocked";
    public static final String PORTAL_INIT_IS_MODEL = "isModel";
    public static final String PORTAL_INIT_FULL_TEMPLATE = "fullTemplate";
    public static final String PORTAL_INIT_PORTAL_PATH = "portalPath";
    public static final String PORTAL_INIT_PORTAL_IDENTIFIER = "portalIdentifier";
    public static final String PORTAL_INIT_PORTAL_TAB_PATH = "portalTabPath";
    public static final String PORTAL_INIT_PORTAL_TAB_IDENTIFIER = "portalTabIdentifier";
    public static final String PORTAL_INIT_JCR_REST_API_URI = "jcrRestAPIUri";
    public static final String PORTAL_INIT_LOCALE = "locale";

    private ScriptEngineUtils scriptEngineUtils;
    private String template;
    private String resolvedTemplate;
    private String jcrRestAPI;
    private Boolean debugEnabled;

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        JCRNodeWrapper portalNode = JCRContentUtils.getParentOfType(resource.getNode(), PortalConstants.JMIX_PORTAL);
        DateTime currentDateTime = new DateTime();
        String lastViewed = portalNode.getPropertyAsString(PortalConstants.J_LASTVIEWED);

        boolean firstView = StringUtils.isEmpty(lastViewed);
        if(firstView || currentDateTime.getDayOfYear() != ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(lastViewed).getDayOfYear()){
            portalNode.setProperty(PortalConstants.J_LASTVIEWED, currentDateTime.toCalendar(Locale.ENGLISH));
            portalNode.saveSession();
        }

        return null;
    }

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;

        // add inline init portal script
        String script = getResolvedTemplate();
        if (script != null) {
            Source source = new Source(out);
            OutputDocument outputDocument = new OutputDocument(source);
            List<Element> headElementList = source.getAllElements(HTMLElementName.HEAD);
            for (Element element : headElementList) {
                final EndTag headEndTag = element.getEndTag();
                String extension = StringUtils.substringAfterLast(template, ".");
                ScriptEngine scriptEngine = scriptEngineUtils.scriptEngine(extension);
                ScriptContext scriptContext = new PortalScriptContext();
                final Bindings bindings = scriptEngine.createBindings();

                // bindings
                bindings.put("options", getBindingMap(renderContext, resource));
                scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
                // The following binding is necessary for Javascript, which doesn't offer a console by default.
                bindings.put("out", new PrintWriter(scriptContext.getWriter()));
                scriptEngine.eval(script, scriptContext);
                StringWriter writer = (StringWriter) scriptContext.getWriter();
                final String portalScript = writer.toString();
                if (StringUtils.isNotBlank(portalScript)) {
                    outputDocument.replace(headEndTag.getBegin(), headEndTag.getBegin() + 1,
                            "\n" + AggregateCacheFilter.removeEsiTags(portalScript) + "\n<");
                }
                break; // avoid to loop if for any reasons multiple body in the page
            }
            out = outputDocument.toString().trim();
        }

        return out;
    }

    private HashMap<String, Object> getBindingMap(RenderContext renderContext, Resource resource) throws RepositoryException {
        JCRNodeWrapper portalNode = JCRContentUtils.getParentOfType(resource.getNode(), PortalConstants.JMIX_PORTAL);
        HashMap<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put(PORTAL_INIT_BASE_URL,
                stringifyJsParam(StringUtils.isNotEmpty(renderContext.getURLGenerator().getContext())
                        ? renderContext.getURLGenerator().getContext() + renderContext.getURLGenerator().getBaseLive() 
                        : renderContext.getURLGenerator().getBaseLive()));
        bindingMap.put(PORTAL_INIT_PORTAL_PATH, stringifyJsParam(portalNode.getPath()));
        bindingMap.put(PORTAL_INIT_PORTAL_TAB_PATH, stringifyJsParam(resource.getNode().getPath()));
        bindingMap.put(PORTAL_INIT_PORTAL_IDENTIFIER, stringifyJsParam(portalNode.getIdentifier()));
        bindingMap.put(PORTAL_INIT_PORTAL_TAB_IDENTIFIER, stringifyJsParam(resource.getNode().getIdentifier()));
        bindingMap.put(PORTAL_INIT_IS_EDITABLE, resource.getNode().hasPermission("jcr:write_live"));
        bindingMap.put(PORTAL_INIT_IS_LOCKED, portalNode.hasProperty("j:locked") && portalNode.getProperty("j:locked").getBoolean());
        bindingMap.put(PORTAL_INIT_IS_MODEL, portalNode.isNodeType(PortalConstants.JNT_PORTAL_MODEL));
        bindingMap.put(PORTAL_INIT_FULL_TEMPLATE, stringifyJsParam(portalNode.getProperty(PortalConstants.J_FULL_TEMPLATE).getString()));
        bindingMap.put(PORTAL_INIT_JCR_REST_API_URI, stringifyJsParam(jcrRestAPI));
        bindingMap.put(PORTAL_INIT_LOCALE, stringifyJsParam(resource.getLocale().toString()));
        bindingMap.put(PORTAL_INIT_DEBUG, debugEnabled);
        return bindingMap;
    }

    private String stringifyJsParam(String param) {
        return "'" + param + "'";    
    }

    protected String getResolvedTemplate() throws IOException {
        if (resolvedTemplate == null) {
            resolvedTemplate = WebUtils.getResourceAsString(template);
            if (resolvedTemplate == null) {
                logger.warn("Unable to lookup template at {}", template);
            }
        }
        return resolvedTemplate;
    }

    public void setScriptEngineUtils(ScriptEngineUtils scriptEngineUtils) {
        this.scriptEngineUtils = scriptEngineUtils;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setJcrRestAPI(String jcrRestAPI) {
        this.jcrRestAPI = jcrRestAPI;
    }

    public void setDebugEnabled(Boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    class PortalScriptContext extends SimpleScriptContext {
        private Writer writer = null;

        /**
         * {@inheritDoc}
         */
        @Override
        public Writer getWriter() {
            if (writer == null) {
                writer = new StringWriter();
            }
            return writer;
        }
    }
}
