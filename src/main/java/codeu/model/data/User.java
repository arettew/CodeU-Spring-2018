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

/** Class representing a registered user. */
public class User {
  private final UUID id;
  private final String name;
  private final String password;
  private String about; 
  private Boolean allowMessageDel;
  private final Instant creation;

  /**
   * Constructs a new User.
   *
   * @param id the ID of this User
   * @param name the username of this User
   * @param password the password of this User
   * @param about the about me message of this User 
   * @param allowMessageDel does this User want messages deleted?
   * @param creation the creation time of this User
   */
  public User(UUID id, String name, String password, String about, Boolean delete, Instant creation) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.about = about;
    this.allowMessageDel = delete;
    this.creation = creation;
  }

  /**
   * Constructs a new User.
   *
   * @param id the ID of this User
   * @param name the username of this User
   * @param password the password of this User
   * @param about the about me message of this User 
   * @param allowMessageDel does this User want messages deleted?
   * @param creation the creation time of this User
   */
   public User(UUID id, String name, String password, Instant creation) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.about = "Hi! I'm " + name + "!";
    this.allowMessageDel = true;
    this.creation = creation;
  }

  /** Returns the ID of this User. */
  public UUID getId() {
    return id;
  }

  /** Returns the username of this User. */
  public String getName() {
    return name;
  }
  
  /** Returns the password of this User */
  public String getPassword() {
    return password;
  }

  /** Returns the creation time of this User. */
  public Instant getCreationTime() {
    return creation;
  }

  /** Returns the "about me" message of this User. */
  public String getAbout() {
    return about;
  }

  /** Changes the "about me" message of this User */
  public void setAbout(String aboutMessage) {
    this.about = aboutMessage;
  }

  /** Returns whether this User allows message deletion */
  public Boolean getAllowMessageDel() {
    return allowMessageDel;
  }

  public void setAllowMessageDel(Boolean delete) {
    this.allowMessageDel = delete;
  }
}
