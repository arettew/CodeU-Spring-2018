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

package codeu.model.data;

import java.time.Instant;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class UserTest {

  @Test
  public void testCreate() {
    UUID id = UUID.randomUUID();
    String name = "test_username";
    Instant creation = Instant.now();
    String password = "password";
    String about = "Hi! I'm test_username!";
    boolean isAdmin = false;

    User user = new User(id, name, password, about, creation, isAdmin);

    Assert.assertEquals(id, user.getId());
    Assert.assertEquals(name, user.getName());
    Assert.assertEquals(password, user.getPassword());
    Assert.assertEquals(about, user.getAbout());
    Assert.assertEquals("Hi! I'm test_username!", user.getAbout());
    Assert.assertEquals(true, user.getAllowMessageDel());
    Assert.assertEquals(0, user.getMessagesSent());
    Assert.assertEquals(creation, user.getCreationTime());
    Assert.assertEquals(isAdmin, user.getIsAdmin());
  }

  @Test
  public void testAltCreate() {
    UUID id = UUID.randomUUID();
    String name = "test_username";
    String about = "Hi! I'm test_username!";
    Instant creation = Instant.now();
    String password = "password";
    boolean isAdmin = false;
    boolean allowMessageDel = false;
    int messagesSent = 10;
    Map<UUID, Boolean> conversationVisibilities = new HashMap();

    User user = new User(id, name, password, about, isAdmin, allowMessageDel, messagesSent, creation,
                         conversationVisibilities);

    Assert.assertEquals(id, user.getId());
    Assert.assertEquals(name, user.getName());
    Assert.assertEquals(password, user.getPassword());
    Assert.assertEquals(about, user.getAbout());
    Assert.assertEquals(creation, user.getCreationTime());
    Assert.assertEquals(isAdmin, user.getIsAdmin());
  }

  @Test
  public void changeAdminStatus() {
    UUID id= UUID.randomUUID();
    String name = "test_username";
    Instant creation = Instant.now();
    String about = "about";
    String password = "password";
    boolean isAdmin = false;

    User user = new User(id, name, password, about, creation, isAdmin);
    user.invertAdminStatus();

    Assert.assertEquals(true, user.getIsAdmin());
    Assert.assertEquals("unique message", user.getAbout());
    Assert.assertEquals(allowMessageDel, user.getAllowMessageDel());
    Assert.assertEquals(messagesSent, user.getMessagesSent());
    Assert.assertEquals(creation, user.getCreationTime());
    Assert.assertEquals(conversationVisibilities, user.getConversations());
  }

  @Test
  public void changeElements() {
    UUID id= UUID.randomUUID();
    String name = "test_username";
    Instant creation = Instant.now();
    String about = "about";
    String password = "password";
    boolean isAdmin = false;
    boolean allowMessageDel = false;


    User user = new User(id, name, password, about, isAdmin, creation, allowMessageDel);
    user.setAbout("new_message");
    user.incMessagesSent();
    user.setAllowMessageDel(false);

    Assert.assertEquals("new_message", user.getAbout());
    Assert.assertEquals(user.getAbout(), "new_message");
    Assert.assertEquals(user.getMessagesSent(), 1);
    Assert.assertEquals(user.getAllowMessageDel(), allowMessageDel);
  }
}
