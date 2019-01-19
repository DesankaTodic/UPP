<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Aukcija</title>
<link href="<c:url value="/resources/stylesheets/styles.css"/>"
	rel="stylesheet" />

<link href="<c:url value="/resources/stylesheets/reset.css" />"
	rel="stylesheet">

<link href="<c:url value="/resources/stylesheets/animate.css" />"
	rel="stylesheet">
</head>
<body>
	<div id="container"
		style="width: 20%; height: 43%; top: 0px; left: 0px; margin-top: 10%; margin-left: 40%;">
		<h2
			style="text-align: center; padding-top: 10px; padding-bottom: 10px;">Aukcija</h2>
		<c:if test="${fn:length(formProperties) > 0}">

			<form name='f' action="<c:url value='/submitAuction' />"
				method='POST'>

				<fieldset>

					<!-- 
					Treba jos dodati validaciju (da li su uneta required obelezja, 
					da li su uneta slova iako je tip long - ili obezbediti da se to onemoguci
					Pokriti unos datuma
					Pokusati smestiti u poseban jsp fajl forme, pa ukljuciti pomocu include
					 -->
					<input type="hidden" name="processId" value="${processId}" />

					<c:forEach var="formProperty" items="${formProperties}">

						<c:if test="${formProperty.readable == true}">
							<label>${formProperty.name}</label>
							
							<c:choose>
								<c:when test="${formProperty.id.equals('maxDate') || formProperty.id.equals('finishedDate')}">
									<input type="text"
										<c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if>
										<c:if test="${formProperty.writable==false}"> disabled </c:if>
										value="${formProperty.value}" placeholder="yyyy-MM-dd"/>
								</c:when>

								<c:otherwise>
									<c:if
										test="${formProperty.type.name.equals('string')}">
										<input type="text"
											<c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if>
											<c:if test="${formProperty.writable==false}"> disabled </c:if>
											value="${formProperty.value}" />
									</c:if>
									
									<c:if
										test="${formProperty.type.name.equals('long')}">
										<input type="number"
											<c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if>
											<c:if test="${formProperty.writable==false}"> disabled </c:if>
											value="${formProperty.value}" />
									</c:if>
		
									<c:if test="${formProperty.type.name.equals('boolean')}">
										<input type="checkbox"
											<c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if>
											<c:if test="${formProperty.writable==false}"> disabled </c:if>
											<c:if test="${formProperty.value==true}">checked </c:if> />
									</c:if>
		
									<c:if test="${formProperty.type.name.equals('enum')}">
										<select
											<c:if test="${formProperty.writable==true}"> name="${formProperty.id}"</c:if>
											<c:if test="${formProperty.writable==false}"> disabled </c:if>>
		
											<c:forEach var="key"
												items="${formProperty.getType().getInformation('values').keySet()}">
												<option value="${key}">${formProperty.getType().getInformation('values').get(key)}</option>
											</c:forEach>
										</select>
									</c:if>
								</c:otherwise>
							</c:choose>

						</c:if>
						<c:if test="${formProperty.readable == false}">
							<input type="hidden" name="${formProperty.id}"
								value="${formProperty.value}" />
						</c:if>
						<br />
					</c:forEach>
					<div class="upperLine" style="margin-top: 30px; width: 60%;">
						<input type="submit" class="login" value="Potvrda">
					</div>

				</fieldset>
			</form>
		</c:if>
	</div>


</body>
</html>