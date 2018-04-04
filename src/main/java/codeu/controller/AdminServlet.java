
package codeu.controller;

import org.mindrot.jbcrypt.*;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for the login page. */
public class AdminServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /**
   * Set up state for handling login-related requests. This method is only called when running in a
   * server, not when running in a test.
   */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * This function fires when a user requests the /adminview URL. It checks whether the user is an admin
   * If they are, they will be forwarded to adminview.jsp, if not
   * they are forwarded to conversations.jsp
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String requestURL = request.getRequestURI();
    String userName = requestURL.substring("/adminview/".length());
    User user = userStore.getUser(userName);
    if (!user.getIsAdmin()) {
      
      System.out.println("user not admin");
      response.sendRedirect("/conversations");
    }

    request.getRequestDispatcher("/WEB-INF/view/adminview.jsp").forward(request, response);
  }

  /**
   * This function fires when a user submits the login form. It gets the username and password from
   * the submitted form data, checks that they're valid, and either adds the user to the session
   * so we know the user is logged in or shows an error to the user.
   */
   @Override
 public void doPost(HttpServletRequest request, HttpServletResponse response)
     throws IOException, ServletException {

  }

}
