<%@page import="org.springframework.security.web.savedrequest.HttpSessionRequestCache"%>
<%@page import="org.springframework.security.web.savedrequest.SavedRequest"%>
<%@page import="org.springframework.web.util.UriUtils"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%

SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

if (savedRequest != null) {
	session.setAttribute("LOGIN_REDIRECT", savedRequest.getRedirectUrl());
}
				
%>
<o:header title="Log In" />
<script type="text/javascript">
<!--

$(document).ready(function() {
	// select the appropriate field based on context
	$('#<c:out value="${ login_hint != null ? 'j_password' : 'j_username' }" />').focus();
});

//-->
</script>
<o:topbar />
<div class="container-fluid main">

	<c:if test="${ param.error != null }">
		<c:choose>
			<c:when test="${ param.error == 'kerberos' }">
				<div class="alert alert-error"><spring:message code="login.error_kerberos"/></div>
			</c:when>
			<c:when test="${ param.error == 'cert' }">
				<div class="alert alert-error"><spring:message code="login.error_cert"/></div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-error"><spring:message code="login.error"/></div>	
			</c:otherwise>
		</c:choose>				
	</c:if>


<div class="row-fluid">
      <div class="span4 well">
       <h2><spring:message code="login.login_with_username_and_password"/></h2>
	   <form action="<%=request.getContextPath()%>/login" method="POST">
	   	<div>
         <div class="input-prepend input-append input-block-level">
         	<span class="add-on"><i class="icon-user"></i></span>
         	<input type="text" placeholder="<spring:message code="login.username"/>" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" value="<c:out value="${ login_hint }" />" id="j_username" name="username">
         	<span class="add-on"><spring:message code="login.username_address_space"/></span>
         	</div>
        </div>
        <div>
         <div class="input-prepend input-block-level">
         	<span class="add-on"><i class="icon-lock"></i></span>
         	<input type="password" placeholder="<spring:message code="login.password"/>" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" id="j_password" name="password">
         </div>
        </div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <div><input type="submit" class="btn btn-inverse" value="<spring:message code="login.login-button"/>" name="submit"></div>
	   </form>
      </div>
   
	<div class="span4 well">
		<h2><spring:message code="login.use_kerberos"/></h2>
		<div><a href="kerberos_login" class="btn btn-inverse"><spring:message code="login.kerberos_button"/></a></div>
	</div>

	<div class="span4 well">
		<h2><spring:message code="login.use_cert"/></h2>
		<div><a href="cert_login" class="btn btn-inverse"><spring:message code="login.cert_button"/></a></div>
	</div>

</div>

</div>
<o:footer/>
