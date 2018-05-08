// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

// import org.mindrot.jbcrypt.*;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.data.Message;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.persistence.PersistentDataStoreException;
import codeu.controller.ServletUrlStrings;
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
public class LeaderboardServlet extends HttpServlet {

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

  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * This function fires when a user navigates to the leaderboard page, calls the
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    int numUsers = userStore.getNumUsers();
    String newestUser = "";
    String wordiestUser = "";
    try{
      newestUser = getNewestUsers(1).get(0).getName();
      wordiestUser = getWordiestUsers(1).get(0).getName();
    } catch(PersistentDataStoreException p){

    }

    request.setAttribute("numUsers", numUsers);
    request.setAttribute("newestUser", newestUser);
    request.setAttribute("wordiestUser", wordiestUser);
    request.getRequestDispatcher(ServletUrlStrings.leadershipJsp).forward(request, response);
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
