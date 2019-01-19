<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Greska</title>
			<link href="<c:url value="/resources/stylesheets/styles.css"/>"
				rel="stylesheet" />
			
			<link href="<c:url value="/resources/stylesheets/reset.css" />"
				rel="stylesheet">
			
			<link href="<c:url value="/resources/stylesheets/animate.css" />"
				rel="stylesheet">

</head>
<body>
	<div id="container" style="height: 215px;">
		<h2 style="text-align: center;padding-top: 10px;padding-bottom: 10px;">Greska</h2>
		<hr>
		<form name='f' action="<c:url value='/registration' />"method='GET'>
		
			<fieldset>
			<h2 style="padding-left: 40px;padding-top: 25px;">Username ili email vec postoji u sistemu!</h2>
				
			 <div class="upperLine" style="margin-top: 50px;width: 53%;">
				<input type="submit" class="login" value="Ispravi">
			</div>
			
			</fieldset>
		</form>
	</div>
	
</body>
</html>