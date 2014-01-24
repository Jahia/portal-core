println("<script type=\"text/javascript\">");
println("portal = new Jahia.Portal({");
options.eachWithIndex() {
    obj, i -> println "${obj.key}: ${obj.value}${i+1 < options.size() ? ',' : ''}"
};
println("});")
println("</script>")