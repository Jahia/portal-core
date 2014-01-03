<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="button" id="monbutton" value="test"/>

<script>
    $(document).ready(function(){
        $("#monbutton").click(function(){
            alert("success !!!");
        });
    });
</script>
