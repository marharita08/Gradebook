<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title>Sign Up</title>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="/WEB-INF/css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <div class="card" style="width: 50%">
                    <br/>
                    <h2>Sign Up</h2>
                    <br/>
                    <form:form>
                        <p id="placeToShow" class="warning"></p>
                        Username:
                        <form:input type="text" path="username" onkeyup="checkUsername(-1)"/>
                        <br/><br/>
                        Password:
                        <form:input type="password" path="password"/>
                        <br/><br/>
                        <button formmethod="post" formaction="" class="bg-primary">Sign Up</button>
                        <br/><br/>
                    </form:form>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/checkUsername.js"%>
    </script>
</html>
