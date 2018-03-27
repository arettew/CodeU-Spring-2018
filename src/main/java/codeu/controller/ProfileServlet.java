package codeu.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
  * Servlet class responsible for user profile pages
  */

public class ProfileServlet extends HttpServlet {
  
  /** Store class that gives access to users */
  private UserStore userStore;

  /** Set up state for handling profile requests. */
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
   * This function fires when a user navigates to the profile page. It gets the profile title from
   * the URL and finds the corresponding User. It then forwards to chat.jsp for rendering if the 
   * user is valid. 
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
      String requestURL = request.getRequestURI();
      String userName = requestURL.substring("/profile/".length());
      User user = userStore.getUser(userName);
      if (user == null) {
        // couldn't find user. redirect to conversations
        System.out.println("user was null");
        response.sendRedirect("/conversations");
        return;
      }

      request.setAttribute("profileOwner", user.getName());
      request.setAttribute("OwnerID", user.getId());
      request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
    }

  /**
   * This function fires when a user clicks the submit button to edit their About Me message. It gets 
   * logged in username from the session and the message from the form data. It cleans the message, changes
   * the message in User data type, and then redirects back to the profile page. 
   */ 
  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

      String requestURL = request.getRequestURI();
      String ownerName = requestURL.substring("/profile/".length());
      String userName = (String) request.getSession().getAttribute("user");
      User owner = userStore.getUser(ownerName);

      if (userName == null) {
        //  User is not logged in. Don't let them edit the message
        response.sendRedirect("/login");
        return;
      }

      if (!userName.equals(ownerName)) {
        //  This is not the users profile. Don't let them edit the message
        response.sendRedirect("/profile/" + ownerName);
        return;
      } 

      String aboutMessage = request.getParameter("about");

      //  This cleans the message of HTML
      String cleanedAboutMessage = Jsoup.clean(aboutMessage, Whitelist.none());
      owner.setAbout(cleanedAboutMessage);

      // TODO: Make sure that the change to the message is properly stored
      userStore.updateUser(owner);

      //  Redirect to a GET request
      response.sendRedirect("/profile/" + ownerName);
    } 

}
