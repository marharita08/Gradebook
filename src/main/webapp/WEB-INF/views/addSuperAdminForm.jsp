<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>

<html>
    <head>
        <title>Додати адміністратора</title>
        <link rel="icon" type="img/png" href="/Gradebook/images/icon.png">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style><%@include file="/WEB-INF/css/style.css"%></style>
    </head>
    <body>
        <%@include file="header.jsp"%>
        <div align="center">
            <div align="center" class="box">
                <br/>
                <div class="card" style="width: 50%">
                    <br/>
                    <h2>Додати адміністратора</h2>
                    <p>Введіть дані користувача, який буде головним адміністратором вашої школи.</p>
                    <br/>
                    <form:form>
                        <form:input path="dbName" type="hidden"/>
                        <div class="row">
                            <div class="col-25">
                                <label>Ім<span>&#39;</span>я</label>
                            </div>
                            <div class="col-75">
                                <form:input type="text" path="username" placeholder="Ім'я користувача"/>
                                <br/><br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-25">
                                 <label>Пароль</label>
                            </div>
                            <div class="col-75">
                                <form:input type="password" path="password" placeholder="Пароль"/>
                                <br/><br/>
                            </div>
                        </div>
                        <button formmethod="post" formaction="admin" class="bg-primary" id="save">Додати</button>
                        <br/><br/>
                    </form:form>
                </div>
            </div>
        </div>
        <%@include file="footer.jsp"%>
    </body>
</html>