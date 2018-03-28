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
    Boolean isAdmin = false;

    User user = new User(id, name, password, about, creation, isAdmin);

    Assert.assertEquals(id, user.getId());
    Assert.assertEquals(name, user.getName());
    Assert.assertEquals(password, user.getPassword());
    Assert.assertEquals(about, user.getAbout());
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
    Boolean isAdmin = false;

    User user = new User(id, name, password, about, creation, isAdmin);

    Assert.assertEquals(id, user.getId());
    Assert.assertEquals(name, user.getName());
    Assert.assertEquals(password, user.getPassword());
    Assert.assertEquals(about, user.getAbout());
    Assert.assertEquals(creation, user.getCreationTime());
    Assert.assertEquals(isAdmin, user.getIsAdmin());
  }

  @Test
  public void changeAbout() {
    UUID id= UUID.randomUUID();
    String name = "test_username";
    Instant creation = Instant.now();
    String about = "about";
    String password = "password";
    Boolean isAdmin = false;

    User user = new User(id, name, password, about, creation, isAdmin);
    user.setAbout("new_message");

    Assert.assertEquals("new_message", user.getAbout());
  }
}
