<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Obavjestenje</title>
			<link href="<c:url value="/resources/stylesheets/styles.css"/>"
				rel="stylesheet" />
			
			<link href="<c:url value="/resources/stylesheets/reset.css" />"
				rel="stylesheet">
			
			<link href="<c:url value="/resources/stylesheets/animate.css" />"
				rel="stylesheet">

</head>
<body>
	<div id="container" style="width: 20%;height: 22%;top: 0px;left: 0px;margin-top: 15%;margin-left: 40%;">
		<h2 style="text-align: center;padding-top: 10px;padding-bottom: 10px;">Obavjestenje</h2>
		<hr>
		<form name='f' action="<c:url value='/login' />"method='GET'>
		
		<fieldset>
		<h2 style="padding-left: 20px;padding-top: 10px;">Poslat vam je mejl sa linkom	za potvrdu	registracije!</h2>
		<h2 style="padding-left: 100px;padding-top: 10px;">Potvrdite u roku od 24 sata!</h2>
			
		 <div  class="upperLine" style="margin-top: 25px;width: 60%;">
			<input type="submit"  class="login" value="Logovanje">
		</div>
		
		</fieldset>
		</form>
	</div>
	
</body>
</html>