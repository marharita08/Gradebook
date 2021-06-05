<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <title>Error Page</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <style><%@include file="../css/style.css"%></style>
</head>
<body>
<%@include file="header.jsp"%>
<%
    int status = response.getStatus();
    if(status == 500){ %>
        <h2>Exception occurred while processing the request</h2>
    <%
        if (exception == null){
            Throwable e = (Throwable) request.getAttribute("javax.servlet.error.exception");%>
            <p>Type: <%= e.getClass()%></p>
            <p>Message: <%= e.getMessage() %></p>
    <%
        } else {%>
            <p>Type: <%= exception.getClass()%></p>
            <p>Message: <%= exception.getMessage() %></p>
<%
        }
    } else if (status == 403) {%>
        <h2>Error: <%=status%></h2>
        <p>forbidden</p>
    <%
    } else if (status == 404) {%>
        <h2>Error: <%=status%></h2>
        <p>page not found</p>
<%
    } else {%>
        <h2>Error: <%=status%></h2>
<%}%>
<button onclick='history.back()'>Back</button>
<%@include file="footer.jsp"%>
</body>
</html>
