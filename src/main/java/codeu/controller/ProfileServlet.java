package codeu.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import java.util.UUID;
import java.util.Base64.Encoder;
import java.util.Base64;
import java.util.Vector;
import java.lang.IllegalArgumentException;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.appengine.api.images;

/**
  * Servlet class responsible for user profile pages
  */
@MultipartConfig
public class ProfileServlet extends HttpServlet {
  
  /** Store class that gives access to users */
  private UserStore userStore;

  /** Constant strings that describe the request for the DoPost function */
  private static final String REQUEST_ABOUT = "about";
  private static final String REQUEST_HIDDEN = "hidden";
  private static final String REQUEST_RESET = "reset";
  private static final String REQUEST_MESSAGEDELETION = "messageDeletion";

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
      request.setAttribute("ownerId", user.getId());
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
        //  User is not logged in. Don't let them change any element of the profile 
        response.sendRedirect("/login");
        return;
      }

      if (!userName.equals(ownerName)) {
        //  This is not the user's profile. Don't let them change any element of the profile
        response.sendRedirect("/profile/" + ownerName);
        return;
      } 

      //  If whichform is null, it means the profile pic got uploaded because servlets can't handle
      //  multipart form data the same way.
      if (request.getParameter("whichForm") != null) {

        // The parameter whichForm from profile.jsp determines which form was submitted. This is 
        // helpful to handle each post request differently.
        switch (request.getParameter("whichForm")) {
          
          case REQUEST_ABOUT:
            //About message was posted
            String aboutMessage = request.getParameter("about");

            break;

          case REQUEST_HIDDEN:
            //Conversation to hide was posted
            UUID conversationToHide = UUID.fromString(request.getParameter("convToHide"));
            owner.hideConversation(conversationToHide);

            break;
            
          case REQUEST_MESSAGEDELETION:
            //  The user wants to change whether or not their messages will be deleted
            String delete = request.getParameter("delete");

            boolean allowMessageDel = (delete.equals("yes"));
            owner.setAllowMessageDel(allowMessageDel);

            break;

          case REQUEST_RESET:
            //User wants to show all their conversations again
            owner.showAllConversations();

            break;

        }

      } else {
          //  User wants to upload a profile picture
          Part filePart = request.getPart("picture");
          InputStream fileContent = filePart.getInputStream();
          Image image = readImage(fileContent, filePart);
          owner.setImage(image);
      }

      // Updates info before refreshing
      userStore.updateUser(owner);

      //  Redirect to a GET request
      response.sendRedirect("/profile/" + ownerName);
    }

  //  Helper function which uses an inputstream to read the image bytes and returns an image
  private String readImage(InputStream fileContent, Part filePart) {
    String image = null;

    try{
      // Reading the bytes from the File
      byte[] inputBytes = new byte[(int)filePart.getSize()];
      fileContent.read(inputBytes);

      image = makeImage(inputBytes);

      return image;

    } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return null;
    }
  }

}
