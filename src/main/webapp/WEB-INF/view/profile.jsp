<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.UUID"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Queue"%>
<%@ page import="java.util.PriorityQueue"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
String profileOwnerName = (String) request.getAttribute("profileOwner");
UUID profileOwnerId = (UUID) request.getAttribute("OwnerID");
SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
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

    <!--This is where the user's sent messages will show up -->
    <div id="messages">
      <ul>
    <% //This queue acts as a priority queue. Everytime you pop, the oldest message is returned
       Queue<Message> user_messages = MessageStore.getInstance()
                                         .getMessagesByOwner(profileOwnerId); 
    %>
    <% int initial_size = user_messages.size(); %>
    <% for (int i = 0; i < initial_size; ++i) { %>
    <%   // If the message is not empty, then print it %>
    <%   if(user_messages.peek().getContent() != null && 
                                              !user_messages.peek().getContent().isEmpty()) { %>
          <li> <b> <%= formatter.format(Date.from(user_messages.peek().getCreationTime())) %> 
          </b>: <%= user_messages.peek().getContent() %> </li>
    <%   } %>      
    <%   user_messages.remove(); %>
    <% } %>
      </ul>
    </div>
  </div>

</body>
</html>
