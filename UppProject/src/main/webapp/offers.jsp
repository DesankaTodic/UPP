<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Auction</title>
<link href="<c:url value="/resources/stylesheets/styles.css"/>"
	rel="stylesheet" />

</head>
<body>

<h1>Ponude</h1>


<table border="1">
    <tr>
        <th>Datum</th>
        <th>Cijena</th>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
    </tr>
    <c:forEach items="${offers}" var="offer">
        <tr>
            <td>${offer.date}</td>
            <td>${offer.price}</td>
            <td><a href="${pageContext.request.contextPath}/${rootAccept}/${processId}/${offer.id}">Prihvati</a></td>
            <td><a href="${pageContext.request.contextPath}/${rootInfo}/${processId}/${offer.id}">Dodatne informacije</a></td>
        </tr>
    </c:forEach>
</table>
<table border="1">
	<tr>
		<td colspan="2">&nbsp;</td><td><a href="${pageContext.request.contextPath}/giveup/${processId}">Odustani</a></td>
		<td><a href="${pageContext.request.contextPath}/startover/${processId}">Kreni ispocetka</a></td>
	</tr>
</table>
	
</body>
</html>


