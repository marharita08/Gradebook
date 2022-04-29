<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="org.example.entities.*" %>
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
                <h2><%=request.getAttribute("header")%></h2>
                <%
                    Lesson lesson = (Lesson) request.getAttribute("lesson");
                    Theme theme = lesson.getTheme();
                    SubjectDetails subjectDetails = theme.getSubjectDetails();
                    Teacher teacher = subjectDetails.getTeacher();
                %>
                <table>
                    <tr>
                        <td>Class:</td>
                        <td><%=subjectDetails.getPupilClass().getName()%></td>
                    </tr>
                    <tr>
                        <td>Subject:</td>
                        <td><%=subjectDetails.getSubject().getName()%></td>
                    </tr>
                    <%
                        if(teacher != null) {
                    %>
                            <tr>
                                <td>Teacher:</td>
                                <td><%=teacher.getName()%></td>
                            </tr>
                    <%
                        }
                    %>
                    <tr>
                        <td>Theme:</td>
                        <td><%=theme.getName()%></td>
                    </tr>
                    <tr>
                        <td>Date:</td>
                        <td><%=lesson.getDate()%></td>
                    </tr>
                    <tr>
                        <td>Topic:</td>
                        <td><%=lesson.getTopic()%></td>
                    </tr>
                </table>
                <form action="save-marks" method="post">
                    <table id="myTable">
                        <tr>
                            <th>Pupil</th>
                            <th>Mark</th>
                        </tr>
                        <tr>
                            <th>
                                <input id="pupil" onkeyup="filter(id, 0)" class="search" placeholder="Search...">
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
                                                       pattern="a|[1-9]|1[0-2]"
                                                       oninvalid="this.setCustomValidity('Inputted value is invalid')"
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
                                <div class="inline">Save</div>
                            </button>
                    <%
                        }
                    %>
                </form>
                <button onclick='location.href="<%=root%>index.jsp"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>list</i></div>
                    <div class="inline">Menu</div>
                </button>
                <button onclick=history.back() class="bg-primary">
                    <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                    <div class="inline">Back</div>
                </button>
                <button onclick='location.href="../../theme/<%=theme.getId()%>/lessons"' class="bg-primary">
                    <div class="inline"><i class='material-icons'>event_note</i></div>
                    <div class="inline">To lessons</div>
                </button>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        <%@include file="../js/filter.js"%>
    </script>
</html>
