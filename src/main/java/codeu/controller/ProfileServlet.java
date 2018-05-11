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
import java.util.Set;
import java.lang.IllegalArgumentException;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

/**
  * Servlet class responsible for user profile pages
  */
@MultipartConfig
public class ProfileServlet extends HttpServlet {
  
  /** Store class that gives access to users */
  private UserStore userStore;

  /** Image Factory to create images and help with image resizing*/
  private ImagesServiceFactory imageFactory;

  /** Image service to call transforms on images */
  private ImagesService imageService = imageFactory.getImagesService();

  /** Transform to be used when resizing an image */
  private final Transform imageResize = imageFactory.makeResize(300, 300);

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

      //  This boolean will be true if the user changes anything. If it isn't true, the user doesn't
      //  need to be updated
      boolean fieldUpdated = false;

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

      //  This if statement checks whether the form submitted is a multipart form. The only 
      //  multipart form is the profile picture submission.
      if (request.getContentType() != null && 
          request.getContentType().toLowerCase().startsWith("multipart/form-data")) {

        Part filePart = request.getPart("picture");
        InputStream fileContent = filePart.getInputStream();
        byte[] imageData = readImage(fileContent, filePart);

        //  Checking if the imageData is empty
        if (imageData.length != 0) {
          byte[] resizedImageData = resizeImage(imageData);
          owner.setImageData(resizedImageData);

          //  Updating the user right away and later returning, as nothing else needs to be updated
          userStore.updateUser(owner);
          //  Redirect to a GET request
          response.sendRedirect("/profile/" + ownerName); 
        }

        return;
      }

      //  Checking if the user wrote a new about message or left the field blank
      String aboutMessage = request.getParameter("about");
      if (aboutMessage != null && !aboutMessage.isEmpty()) {

        //  This cleans the message of HTML
        String cleanedAboutMessage = Jsoup.clean(aboutMessage, Whitelist.none());
        owner.setAbout(cleanedAboutMessage);

        fieldUpdated = true;
      }

      //  The user wants to change whether or not their messages will be deleted
      if (request.getParameter("delete") != null) {

        //  This statement checks whether the parameter was already true so it can be determined
        //  that nothing was really updated
        if (!owner.getAllowMessageDel()) {
          fieldUpdated = true;
        }
        owner.setAllowMessageDel(true);

      } else {

        if (!owner.getAllowMessageDel()) {
          fieldUpdated = true;
        }
        owner.setAllowMessageDel(false);

      }

      //  Checking if the user wants to show all of their conversations 
      if (request.getParameter("showAllConvs") != null) {

        if (!owner.getShowAllConversations()) {
          fieldUpdated = true;
        }
        owner.showAllConversations(true);

      } else {

        if (owner.getShowAllConversations()) {
          fieldUpdated = true;
        }
        owner.showAllConversations(false);

      }

      //  Set of conversations the user has participated in
      Set<UUID> conversations = owner.getConversations().keySet();

      //  The servlet won't know what parameter to get to check which conversations the user wants
      //  to hide. The following code loops through all the conversations as if they were 
      //  parameter names to see which ones were sent.
      for (UUID conversationId : conversations) {
        if (request.getParameter(conversationId.toString()) != null) {
          owner.hideConversation(conversationId);
          fieldUpdated = true;
        }
      }

      //  Updates info before refreshing
      if (fieldUpdated) {
        userStore.updateUser(owner);
      }

      //  Redirect to a GET request
      response.sendRedirect("/profile/" + ownerName);
    }

  //  Helper function which uses an inputstream to read the image bytes and returns an image
  private byte[] readImage(InputStream fileContent, Part filePart) {

    try {
      //  Reading the bytes from the File
      byte[] inputBytes = new byte[(int)filePart.getSize()];
      fileContent.read(inputBytes);

      return inputBytes;

    } catch (IOException | IllegalArgumentException ex) {
      throw new RuntimeException("Unable to read image", ex);
    }
  }

  //  Helper function to resize an image
  private byte[] resizeImage(byte[] inputImageData) {
    Image currentImage = imageFactory.makeImage(inputImageData);
    Image newImage = imageService.applyTransform(imageResize, currentImage);
    return newImage.getImageData();
  }

}
