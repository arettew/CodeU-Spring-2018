package codeu.controller;

import org.mindrot.jbcrypt.BCrypt;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.data.Message;
import codeu.model.store.basic.MessageStore;
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
  private MessageStore messageStore;

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

  void setMessageStore(MessageStore messageStore){
    this.messageStore = messageStore;
  }

  /**
   * This function fires when a user requests the /login URL. It simply forwards the request to
   * login.jsp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    request.getRequestDispatcher("/WEB-INF/view/admin.jsp").forward(request, response);
  }

  /**
   * This function fires when a user submits the login form. It gets the username and password from
   * the submitted form data, checks that they're valid, and either adds the user to the session
   * so we know the user is logged in or shows an error to the user.
   */
   @Override
 public void doPost(HttpServletRequest request, HttpServletResponse response)
     throws IOException, ServletException {
       /* fill in when admin.jsp is made */
 }

 public List<User> mostActiveUsers(int x) throws PersistentDataStoreException {
   List<UUID> userIds = userStore.getUsers().stream().map(User::getId).collect(Collectors.toList());
   Map<UUID, Integer> numberOfMessagesByUserId = userIds.stream()
      .collect(Collectors.toMap(Function.identity(), userId -> (messageStore.getMessagesByUserId(userId)).size()));
   List<User> sortedUsers = numberOfMessagesByUserId.entrySet().stream()
      .sorted(Map.Entry.comparingByValue())
      .sorted(Collections.reverseOrder())
      .map(Map.Entry::getKey)
      .map(userId -> userStore.getUser(userId))
      .limit(x)
      .collect(Collectors.toList());
    return sortedUsers;
 }

 public List<User> newestUsers(int x) throws PersistentDataStoreException {
   List<User> newestUsers = userStore.getUsers().stream()
      .sorted(Collections.reverseOrder())
      .limit(x)
      .collect(Collectors.toList());
   return newestUsers;
 }

 public List<User> wordiestUsers(int x) throws PersistentDataStoreException {
   List<UUID> userIds = userStore.getUsers().stream().map(User::getId).collect(Collectors.toList());
   Map<UUID, Integer> numberOfWordsByUserId = userIds.stream()
      .collect(Collectors.toMap(Function.identity(), userId ->
      messageStore.getMessagesByUserId(userId).stream()
        .mapToInt(m -> m.getWords())
        .sum()));
   List<User> sortedUsers = numberOfWordsByUserId.entrySet().stream()
      .sorted(Map.Entry.comparingByValue())
      .sorted(Collections.reverseOrder())
      .map(Map.Entry::getKey)
      .map(userId -> userStore.getUser(userId))
      .limit(x)
      .collect(Collectors.toList());
    return sortedUsers;
 }

}
