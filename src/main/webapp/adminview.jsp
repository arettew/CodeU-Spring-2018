<!DOCTYPE html>
<html>
<head>
  <title>CodeU Chat App</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>Admin Page</h1>
      <p>
        <strong>Site Statistics</strong>
        Here are some site stats:
      </p>
      <!-- TO:DO (rchen): link backend --> 
      <ul>
      	<li><strong>Users:</strong> (hard coded) 42 </li>
      	<li><strong>Conversations:</strong> (hard coded) 127 </li>
      	<li><strong>Messages:</strong> (hard coded) 1,337 </li>
        <li><strong>Most Active User:</strong> (hard coded) rebecca </li>
        <li><strong>Newest User:</strong> (hard coded) gautham </li>
        <li><strong>Wordiest User:</strong> (hard coded) andres </li>
      </ul>
    </div>
  </div>
</body>
</html>