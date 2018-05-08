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

package codeu.model.store.persistence;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentDataStoreException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles all interactions with Google App Engine's Datastore service. On startup it
 * sets the state of the applications's data objects from the current contents of its Datastore. It
 * also performs writes of new of modified objects back to the Datastore.
 */
public class PersistentDataStore {

  // Handle to Google AppEngine's Datastore service.
  private DatastoreService datastore;

  //  List of UserEntities that can be used alter user data
  private Map<UUID, Entity> userEntitiesById;

  //  List of Message Entities that can be used to alter message data
  private Map<UUID, Entity> messageEntitiesById;

  /**
   * Constructs a new PersistentDataStore and sets up its state to begin loading objects from the
   * Datastore service.
   */
  public PersistentDataStore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Loads all User objects from the Datastore service and returns them in a List.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<User> loadUsers() throws PersistentDataStoreException {

    List<User> users = new ArrayList<>();
    userEntitiesById = new HashMap<>();

    // Retrieve all users from the datastore.
    Query query = new Query("chat-users");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        String userName = (String) entity.getProperty("username");
        String password = (String) entity.getProperty("password");
        String about = (String) entity.getProperty("about");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        boolean isAdmin = (Boolean) entity.getProperty("isAdmin");

        boolean delete = (entity.hasProperty("allowMessageDel"))
                       ? (boolean) entity.getProperty("allowMessageDel")
                       : false;
        int messagesSent = (entity.hasProperty("messagesSent"))
                         ? ((Long) entity.getProperty("messagesSent")).intValue()
                         : -1;

        // For some reason I kept getting an exception of an instance being null (which I don't
        // know how it could happen) so this following check fixed it.
        // Instant creationTime = Instant.now();
        if (entity.getProperty("creation") != null) {
          creationTime = Instant.parse((String) entity.getProperty("creation"));
        }

        // Retrieving the individual lists of Keys and Values for the conversationVisibilities map.
        List<String> conversationIdsString = (List<String>) entity.getProperty("conversationIds");
        List<UUID> conversationIds = convertListtoUUID(conversationIdsString);
        List<Boolean> hiddenConversations = (List<Boolean>) entity.getProperty("hiddenConversations");

        // A new map is created from the two lists
        Map<UUID, Boolean> conversationVisibilities = new HashMap();
        for (int i = 0; i < conversationIds.size(); ++i) {
          conversationVisibilities.put(conversationIds.get(i), hiddenConversations.get(i));
        }

        User user = new User(uuid, userName, password, about, isAdmin, delete, messagesSent, creationTime,
                             conversationVisibilities);

        users.add(user);
        userEntitiesById.put(uuid, entity);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return users;
  }

  /**
   * Loads all User objects who are admins from the Datastore service and returns them in a List.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<User> loadAdmins() throws PersistentDataStoreException {

    List<User> admins = new ArrayList<>();

    // Retrieve all users from the datastore.
    Query query = new Query("chat-users");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        boolean isAdmin = (Boolean) entity.getProperty("isAdmin");
        if(!isAdmin) {
          continue;
        }
        String userName = (String) entity.getProperty("username");
        String password = (String) entity.getProperty("password");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String about = (String) entity.getProperty("about");
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        User admin = new User(uuid, userName, password, creationTime, isAdmin);
        if (isAdmin) {
          admins.add(admin);
        }
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }
    return admins;
  }

  /**
   * Loads all Conversation objects from the Datastore service and returns them in a List.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Conversation> loadConversations() throws PersistentDataStoreException {

    List<Conversation> conversations = new ArrayList<>();

    // Retrieve all conversations from the datastore.
    Query query = new Query("chat-conversations");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID ownerUuid = UUID.fromString((String) entity.getProperty("owner_uuid"));
        String title = (String) entity.getProperty("title");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        Conversation conversation = new Conversation(uuid, ownerUuid, title, creationTime);
        conversations.add(conversation);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return conversations;
  }

  /**
   * Loads all Message objects from the Datastore service and returns them in a List.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Message> loadMessages() throws PersistentDataStoreException {

    List<Message> messages = new ArrayList<>();
    messageEntitiesById = new HashMap<>();

    // Retrieve all messages from the datastore.
    Query query = new Query("chat-messages");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID conversationUuid = UUID.fromString((String) entity.getProperty("conv_uuid"));
        UUID authorUuid = UUID.fromString((String) entity.getProperty("author_uuid"));
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String content = (String) entity.getProperty("content");
        Message message = new Message(uuid, conversationUuid, authorUuid, content, creationTime);
        messages.add(message);
        messageEntitiesById.put(uuid, entity);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return messages;
  }

  /** Write a User object to the Datastore service. */
  public void writeThrough(User user) {
    Entity userEntity = new Entity("chat-users");
    userEntity.setProperty("uuid", user.getId().toString());
    userEntity.setProperty("username", user.getName());
    userEntity.setProperty("password", user.getPassword());
    userEntity.setProperty("about", user.getAbout());
    userEntity.setProperty("creation_time", user.getCreationTime().toString());
    userEntity.setProperty("isAdmin", user.getIsAdmin());
    userEntity.setProperty("messagesSent", user.getMessagesSent());
    userEntity.setProperty("allowMessageDel", user.getAllowMessageDel());
    userEntity.setProperty("creation", user.getCreationTime().toString());

    /** Since the map of conversationVisibilities can't be stored on the user entity, a list of
    *   its keys and a separate list of its values are stored. UUIDs are also not supported, so
    *   the list is converted to contain Strings.
    */
    List<UUID> conversationIds = new ArrayList<UUID>(user.getConversations().keySet());
    List<String> stringList = convertListtoString(conversationIds);
    userEntity.setProperty("conversationIds", stringList);

    List<Boolean> hiddenConversations = new ArrayList<Boolean>(user.getConversations().values());
    userEntity.setProperty("hiddenConversations", hiddenConversations);

    datastore.put(userEntity);
  }

