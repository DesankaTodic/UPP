<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<title>Pocetna stranica</title>

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
		<form name='f' action="<c:url value='/ok' />"method='GET'>
		
		<fieldset>
		<h2 style="padding-left: 75px;padding-top: 10px;">Uspjesno ste pokrenuli aukciju!</h2>
		<h2 style="padding-left: 100px;padding-top: 10px;">Dobicete ponude na mejl!</h2>
			
		 <div  class="upperLine" style="margin-top: 25px;width: 60%;">
			<input type="submit"  class="login" value="OK">
		</div>
		
		</fieldset>
		</form>
	</div>
	
</body>
</html>