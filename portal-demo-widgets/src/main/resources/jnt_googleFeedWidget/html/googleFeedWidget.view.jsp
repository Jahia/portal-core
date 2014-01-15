<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<script type="text/javascript">
    // Load a bunch of scripts and make sure the DOM is ready.
    $.when(
                    $.getScript("https://www.google.com/jsapi"),

                    // DOM ready deferred.
                    //
                    // NOTE: This returns a Deferred object, NOT a promise.
                    $.Deferred(
                            function (deferred) {
                                // In addition to the script loading, we also
                                // want to make sure that the DOM is ready to
                                // be interacted with. As such, resolve a
                                // deferred object using the $() function to
                                // denote that the DOM is ready.
                                $(deferred.resolve);
                            }
                    )
            ).done(
            function (/* Deferred Results */) {
                // The DOM is ready to be interacted with AND all
                // of the scripts have loaded. Let's test to see
                // that the scripts have loaded.
                if (google) {
                    google.load("feeds", "1", {'callback': function () {
                        var feedControl = new google.feeds.FeedControl();

                        // Add two feeds.
                        feedControl.addFeed("http://www.digg.com/rss/index.xml", "Digg");
                        feedControl.addFeed("http://feeds.feedburner.com/Techcrunch", "TechCrunch");

                        // Draw it.
                        feedControl.draw(document.getElementById("feeds-${currentNode.identifier}"));
                    }});
                }
            }
    );
</script>

<div id="feeds-${currentNode.identifier}">

</div>