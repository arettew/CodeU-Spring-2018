
package codeu.controller;

import org.mindrot.jbcrypt.BCrypt;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.data.Message;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.controller.ServletUrlStrings;
import codeu.model.store.persistence.PersistentDataStoreException;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.*;
import java.util.function.*;


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
    setMessageStore(MessageStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * This function fires when a user requests the /adminview URL. It checks whether the user is an admin
   * If they are, they will be forwarded to adminview.jsp, if not
   * they are forwarded to conversations.jsp
   * This function fires when a user requests the /login URL. It simply forwards the request to
   * login.jsp.
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

    int numUsers = userStore.getNumUsers();
    int numConversations = conversationStore.getNumConversations();
    int numMessages = messageStore.getNumMessages();
    String mostActiveUser = "";
    String newestUser = "";
    String wordiestUser = "";
    try{
      mostActiveUser = getMostActiveUsers(1).get(0).getName();
      newestUser = getNewestUsers(1).get(0).getName();
      wordiestUser = getWordiestUsers(1).get(0).getName();
    } catch(PersistentDataStoreException p){

    }

    request.setAttribute("numUsers", numUsers);
    request.setAttribute("numConversations", numConversations);
    request.setAttribute("numMessages", numMessages);
    request.setAttribute("mostActiveUser", mostActiveUser);
    request.setAttribute("newestUser", newestUser);
    request.setAttribute("wordiestUser", wordiestUser);

    request.getRequestDispatcher(ServletUrlStrings.adminViewJsp).forward(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String username = request.getParameter("username");

    if (userStore.isUserRegistered(username)) {
      User user = userStore.getUser(username);
      userStore.deleteUser(user);
    }
  }

  public List<User> getMostActiveUsers(int x) throws PersistentDataStoreException {
    List<UUID> userIds = userStore.getUsers().stream().map(User::getId).collect(Collectors.toList());
    Map<UUID, Integer> numberOfMessagesByUserId = userIds.stream()
      .collect(Collectors.toMap(Function.identity(), userId -> (messageStore.getMessagesByUserId(userId)).size()));
    return numberOfMessagesByUserId.entrySet().stream()
      .sorted(Map.Entry.comparingByValue())
      .sorted(Collections.reverseOrder())
      .map(Map.Entry::getKey)
      .map(userId -> userStore.getUser(userId))
      .limit(x)
      .collect(Collectors.toList());
  }

  public List<User> getNewestUsers(int x) throws PersistentDataStoreException {
    return userStore.getUsers().stream()
      .sorted(Collections.reverseOrder())
      .limit(x)
      .collect(Collectors.toList());
  }

  public List<User> getWordiestUsers(int x) throws PersistentDataStoreException {
    List<UUID> userIds = userStore.getUsers().stream().map(User::getId).collect(Collectors.toList());
    Map<UUID, Integer> numberOfWordsByUserId = userIds.stream()
      .collect(Collectors.toMap(Function.identity(), userId ->
      messageStore.getMessagesByUserId(userId).stream()
        .mapToInt(Message::getWords)
        .sum()));
    return numberOfWordsByUserId.entrySet().stream()
      .sorted(Map.Entry.comparingByValue())
      .sorted(Collections.reverseOrder())
      .map(Map.Entry::getKey)
      .map(userId -> userStore.getUser(userId))
      .limit(x)
      .collect(Collectors.toList());
  }

}
