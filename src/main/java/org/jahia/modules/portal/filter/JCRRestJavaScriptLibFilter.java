/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2019 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.portal.filter;

import org.apache.commons.lang.StringUtils;
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
import java.lang.Boolean;import java.lang.Exception;import java.lang.Object;import java.lang.Override;import java.lang.String;import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by kevan on 28/03/14.
 */
public class JCRRestJavaScriptLibFilter extends AbstractFilter{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(JCRRestJavaScriptLibFilter.class);

    public static final String JCR_REST_UTIL_INIT_DEBUG = "debug";
    public static final String JCR_REST_UTIL_INIT_API_BASE = "jcrRestAPIBase";
    public static final String JCR_REST_UTIL_INIT_API_VERSION = "jcrRestAPIVersion";
    public static final String JCR_REST_UTIL_INIT_API_CURRENT_LOCALE = "currentLocale";
    public static final String JCR_REST_UTIL_INIT_API_CURRENT_WORKSPACE = "currentWorkspace";
    public static final String JCR_REST_UTIL_INIT_API_CURRENT_RESOURCE_PATH = "currentRessourcePath";
    public static final String JCR_REST_UTIL_INIT_API_CURRENT_RESOURCE_IDENTIFIER = "currentRessourceIdentifier";

    private static final String JCR_REST_SCRIPT_TEMPLATE = "jcrRestUtilsInit.groovy";
    private static final String JCR_REST_JS_FILE = "JCRRestUtils.js";

    private ScriptEngineUtils scriptEngineUtils;
    private String resolvedTemplate;
    private String jcrRestAPIVersion;
    private Boolean debugEnabled;

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;
        String context = StringUtils.isNotEmpty(renderContext.getRequest().getContextPath()) ? renderContext.getRequest().getContextPath() : "";

        // add lib
        String path = context + "/modules/" + renderContext.getMainResource().getNode().getPrimaryNodeType().getTemplatePackage().getBundle().getSymbolicName()
                + "/javascript/" + JCR_REST_JS_FILE;
        String encodedPath = URLEncoder.encode(path, "UTF-8");
        out += ("<jahia:resource type='javascript' path='" + encodedPath + "' insert='true' resource='" + JCR_REST_JS_FILE + "'/>");

        // instance JavaScript object
        String script = getResolvedTemplate();
        if (script != null) {
            String extension = StringUtils.substringAfterLast(JCR_REST_SCRIPT_TEMPLATE, ".");
            ScriptEngine scriptEngine = scriptEngineUtils.scriptEngine(extension);
            ScriptContext scriptContext = new JCRRestUtilsScriptContext();
            final Bindings bindings = scriptEngine.createBindings();

            // bindings
            bindings.put("options", getBindingMap(renderContext, resource, context));
            scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
            // The following binding is necessary for Javascript, which doesn't offer a console by default.
            bindings.put("out", new PrintWriter(scriptContext.getWriter()));
            scriptEngine.eval(script, scriptContext);
            StringWriter writer = (StringWriter) scriptContext.getWriter();
            final String resultScript = writer.toString();
            if (StringUtils.isNotBlank(resultScript)) {
                out += ("<jahia:resource type='inlinejavascript' path='" + URLEncoder.encode(resultScript, "UTF-8") + "' insert='false' resource='' title='' key=''/>");
            }
        }

        return out;
    }

    private HashMap<String, java.lang.Object> getBindingMap(RenderContext renderContext, Resource resource, String context) throws RepositoryException {
        HashMap<String, Object> bindingMap = new HashMap<String, Object>();
        String APIVersion = StringUtils.isNotEmpty(jcrRestAPIVersion) ? jcrRestAPIVersion : "v1";
        bindingMap.put(JCR_REST_UTIL_INIT_DEBUG, debugEnabled);
        bindingMap.put(JCR_REST_UTIL_INIT_API_VERSION, stringifyJsParam(APIVersion));
        bindingMap.put(JCR_REST_UTIL_INIT_API_BASE, stringifyJsParam(context + "/modules/api/jcr/" + APIVersion));
        bindingMap.put(JCR_REST_UTIL_INIT_API_CURRENT_WORKSPACE, stringifyJsParam(renderContext.getWorkspace()));
        bindingMap.put(JCR_REST_UTIL_INIT_API_CURRENT_LOCALE, stringifyJsParam(resource.getLocale().toString()));
        bindingMap.put(JCR_REST_UTIL_INIT_API_CURRENT_RESOURCE_IDENTIFIER, stringifyJsParam(resource.getNode().getIdentifier()));
        bindingMap.put(JCR_REST_UTIL_INIT_API_CURRENT_RESOURCE_PATH, stringifyJsParam(resource.getNode().getPath()));

        return bindingMap;
    }

    private String stringifyJsParam(String param) {
        return "'" + param + "'";
    }

    protected String getResolvedTemplate() throws IOException {
        if (resolvedTemplate == null) {
            String templatePath = "/modules/portal-core/WEB-INF/scripts/" + JCR_REST_SCRIPT_TEMPLATE;
            resolvedTemplate = WebUtils.getResourceAsString(templatePath);
            if (resolvedTemplate == null) {
                logger.warn("Unable to lookup template at {}", JCR_REST_SCRIPT_TEMPLATE);
            }
        }
        return resolvedTemplate;
    }


    public void setScriptEngineUtils(ScriptEngineUtils scriptEngineUtils) {
        this.scriptEngineUtils = scriptEngineUtils;
    }

    public void setJcrRestAPIVersion(String jcrRestAPIVersion) {
        this.jcrRestAPIVersion = jcrRestAPIVersion;
    }

    public void setDebugEnabled(Boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    class JCRRestUtilsScriptContext extends SimpleScriptContext {
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
