<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.UUID"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
String profileOwnerName = (String) request.getAttribute("profileOwner");
UUID profileOwnerId = (UUID) request.getAttribute("ownerId");
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
        <input type="hidden" name="whichForm" value ="about">
        <br/>
        <button type="submit">Submit</button>
      </form>
    <% } %>

    <!--This is where the user's sent messages will show up -->
    <h2><%= profileOwnerName %>'s sent messages</h2>
    <div id="messages">
      <ul>
    <% //This list contains messages written by the profile owner in order sorted by time
       List<Message> userMessages = MessageStore.getInstance().getMessagesByAuthor(profileOwnerId);
    %>

    <% for (Message message : userMessages) { %>
    <%   // If the message is not empty, then print it %>
    <%   if(message.getContent() != null && !message.getContent().isEmpty()) { %>
          <li> <b> <%= formatter.format(Date.from(message.getCreationTime()))  %> 
          </b>: <%= message.getContent() %> </li>
    <%   } %>      
    <% }   %>

      </ul>
    </div>

    <!-- This is where the user's non private conversations will be listed -->
    <h2> <%= profileOwnerName %>'s conversations </h2>
    <div id ="conversations">
      
    <% if (profileOwnerName.equals(request.getSession().getAttribute("user"))) { %>
        <form action="/profile/<%= profileOwnerName%>" method="POST">
          <input type="hidden" name="whichForm" value ="reset">
          <button type="submit">Show All Conversations</button>
        </form>
    <%  } %>      

      <ul>
    <% //This map contains the conversations where the profile owner has participated
       User profileOwner = UserStore.getInstance().getUser(profileOwnerName);
       LinkedHashMap<UUID, Boolean> userConversations = profileOwner.getConversations();
       Set<UUID> keys = userConversations.keySet();
    %>

    <% for (UUID key: keys) { %>
    <%  if (userConversations.get(key)) { %>
    <%    Conversation conversation = ConversationStore.getInstance().getConversationWithId(key);%>
    <%    if (profileOwnerName.equals(request.getSession().getAttribute("user"))) { %>
            <li><a href="../chat/<%=conversation.getTitle()%>"> <%= conversation.getTitle() %> </a>
              <form action="/profile/<%= profileOwnerName%>" method="POST">
                <input type="hidden" name="whichForm" value ="hidden">
                <button type="submit" name="convToHide" value="<%= key %>">Hide</button>
              </form>
            </li>
    <%    } %>
    <%  }   %>  
    <% }    %>

    </ul>
  </div>

</body>
</html>
