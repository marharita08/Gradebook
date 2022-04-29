<%@ page import="org.example.entities.SchoolYear" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title><%=request.getAttribute("title")%></title>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="../css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <div class="card" style="width: 50%">
                    <br/>
                    <h2 align="center"><%=request.getAttribute("title")%></h2>
                    <form:form>
                        <br/>
                        <div class="row">
                            <div class="col-25">
                                <label>Name</label>
                            </div>
                            <div class="col-75">
                                <form:input path="name" required="true"/><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>School year</label>
                            </div>
                            <div class="col-75">
                                <form:select path="schoolYear.id">
                                    <%
                                        for (SchoolYear schoolYear:(List<SchoolYear>)request.getAttribute("list")) {
                                    %>
                                    <option value="<%=schoolYear.getId()%>"
                                            <%=(int)request.getAttribute("selectedSchoolYear") == schoolYear.getId() ? "selected='selected'":""%>>
                                        <%=schoolYear.getName()%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </form:select><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>Start date</label>
                            </div>
                            <div class="col-75">
                                <form:input type="date" path="startDate" required="true"/><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>End date</label>
                            </div>
                            <div class="col-75">
                                <form:input type="date" path="endDate" required="true"/><br/><br/>
                            </div>
                        </div>
                        <form:input path="id" type="hidden"/>
                        <button onclick="history.back()" type="button" class="bg-primary">
                            <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                            <div class="inline">Cancel</div>
                        </button>
                        <button formmethod="post" formaction="<%=request.getAttribute("formAction")%>" class="bg-primary">
                            <div class="inline"><i class='material-icons'>save</i></div>
                            <div class="inline">Save</div>
                        </button>
                        <br/><br/>
                    </form:form>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>
