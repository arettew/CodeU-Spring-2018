
package codeu.controller;

import org.mindrot.jbcrypt.*;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.controller.ServletUrlStrings;
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
  private ConversationStore conversationStore;
  private MessageStore messageStore;
  public static final String ADMIN_URL = "/adminView/";

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
    String userName = requestURL.substring(ADMIN_URL.length());
    User user = userStore.getUser(userName);
    if (!user.getIsAdmin()) {
      System.out.println("user not admin");
      response.sendRedirect("/conversations");
    }

    int numMessages = messageStore.getNumMessages();
    int numConversations = conversationStore.getNumConversations();
    int numUsers = userStore.getNumUsers();

    request.setAttribute("numMessages", numMessages);
    request.setAttribute("numConversations", numConversations);
    request.setAttribute("numUsers", numUsers);

    request.getRequestDispatcher(ServletUrlStrings.adminViewJsp).forward(request, response);
  }

}
