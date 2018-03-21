package codeu.controller;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet class responsible for user Registration
 */

public class RegisterServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws IOException, ServletException {

    response.getWriter().println("<h1>RegisterServlet GET request.</h1>");

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

    User user = new User(UUID.randomUUID(), username, passwordHash, Instant.now());
    userStore.addUser(user);
  }
}
