<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%
String profileOwnerName = (String) request.getAttribute("profileOwner");
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
    <a id="navTitle" href = "/">CodeU Chat App</a>
    <a href="/conversations">Conversation</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
    <% } %>
    <a href="/about.jsp">About</a>
  </nav>

  <div id="container">
    <h1><%= profileOwnerName %>'s Profile Page</h1>
    <hr>
    
    <h2>About <%= profileOwnerName %></h2>
    <% String about = 
      UserStore.getInstance().getUser(profileOwnerName).getAbout(); %>
    <% if(about == null){ %>
      <p>Hello I'm <%= profileOwnerName %>!</p>
    <% } else{ %>
      <p><%= about %></a>
    <% } %>

    <% if (profileOwnerName.equals(request.getSession().getAttribute("user"))) { %>
      <h3>Edit your about me. Only you can see this.</h3>
      <form action="/profile/<%= profileOwnerName%>" method="POST">
        <input type="text" name="about">
        <br/>
        <button type="submit">Submit</button>
      </form>

      <p> Would you like to allow your messages to be deleted after you've hit a high amount?</p>
      <form action="/profile/<%= profileOwnerName%>" method="POST">
        <% Boolean allowMessageDel = 
          UserStore.getInstance().getUser(profileOwnerName).getAllowMessageDel(); %>
        <input type="radio" name="delete" value="yes" 
          <%if(allowMessageDel) { %> 
            checked
            <% } %> 
        >
        Yes<br>
        <input type="radio" name="delete" value="no"
          <%if(!allowMessageDel) { %> 
            checked
            <% } %> 
        >
        No<br>
        <button type="submit">Submit</button>
      </form>
    <% } %>
  </div>

  <div id="container">
    <h1>Messages will go here?</h1>
  </div>


</body>
</html>