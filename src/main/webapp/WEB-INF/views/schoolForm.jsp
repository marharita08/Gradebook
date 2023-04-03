<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
        <%
            School currSchool = (School)request.getAttribute("command");
        %>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <br/>
                <div class="card" style="width: 50%">
                    <br/>
                    <h2 align="center"><%=request.getAttribute("title")%></h2>
                    <form:form enctype="multipart/form-data">
                        <form:input path="id" type="hidden"/><br/><br/>
                        <br/>
                        <div class="row">
                            <div class="col-25">
                                <label>Назва</label>
                            </div>
                            <div class="col-75">
                                <form:input path="name" required="true"/><br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                <label>Фото</label>
                            </div>
                            <div class="col-75">
                                <div id="imgDiv" <%=currSchool.getPhoto()==null?"style='display:none'":""%>>
                                    <img src='<%=root + currSchool.getPhoto()%>' style='width:70%' id="image">
                                </div>
                                <label class="bg-primary button" id="imgBtn">
                                    <div class="inline"><i class='material-icons'>add_a_photo</i></div>
                                    <div class="inline">Обрати фото</div>
                                    <input type="file" name="file" accept="image/*" style="display:none" id="imgInp"/>
                                </label>
                                <button class="bg-primary" style="display:none" id="imgDel" type="reset">
                                     <div class="inline"><i class='material-icons'>delete</i></div>
                                     <div class="inline">Видалити фото</div>
                                </button>
                                <br/><br/>
                            </div>
                        </div>
                        <p>Обране фото буде відображатися на головній сторінці вашої школи</p>
                        <button onclick="history.back()" type="button" class="bg-primary">
                            <div class="inline"><i class='material-icons'>keyboard_return</i></div>
                            <div class="inline">Назад</div>
                        </button>
                        <button formmethod="post" formaction='<%=request.getAttribute("action")%>' class="bg-primary">
                            <div class="inline"><i class='material-icons'>save</i></div>
                            <div class="inline">Зберегти</div>
                        </button>
                        <br/><br/>
                    </form:form>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
    <script>
        imgInp.onchange = evt => {
            const [file] = imgInp.files
            if (file) {
                imgDiv.style.display = "block";
                imgBtn.style.display = "none";
                imgDel.style.display = "block";
                image.src = URL.createObjectURL(file);
            }
        }
        imgDel.onclick = evt => {
            if (<%=currSchool.getPhoto()==null%>) {
                imgDiv.style.display = "none";
            } else {
                image.src = '<%=root + currSchool.getPhoto()%>';
            }
            imgBtn.style.display = "inline-block";
            imgDel.style.display = "none";
        }
    </script>
</html>