<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
    <head>
        <title>Log in</title>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="/WEB-INF/css/style.css"%></style>
    </head>
    <body>
        <%@include file="WEB-INF/views/header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <div class="card" style="width: 50%">
                    <br/>
                    <h2 align="center">Log in</h2>

                    <%
                        if(request.getParameter("error") != null) {
                    %>
                            <div class="warning">
                                Invalid username or password.
                            </div>
                    <%
                        }
                        if(request.getParameter("logout") != null) {
                    %>
                            <div class="success">
                                You have been logged out.
                            </div>
                    <%
                        }
                    %>
                    <form method="post" action="/Gradebook/login">
                        <br/>
                        Username:
                        <input type="text" name="username" id="username" required/><br/><br/>
                        Password:
                        <input type="password" name="password" id="password" required/><br/><br/>
                        <sec:csrfInput />
                        <button type="submit" class="bg-primary">Log in</button>
                        <br/><br/>
                    </form>
                </div>
            </div>
        </div>
        <%@include file="WEB-INF/views/footer.jsp"%>
    </body>
</html>
