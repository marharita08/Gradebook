<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.Theme" %>
<%@ page import="org.example.entities.SubjectDetails" %>
<%@ page import="org.example.entities.Teacher" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Список тем</title>
        <link rel="icon" type="img/png" href="../images/icon.png">
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
                <%
                    SubjectDetails subjectDetails = (SubjectDetails) request.getAttribute("subjectDetails");
                    Teacher teacher = subjectDetails.getTeacher();
                    List<Theme> list = (List<Theme>)request.getAttribute("list");
                    int colspan = 2;
                    if (currUser.hasRole("ADMIN")) {
                        colspan ++;
                    }
                    if (currUser.hasRole("TEACHER")) {
                        colspan += 2;
                    }
                %>
                <table>
                    <tr>
                        <td>Клас:</td>
                        <td><%=subjectDetails.getPupilClass().getName()%></td>
                    </tr>
                    <tr>
                        <td>Предмет:</td>
                        <td><%=subjectDetails.getSubject().getName()%></td>
                    </tr>
                    <%
                        if(teacher != null) {
                    %>
                            <tr>
                                <td>Вчитель:</td>
                                <td><%=teacher.getName()%></td>
                            </tr>
                    <%
                        }
                    %>
                </table>
                <table id="myTable">
                    <tr>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>ID</th>
                        </sec:authorize>
                        <th>Назва</th>
                        <th></th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <th></th>
                            <th></th>
                            <th></th>
                        </sec:authorize>
                    </tr>
                    <tr>
                        <%
                            int i = 0;
                        %>
                        <sec:authorize access="hasAuthority('ADMIN')">
                            <th>
                                <input type="text"
                                       id="id"
                                       onkeyup="filter(id, <%=i++%>)"
                                       class="search-slim"
                                       placeholder="Пошук...">
                            </th>
                        </sec:authorize>
                        <th>
                            <input type="text"
                                   id="name"
                                   onkeyup="filter(id, <%=i%>)"
                                   class="search"
                                   placeholder="Пошук...">
                        </th>
                        <th></th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <th></th>
                            <th></th>
                            <th></th>
                        </sec:authorize>
                    </tr>
                    <tbody>
                        <%
                            if (list.isEmpty()) {
                        %>
                                <tr class="card">
                                    <td colspan="<%=colspan%>">Список тем пустий</td>
                                </tr>
                        <%
                            }
                            for (Theme theme:list) {
                        %>
                                <tr class="card">
                                    <sec:authorize access="hasAuthority('ADMIN')">
                                        <td><%=theme.getId()%></td>
                                    </sec:authorize>
                                    <td>
                                        <div class="inline"><i class='material-icons'>text_snippet</i></div>
                                        <div class="inline"><%=theme.getName()%></div>
                                    </td>
                                    <td>
                                        <a href="<%=root%>theme/<%=theme.getId()%>/lessons">уроки</a>
                                    </td>
                                    <sec:authorize access="hasAuthority('TEACHER')">
                                        <%
                                            if(teacher != null && currUser.getId() == teacher.getId()) {
                                        %>
                                        <td>
                                            <a href="<%=root%>theme/<%=theme.getId()%>/lesson">додати урок</a>
                                        </td>
                                        <td>
                                            <a href="<%=root%>theme/<%=theme.getId()%>">
                                                <i class="material-icons">edit</i>
                                            </a>
                                        </td>
                                        <td>
                                            <a>
                                                <form action="<%=root%>theme/<%=theme.getId()%>/delete" method=post>
                                                    <sec:csrfInput />
                                                    <button type="submit">
                                                        <i class="material-icons">delete</i>
                                                    </button>
                                                </form>
                                            </a>
                                        </td>
                                        <%
                                            }
                                        %>
                                    </sec:authorize>
                                </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                <br/>
                <button onclick='location.href="<%=root%>main"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Меню</div>
                </button>
                <button onclick=history.back() class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Назад</div>
                </button>
                <sec:authorize access="hasAuthority('TEACHER')">
                    <%
                        if(teacher != null && currUser.getId() == teacher.getId()) {
                    %>
                            <button onclick='location.href="theme"' class="bg-primary">
                                <div class="inline"><i class='material-icons'>note_add</i></div>
                                <div class="inline">Додати</div>
                            </button>
                    <%
                        }
                    %>
                </sec:authorize>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/filter.js"%>
    </script>
</html>

