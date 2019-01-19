<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<title>Login</title>


<link href="<c:url value="/resources/stylesheets/styles.css"/>"
	rel="stylesheet" />

<link href="<c:url value="/resources/stylesheets/reset.css" />"
	rel="stylesheet">

<link href="<c:url value="/resources/stylesheets/animate.css" />"
	rel="stylesheet">

</head>

<body>


	<div id="container" style="height: 170px;">

		<form name='f' action="<c:url value='submitLogin' />"
			method='POST'>

			<c:if test="${not empty error}">
				<div class="errorblock">
				Pogresni kredencijali, pokusajte ponovo.
				</div>
			</c:if>

		
			<label for="name">Korisnicko ime</label>
			<input type="text" name='username' value=''> 
				
			<label for="username">Lozinka</label>
			<input type="password" name='password'>

			<div class="upperLine" style="margin-top: 10px;">
				<input type="submit" class="login" value="Login">
			</div>


		</form>

	</div>



</body>

</html>






