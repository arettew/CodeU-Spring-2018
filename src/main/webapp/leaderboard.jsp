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
    <a href="/leaderboard.jsp">Leaderboard</a>
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>Leaderboard</h1>
      <p>
        <strong>Site Statistics</strong>
        Here are some site stats:
      </p>

      <ul>
        <li><strong>Most Active User:</strong> (hard coded) rebecca </li>
        <li><strong>Newest User:</strong> (hard coded) gautham </li>
        <li><strong>Wordiest User:</strong> (hard coded) andres </li>
      </ul>
    </div>
  </div>
</body>
</html>