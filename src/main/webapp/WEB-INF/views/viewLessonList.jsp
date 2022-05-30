<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.entities.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Список уроків</title>
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
                <h2>Список уроків</h2>
                <%
                    Theme theme = (Theme) request.getAttribute("theme");
                    SubjectDetails subjectDetails = theme.getSubjectDetails();
                    Teacher teacher = subjectDetails.getTeacher();
                    List<Lesson> list = (List<Lesson>)request.getAttribute("list");
                    int colspan = 3;
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
                    <tr>
                        <td>Тема:</td>
                        <td><%=theme.getName()%></td>
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
                            <%colspan++;%>
                        </sec:authorize>
                        <th>Дата</th>
                        <th>Тема уроку</th>
                        <th></th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <%
                                if(teacher != null && currUser.getId() == teacher.getId()) {
                            %>
                                    <th></th>
                                    <th></th>
                            <%
                                    colspan += 2;
                                }
                            %>
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
                                   id="date"
                                   onkeyup="filter(id, <%=i++%>)"
                                   class="search"
                                   placeholder="Пошук...">
                        </th>
                        <th>
                            <input type="text"
                                   id="topic"
                                   onkeyup="filter(id, <%=i%>)"
                                   class="search"
                                   placeholder="Пошук...">
                        </th>
                        <th></th>
                        <sec:authorize access="hasAuthority('TEACHER')">
                            <%
                                if(teacher != null && currUser.getId() == teacher.getId()) {
                            %>
                                    <th></th>
                                    <th></th>
                            <%
                                }
                            %>
                        </sec:authorize>
                    </tr>
                    <tbody>
                        <%
                            if (list.isEmpty()) {
                        %>
                                <tr class="card">
                                    <td colspan="<%=colspan%>">Список уроків пустий</td>
                                </tr>
                        <%
                            }
                            for (Lesson lesson:list) {
                                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                String strDate = dateFormat.format(lesson.getDate());
                        %>
                                <tr class="card">
                                    <sec:authorize access="hasAuthority('ADMIN')">
                                        <td><%=lesson.getId()%></td>
                                    </sec:authorize>
                                    <td>
                                        <div class="inline"><i class='material-icons'>today</i></div>
                                        <div class="inline"><%=strDate%></div>
                                    </td>
                                    <td>
                                        <div class="inline"><i class='material-icons'>event_note</i></div>
                                        <div class="inline"><%=lesson.getTopic()%></div>
                                    </td>
                                    <td><a href="<%=root%>lesson/<%=lesson.getId()%>/marks">оцінки</a></td>
                                    <sec:authorize access="hasAuthority('TEACHER')">
                                        <%
                                            if(teacher != null && currUser.getId() == teacher.getId()) {
                                        %>
                                                <td>
                                                    <a href="../../lesson/<%=lesson.getId()%>">
                                                        <i class="material-icons">edit</i>
                                                    </a>
                                                </td>
                                                <td>
                                                    <a href=".<%=root%>lesson/<%=lesson.getId()%>/delete">
                                                        <i class="material-icons">delete</i>
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
                            <button onclick='location.href="lesson"' class="bg-primary">
                                <div class="inline"><i class='material-icons'>edit_calendar</i></div>
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
