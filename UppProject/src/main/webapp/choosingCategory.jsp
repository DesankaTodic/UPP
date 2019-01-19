<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Izbor kategorije</title>
			<link href="<c:url value="/resources/stylesheets/styles.css"/>"
				rel="stylesheet" />
			
			<link href="<c:url value="/resources/stylesheets/reset.css" />"
				rel="stylesheet">
			
			<link href="<c:url value="/resources/stylesheets/animate.css" />"
				rel="stylesheet">
	
	</head>
<body>
	<div id="container" style="height: 190px;">
		<h2 style="text-align: center;padding-top: 10px;padding-bottom: 10px;">Izbor kategorije</h2>
		<hr>
		<c:if test="${fn:length(formProperties) > 0}">
			<form name='f' action="<c:url value='/submitCategory' />"method='POST'>
			
				<fieldset>
				
					<!--da bi pokupio i username i processId-->
					
				 	<input type="hidden"  name="processId" value="${processId}" />
					<input type="hidden"  name="username" value="${username}" />
						
					<c:forEach var="formProperty" items="${formProperties}">
					
						<c:if test="${formProperty.readable == true}">
						<label>${formProperty.name}</label>
					
						
						<c:if test="${formProperty.type.name.equals('string') || formProperty.type.name.equals('long')}">
						<input type="text" <c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if> 
						    <c:if test="${formProperty.writable==false}"> disabled </c:if>  value="${formProperty.value}" />
						</c:if>
						
						<c:if test="${formProperty.type.name.equals('boolean')}">
							<input type="checkbox" <c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if> 
							 <c:if test="${formProperty.writable==false}"> disabled </c:if> <c:if test="${formProperty.value==true}">checked </c:if>/>
						</c:if>
						
						<c:if test="${formProperty.type.name.equals('enum')}">
						<select <c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if>
						  <c:if test="${formProperty.writable==false}"> disabled </c:if> >
						  
						<c:forEach var="key" items="${formProperty.getType().getInformation('values').keySet()}">
							<option value="${key}">${formProperty.getType().getInformation('values').get(key)}</option>
						</c:forEach>
						</select>
						</c:if>
						
						</c:if>
						<c:if test="${formProperty.readable == false}">
							<input type="hidden"  name="${formProperty.id}" value="${formProperty.value}" />
						</c:if>
						<br/>
					</c:forEach>
					 <div class="upperLine" style="margin-top: 50px;width: 53%;">
						<input type="submit"  class="login" value="Potvrda">
					</div>
				
				</fieldset>
			</form>
		
		</c:if>
	</div>

	
</body>
</html>