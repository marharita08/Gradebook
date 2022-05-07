<%@ page import="org.example.entities.Subject" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Mark" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Mark list</title>
        <link rel="icon" type="img/png" href="../../../images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="../css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <ul class="breadcrumb"><%=request.getAttribute("crumbs")%></ul>
                <h2><%=request.getAttribute("header")%></h2>
                <table id="myTable">
                    <tr>
                        <th>Subject</th>
                        <th>Marks</th>
                    </tr>
                    <tr>
                        <th>
                            <input type="text"
                                   id="subject"
                                   onkeyup="filter(id, 0)"
                                   class="search"
                                   placeholder="Search...">
                        </th>
                        <th></th>
                    </tr>
                    <%
                        for (Subject subject:(List<Subject>)request.getAttribute("subjectList")) {
                    %>
                            <tr class="borders">
                                <td class="borders">
                                    <div class="inline"><i class='material-icons'>import_contacts</i></div>
                                    <div class="inline"><%=subject.getName()%></div>
                                </td>
                                <td class="borders">
                                    <%
                                        for (Mark mark:(List<Mark>)request.getAttribute("list")) {
                                    %>
                                            <%=mark.getLesson().getTheme().getSubjectDetails().getSubject().equals(subject)
                                                ? mark.getMark() + " ":""%>
                                    <%
                                        }
                                    %>
                                </td>
                            </tr>
                    <%
                        }
                    %>
                </table>
                <br/>
                <button onclick='location.href="<%=root%>index.jsp"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Menu</div>
                </button>
                <button onclick=history.back() class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Back</div>
                </button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/filter.js"%>
    </script>
</html>
