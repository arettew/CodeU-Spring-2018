package codeu.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.UUID;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.time.Instant;
import org.junit.Assert;

public class ProfileServletTest {
  private ProfileServlet profileServlet;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private HttpSession mockSession;
  private RequestDispatcher mockRequestDispatcher; 
  private UserStore mockUserStore; 

  @Before
  public void setup() throws IOException {
    profileServlet = new ProfileServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockUserStore = Mockito.mock(UserStore.class);
    profileServlet.setUserStore(mockUserStore);
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/test_user");

    UUID fakeUserID = UUID.randomUUID();
    User fakeUser = new User(fakeUserID, "test_user", "password", Instant.now(), false);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(fakeUser);

    profileServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("profileOwner", "test_user");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  } 

  @Test
  public void badUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/bad_user");

    Mockito.when(mockUserStore.getUser("bad_user")).thenReturn(null);

    profileServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }

  @Test
  public void testDoPost() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/test_user");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_user");

    UUID fakeUserID = UUID.randomUUID();
    User fakeUser = new User(fakeUserID, "test_user", "password", Instant.now(), false);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(fakeUser);

    Mockito.when(mockRequest.getParameter("about")).thenReturn("new_message");
    Mockito.when(mockRequest.getParameter("whichForm")).thenReturn("about");

    profileServlet.doPost(mockRequest, mockResponse);

    Assert.assertEquals(fakeUser.getAbout(), "new_message");
    Mockito.verify(mockResponse).sendRedirect("/profile/test_user");
  }

  @Test
  public void testDoPost_noMatch() throws IOException, ServletException {
    Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/test_user");
    Mockito.when(mockSession.getAttribute("user")).thenReturn("bad_user");

    UUID fakeUserID = UUID.randomUUID();
    User fakeUser = new User(fakeUserID, "test_user", "password", Instant.now(), false);
    Mockito.when(mockUserStore.getUser("test_user")).thenReturn(fakeUser);

    Mockito.when(mockRequest.getParameter("about")).thenReturn("new_message");
    Mockito.when(mockRequest.getParameter("whichForm")).thenReturn("about");

    profileServlet.doPost(mockRequest, mockResponse);

    Assert.assertEquals(fakeUser.getAbout(), "Hi! I'm test_user!");
    Mockito.verify(mockResponse).sendRedirect("/profile/test_user");
  }
}