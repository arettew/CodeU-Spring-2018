<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.UUID"%>
<%
String profileOwnerName = (String) request.getAttribute("profileOwner");
UUID profileOwnerId = (UUID) request.getAttribute("OwnerID");
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
    #messages {
      background-color: white;
      height: 500px;
      overflow-y: scroll;
    }
  </style>

  <script>
    // scroll the messages div to the bottom
    function scrollMessage() {
      var messageDiv = document.getElementById('messages');
      messageDiv.scrollTop = messageDiv.scrollHeight;
    };
  </script>
</head>

<body onload="scrollMessage()"> 

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
    <% } %>
    <h2><%= profileOwnerName %>'s sent messages</h2>
    <div id="messages">
      <ul>
    <% 
         List<Message> user_messages = MessageStore.getInstance()
                                         .getMessagesByOwner(profileOwnerId); 
    %>
    <% for (Message message : user_messages) { %>
    <%   if(message.getContent() != null && !message.getContent().isEmpty()) { %>
          <li> <b> <%= message.getCreationTime() %> </b>: <%= message.getContent() %> </li>
    <%   } %>      
    <% } %>
      </ul>
    </div>
  </div>

</body>
</html>
