<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Dodatni info</title>
			<link href="<c:url value="/resources/stylesheets/styles.css"/>"
				rel="stylesheet" />
			
			<link href="<c:url value="/resources/stylesheets/reset.css" />"
				rel="stylesheet">
			
			<link href="<c:url value="/resources/stylesheets/animate.css" />"
				rel="stylesheet">
	
	</head>
<body>
	<div id="container" style="height: 190px;">
		<h2 style="text-align: center;padding-top: 10px;padding-bottom: 10px;">Info za aukciju: ${auctionId}</h2>
		<hr>
			<form name='f' action="<c:url value='/submitAddingInfo' />"method='POST'>
			
				<fieldset>

					<input type="hidden"  name="processId" value="${processId}" />
					<input type="hidden"  name="offerId" value="${offerId}" />
					<input type="hidden"  name="auctionId" value="${auctionId}" />							

						<label>Info: </label>

						<textarea name="info"></textarea> 

						<br/>
					 <div class="upperLine" style="margin-top: 50px;width: 53%;">
						<input type="submit"  class="login" value="Potvrda">
					</div>
				
				</fieldset>
			</form>
		
	</div>

	
</body>
</html>