<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.example.entities.User" %>
<%@ page import="org.apache.log4j.Logger" %>
<header>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <%
        User currUser = null;
        try {
            if(!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
                currUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
        } catch (Exception e) {
            Logger.getLogger("Header").error(e.getMessage(), e);
        }
        String root = "/Gradebook/";
    %>
    <nav class="navbar-fixed-top  bg-primary">
        <ul>
            <li>
                <a href="<%=root%>main">
                    <div class="inline"><i class='material-icons'>home</i></div>
                    <div class="inline">Головна</div>
                </a>
            </li>
            <sec:authorize access="!isAuthenticated()">
                <li>
                    <a href="<%=root%>school">
                        <div class="inline"><i class='material-icons'>add_box</i></div>
                        <div class="inline">Додати школу</div>
                    </a>
                </li>
            </sec:authorize>
            <sec:authorize access="hasAuthority('PUPIL')">
                <li>
                    <a href="<%=root%>pupil/<%=currUser.getId()%>/pupils">
                        <div class="inline"><i class='material-icons'>group</i></div>
                        <div class="inline">Мій клас</div>
                    </a>
                </li>
                <li>
                    <a href="<%=root%>pupil/<%=currUser.getId()%>/subject-details">
                        <div class="inline"><i class='material-icons'>import_contacts</i></div>
                        <div class="inline">Мої предмети</div>
                    </a>
                </li>
                <li>
                    <a href="<%=root%>pupil/<%=currUser.getId()%>/marks">
                        <div class="inline"><i class='material-icons'>edit</i></div>
                        <div class="inline">Мої оцінки</div>
                    </a>
                </li>
            </sec:authorize>
            <sec:authorize access="hasAuthority('TEACHER')">
                <li>
                    <a href="<%=root%>teacher/<%=currUser.getId()%>/classes" >
                        <div class="inline"><i class='material-icons'>group</i></div>
                        <div class="inline">Мої класи</div>
                    </a>
                </li>
                <li>
                    <a href="<%=root%>teacher/<%=currUser.getId()%>/subject-details">
                        <div class="inline"><i class='material-icons'>import_contacts</i></div>
                        <div class="inline">Мої предмети</div>
                    </a>
                </li>
            </sec:authorize>
            <sec:authorize access="hasAuthority('ADMIN')">
                <li>
                    <a href="<%=root%>users?page=1">
                        <div class="inline"><i class='material-icons'>groups</i></div>
                        <div class="inline">Користувачі</div>
                    </a>
                </li>
                <li>
                    <a href="<%=root%>subject-details?page=1">
                        <div class="inline"><i class='material-icons'>import_contacts</i></div>
                        <div class="inline">Деталі предметів</div>
                    </a>
                </li>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <li class="logout">
                    <a href="<%=root%>user/<%=currUser.getId()%>" class="username">
                        <div class="inline">
                            <img
                                alt="avatar"
                                src='<%=root + (currUser.getPhoto() != null ? currUser.getPhoto() : "images/user.png")%>'
                                class="avatar-30"
                            >
                        </div>
                        <div class="inline"><%=" " + currUser.getUsername() + " "%></div>
                    </a>
                    <a>
                        <form action="<%=root%>logout" method=post>
                            <sec:csrfInput />
                            <button type="submit" class="bg-primary">
                                <div class="inline"><i class='material-icons'>logout</i></div>
                                <div class="inline">Вихід</div>
                            </button>
                        </form>
                    </a>
                </li>
            </sec:authorize>
            <sec:authorize access="!isAuthenticated()">
                <li class="logout">
                    <a href="<%=root%>login">
                        <div class="inline"><i class='material-icons'>login</i></div>
                        <div class="inline">Вхід</div>
                    </a>
                    <a href="<%=root%>registration">
                        <div class="inline"><i class='material-icons'>app_registration</i></div>
                        <div class="inline">Реєстрація</div>
                    </a>
                </li>
            </sec:authorize>
        </ul>
    </nav>
    <br/>
    <br/>
</header>
