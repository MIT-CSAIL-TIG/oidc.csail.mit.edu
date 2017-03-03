<%@page import="org.springframework.security.web.savedrequest.HttpSessionRequestCache"%>
<%@page import="org.springframework.security.web.savedrequest.SavedRequest"%>
<%@page import="org.springframework.web.util.UriUtils"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
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
	$('#j_username').focus();
});

//-->
</script>
<o:topbar />
<div class="container-fluid main">

	<h1><spring:message code="login.login_with_username_and_password"/></h1>

	<c:if test="${ param.error != null }">
		<c:choose>
			<c:when test="${ param.error == "kerberos" }">
				<div class="alert alert-error"><spring:message code="login.error_kerberos"/></div>
			</c:when>
			<c:when test="${ param.error == "cert" }">
				<div class="alert alert-error"><spring:message code="login.error_cert"/></div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-error"><spring:message code="login.error"/></div>	
			</c:otherwise>
		</c:choose>				
	</c:if>


<div class="row-fluid">
      <div class="span4 well">
       <h2>Log in with Kerberos username and password</h2>
	   <form action="<%=request.getContextPath()%>/j_spring_security_check" method="POST">
	   	<div>
         <div class="input-prepend input-append input-block-level">
         	<span class="add-on"><i class="icon-user"></i></span>
         	<input type="text" placeholder="Username" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" value="" id="j_username" name="j_username">
         	<span class="add-on">@csail.mit.edu</span>
         	</div>
        </div>
        <div>
         <div class="input-prepend input-block-level">
         	<span class="add-on"><i class="icon-lock"></i></span>
         	<input type="password" placeholder="Password" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" id="j_password" name="j_password">
         </div>
        </div>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <div><input type="submit" class="btn btn-inverse" value="Log In" name="submit"></div>
	   </form>
      </div>
   
	<div class="span4 well">
		<h2>Log in with Kerberos</h2>
		<div><a href="kerberos_login" class="btn btn-inverse">Use Existing Kerberos Tickets</a></div>
	</div>

	<div class="span4 well">
		<h2>Log in with Certificate</h2>
		<div><a href="cert_login" class="btn btn-inverse">Use MIT Certificate</a></div>
	</div>

</div>

</div>
<o:footer/>
