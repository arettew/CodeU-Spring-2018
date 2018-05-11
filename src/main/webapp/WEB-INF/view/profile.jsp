<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.UUID"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
String profileOwnerName = (String) request.getAttribute("profileOwner");
User profileOwner = UserStore.getInstance().getUser(profileOwnerName);
UUID profileOwnerId = (UUID) request.getAttribute("ownerId");
SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
boolean isOwner = profileOwnerName.equals(request.getSession().getAttribute("user"));
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

    <% String base64Image = UserStore.getInstance().getUser(profileOwnerName).getEncodedImage(); %>
    <% if(base64Image != null) { %>
    <%  String format = "data:image/*;base64, "; %>
        <img src="<%= format + base64Image %>" alt="<%= profileOwnerName%>" />
    <% } %>
    <% if (isOwner) { %>

      <h3> Update your Profile Picture: </h3>
      <form action ="/profile/<%= profileOwnerName%>" method = "POST" 
            enctype ="multipart/form-data">
        <input type ="file" name ="picture" accept="image/*" />
        <button type="submit"> Update </button>
        <br />
      </form>

    <% } %>

        <h2>About <%= profileOwnerName %></h2>
        <% String about = profileOwner.getAbout(); %>
        <% if (about == null) { %>
            <p>Hello I'm <%= profileOwnerName %>!</p>
        <% } else { %>
            <p><%= about %></p>
        <% } %>

        <% if (isOwner) { %>
          <h3>Edit your about me. Only you can see this.</h3>
            <form action ="/profile/<%= profileOwnerName%>" method = "POST" >
              <input type="text" name="about">
              <br/>

          <p> Would you like to allow your messages to be deleted after you've hit a high amount?
          </p>
            <% boolean allowMessageDel = 
              UserStore.getInstance().getUser(profileOwnerName).getAllowMessageDel(); %>
            <input type="checkbox" name="delete" 
              <%if(allowMessageDel) { %> 
                checked
                <% } %> 
            >
            Allow Message Deletion<br>
        <% } %>

        <!--This is where the user's sent messages will show up -->
        <h2><%= profileOwnerName %>'s sent messages</h2>
        <div id="messages">
          <ul>
        <% //This list contains messages written by the profile owner in order sorted by time
           List<Message> userMessages = 
           MessageStore.getInstance().getMessagesByAuthor(profileOwnerId);

           ConversationStore conversationStore = ConversationStore.getInstance();
        %>

        <% for (Message message : userMessages) { %>
        <%   // If the message is not empty, then print it %>
        <%   if(message.getContent() != null && !message.getContent().isEmpty()) { %>
              <li> <b> <%= formatter.format(Date.from(message.getCreationTime()))  %> 
              </b>: <a href=<%= "/chat/" 
                    + conversationStore.getConversationById(message.getConversationId())
                      .getTitle() %> > 
                <%= message.getContent() %> </a> </li>
        <%   } %>      
        <% }   %>

          </ul>
        </div>

        <!-- This is where the user's non private conversations will be listed -->
        <h2> <%= profileOwnerName %>'s conversations </h2>
        <div id ="conversations">
          
        <% if (isOwner) { %>
              <input type="checkbox" name="showAllConvs"
              <%if(profileOwner.getShowAllConversations()) { %> 
                checked
                <% } %> 
              > 
              Display All Conversations <br>
        <%  } %>      

          <ul>
        <% //This map contains the conversations where the profile owner has participated
           Map<UUID, Boolean> userConversations = profileOwner.getConversations();
           Set<UUID> uuids = userConversations.keySet();
           boolean noConversations = true;
        %>

        <% 
        for (UUID key: uuids) { 
          if (profileOwner.getShowAllConversations() || userConversations.get(key)) {       
            noConversations = false;                                                        
            Conversation conversation = 
            ConversationStore.getInstance().getConversationById(key);
        %>
            <li><a href="../chat/<%=conversation.getTitle()%>"> <%= conversation.getTitle() %>
                </a>
        <%    if (isOwner) { %>
                    <input type="checkbox" name="<%=key%>" value="<%=key%>">
                    Hide Conversation <br>
                </li>
        <%    } 
            }     
          }    
        %>

        <% if (noConversations) { %>
             <p> Oops! <%= profileOwnerName %>'s conversation list is empty! </p> <br>
        <% } %>

        <% if(isOwner) { %>
             <button type="submit"> Update Profile </button>
        <% } %>

          </form>
        </ul>
      </div>

  </body>
  </html>
