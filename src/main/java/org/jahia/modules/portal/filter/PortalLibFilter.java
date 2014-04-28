package org.jahia.modules.portal.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.service.bean.PortalContext;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.utils.ScriptEngineUtils;
import org.jahia.utils.WebUtils;
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
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 30/12/13
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class PortalLibFilter extends AbstractFilter {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PortalLibFilter.class);

    private static final String JS_API_FILE = "jahia-portal.js";

    private ScriptEngineUtils scriptEngineUtils;
    private String template;
    private String resolvedTemplate;
    private Boolean debugEnabled;

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;

        // add portal API lib
        String path = "/modules/" + renderContext.getMainResource().getNode().getPrimaryNodeType().getTemplatePackage().getBundle().getSymbolicName()
                + "/javascript/" + JS_API_FILE;
        path = StringUtils.isNotEmpty(renderContext.getRequest().getContextPath()) ? renderContext.getRequest().getContextPath() + path : path;
        String encodedPath = URLEncoder.encode(path, "UTF-8");
        out += ("<jahia:resource type='javascript' path='" + encodedPath + "' insert='true' resource='" + JS_API_FILE + "'/>");

        // add portal instance
        String script = getResolvedTemplate();
        if (script != null) {
            String extension = StringUtils.substringAfterLast(template, ".");
            ScriptEngine scriptEngine = scriptEngineUtils.scriptEngine(extension);
            ScriptContext scriptContext = new PortalScriptContext();
            final Bindings bindings = scriptEngine.createBindings();

            // bindings
            bindings.put("portalContext", serializePortal(renderContext));
            scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
            // The following binding is necessary for Javascript, which doesn't offer a console by default.
            bindings.put("out", new PrintWriter(scriptContext.getWriter()));
            scriptEngine.eval(script, scriptContext);
            StringWriter writer = (StringWriter) scriptContext.getWriter();
            final String portalScript = writer.toString();
            if (StringUtils.isNotBlank(portalScript)) {
                out += ("<jahia:resource type='inlinejavascript' path='" + URLEncoder.encode(portalScript, "UTF-8") + "' insert='false' resource='' title='' key=''/>");
            }
        }

        return out;
    }

    private String serializePortal(RenderContext renderContext) throws RepositoryException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PortalContext portal = (PortalContext) renderContext.getRequest().getAttribute("portalContext");
        portal.setDebug(debugEnabled);

        return objectMapper.writeValueAsString(portal);
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
