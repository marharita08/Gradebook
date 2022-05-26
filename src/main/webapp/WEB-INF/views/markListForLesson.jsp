<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="org.example.entities.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Mark list</title>
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
                    Lesson lesson = (Lesson) request.getAttribute("lesson");
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    String strDate = dateFormat.format(lesson.getDate());
                    Theme theme = lesson.getTheme();
                    SubjectDetails subjectDetails = theme.getSubjectDetails();
                    Teacher teacher = subjectDetails.getTeacher();
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
                    <tr>
                        <td>Тема:</td>
                        <td><%=theme.getName()%></td>
                    </tr>
                    <tr>
                        <td>Дата:</td>
                        <td><%=strDate%></td>
                    </tr>
                    <tr>
                        <td>Тема уроку:</td>
                        <td><%=lesson.getTopic()%></td>
                    </tr>
                </table>
                <form action="save-marks" method="post">
                    <table id="myTable">
                        <tr>
                            <th>Учень</th>
                            <th>Оцінка</th>
                        </tr>
                        <tr>
                            <th>
                                <input id="pupil" onkeyup="filter(id, 0)" class="search" placeholder="Пошук...">
                            </th>
                            <th></th>
                        </tr>
                        <%
                            int i = 0;
                            for (Mark mark:((MarkList)request.getAttribute("list")).getList()) {
                        %>
                                <tr class="borders">
                                    <td class="borders">
                                        <div class="inline"><i class='material-icons'>person</i></div>
                                        <div class="inline"><%=mark.getPupil().getName()%></div>
                                    </td>
                                    <%
                                        if(teacher!=null && teacher.getId() == currUser.getId()) {
                                    %>
                                            <td class="borders">
                                                <input name="list[<%=i%>].id" value="<%=mark.getId()%>" type="hidden"/>
                                                <input name="list[<%=i%>].pupil.id" value="<%=mark.getPupil().getId()%>" type="hidden"/>
                                                <input name="list[<%=i%>].lesson.id" value="<%=lesson.getId()%>" type="hidden"/>
                                                <input name="list[<%=i++%>].mark"
                                                       value="<%=mark.getMark()==null?"":mark.getMark()%>"
                                                       pattern="н|[1-9]|1[0-2]"
                                                       oninvalid="this.setCustomValidity('Введіть коректне значення')"
                                                       oninput="this.setCustomValidity('')"/>
                                            </td>
                                    <%
                                        } else if (currUser.hasRole("ADMIN") || currUser.hasRole("TEACHER")
                                            || currUser.getId() == mark.getPupil().getId()) {
                                    %>
                                            <td class="borders"><%=mark.getMark()==null?"":mark.getMark()%></td>
                                    <%
                                        } else {
                                    %>
                                            <td class="borders"></td>
                                    <%
                                        }
                                    %>
                                </tr>
                        <%
                            }
                        %>
                    </table>
                    <br/>
                    <%
                        if(teacher!=null && teacher.getId() == currUser.getId()) {
                    %>
                            <button type="submit" class="bg-primary">
                                <div class="inline"><i class='material-icons'>save</i></div>
                                <div class="inline">Зберегти</div>
                            </button>
                    <%
                        }
                    %>
                </form>
                <button onclick='location.href="<%=root%>index.jsp"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Меню</div>
                </button>
                <button onclick=history.back() class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Назад</div>
                </button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/filter.js"%>
    </script>
</html>
