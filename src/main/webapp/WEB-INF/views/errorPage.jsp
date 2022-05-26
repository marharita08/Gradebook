<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
        <link rel="icon" type="img/png" href="images/icon.png">
        <title>Помилка</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <style><%@include file="../css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <%
                    int status = response.getStatus();
                    if(status == 500) {
                %>
                        <h2>На сервері сталася помилка</h2>
                <%
                        if (exception == null) {
                            Throwable e = (Throwable) request.getAttribute("javax.servlet.error.exception");
                            if (e != null) {
                %>
                                <p>Type: <%= e.getClass()%></p>
                                <p>Message: <%= e.getMessage() %></p>
                <%
                            }
                        } else {
                %>
                            <p>Type: <%= exception.getClass()%></p>
                            <p>Message: <%= exception.getMessage() %></p>
                <%
                        }
                    } else if (status == 403) {
                %>
                        <h2>Помилка: <%=status%></h2>
                        <p>Заборонено.</p>
                <%
                    } else if (status == 404) {
                %>
                        <h2>Помилка: <%=status%></h2>
                        <p>Ресурс не знайдено.</p>
                <%
                    } else {
                %>
                        <h2>Помилка: <%=status%></h2>
                <%
                    }
                    String msg = (String) request.getAttribute("message");
                %>
                <p><%=msg != null ? "Message: " + msg : ""%></p>
                <img alt="user image" src="<%=root%>images/Oopsbutton.jpg" class="user-img">
                <br/>
                <button onclick="history.back()" type="button" class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Назад</div>
                </button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>
