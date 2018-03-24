<%@ page import="codeu.model.data.User" %>
<%
User user = (User) request.getAttribute("userProfile");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Profile</title>
  <link rel="stylesheet" href="/css/main.css">
  <style>
    label {
      display: inline-block;
      width: 100px;
    }
  </style>
</head>
<body>
  
  <nav>
    <a id="navTitle" href = "/">CodeU Chatt App</a>
    <a href="/conversations">Conversation</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
    <% } %>
    <a href="/about">About</a>
  </nav>

  <div id="container">
    <h1><%= user.getName() %>'s Profile Page</h1>
  </div>


</body>
</html>