  /** Change some property of a user then re-add to datastore. */
  public void update(User user) {
    UUID userId = user.getId();
    if (!userEntitiesById.containsKey(userId)) {
      return;
    }

    Entity userEntity = userEntitiesById.get(userId);
    userEntity.setProperty("about", user.getAbout());
    userEntity.setProperty("allowMessageDel", user.getAllowMessageDel());
    userEntity.setProperty("messagesSent", user.getMessagesSent());

    /** Since the map of conversationVisibilities can't be stored on the user entity, a list of
    *   its keys and a separate list of its values are stored. UUIDs are also not supported, so
    *   the list is converted to contain Strings.
    */
    List<UUID> conversationIds = new ArrayList<UUID>(user.getConversations().keySet());
    List<String> conversationIdString = convertListtoString(conversationIds);
    userEntity.setProperty("conversationIds", conversationIdString);

    List<Boolean> hiddenConversations = new ArrayList<Boolean>(user.getConversations().values());
    userEntity.setProperty("hiddenConversations", hiddenConversations);

    datastore.put(userEntity);
  }

  /** Delete a User object from the Datastore service */
  public void delete(User user) {
    UUID userId = user.getId();
    if (!userEntitiesById.containsKey(userId)) {
      return;
    }

    Entity userEntity = userEntitiesById.get(userId);
    datastore.delete(userEntity.getKey());
  }

  /** Write a Message object to the Datastore service. */
  public void writeThrough(Message message) {
    Entity messageEntity = new Entity("chat-messages");
    messageEntity.setProperty("uuid", message.getId().toString());
    messageEntity.setProperty("conv_uuid", message.getConversationId().toString());
    messageEntity.setProperty("author_uuid", message.getAuthorId().toString());
    messageEntity.setProperty("content", message.getContent());
    messageEntity.setProperty("creation_time", message.getCreationTime().toString());
    datastore.put(messageEntity);
  }

  /** Delete a Message object from the Datastore service */
  public void delete(Message message) {
    UUID messageId = message.getId();
    if (!messageEntitiesById.containsKey(messageId)) {
      return;
    }

    Entity messageEntity = messageEntitiesById.get(messageId);
    datastore.delete(messageEntity.getKey());
  }

  /** Write a Conversation object to the Datastore service. */
  public void writeThrough(Conversation conversation) {
    Entity conversationEntity = new Entity("chat-conversations");
    conversationEntity.setProperty("uuid", conversation.getId().toString());
    conversationEntity.setProperty("owner_uuid", conversation.getOwnerId().toString());
    conversationEntity.setProperty("title", conversation.getTitle());
    conversationEntity.setProperty("creation_time", conversation.getCreationTime().toString());
    datastore.put(conversationEntity);
  }

  /** Helper function to turn a List<UUID> into a List<String> */
  private List<String> convertListtoString(List<UUID> inputList) {
    List<String> stringList = new ArrayList();
    for (UUID conversationId : inputList) {
      stringList.add(conversationId.toString());
    }
    return stringList;
  }

  /** Helper function to turn a List<String> into a List<UUID> */
  private List<UUID> convertListtoUUID(List<String> inputList) {
    List<UUID> UUIDList = new ArrayList();
    if (inputList != null) {
      for (String conversationString : inputList) {
        UUIDList.add(UUID.fromString(conversationString));
      }
    }
    return UUIDList;
  }
}
