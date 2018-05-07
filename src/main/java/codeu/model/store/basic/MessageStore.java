
package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import codeu.model.store.basic.UserStore;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class MessageStore {

  /** Singleton instance of MessageStore. */
  private static MessageStore instance;

  /**
   * Returns the singleton instance of MessageStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static MessageStore getInstance() {
    if (instance == null) {
      instance = new MessageStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static MessageStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new MessageStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Messages from and saving Messages to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Messages. */
  private List<Message> messages;

  /** The in-memory list of Messages, organized by author Id */
  private Map<UUID, List<Message>> messagesByAuthorId;


  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private MessageStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    messages = new ArrayList<>();
    messagesByAuthorId = new HashMap<>();
  }

  /**
   * Load a set of randomly-generated Message objects.
   *
   * @return false if an error occurs.
   */
  public boolean loadTestData() {
    boolean loaded = false;
    try {
      messages.addAll(DefaultDataStore.getInstance().getAllMessages());
      for (Message message : messages) {
        messagesByAuthorId.computeIfAbsent(
          message.getAuthorId(), k -> new ArrayList()).add(message);
      }
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.out.println("ERROR: Unable to establish initial store (messages).");
    }
    return loaded;
  }

  /** Add a new message to the current set of messages known to the application. */
  public void addMessage(Message message) {
    messages.add(message);
    persistentStorageAgent.writeThrough(message);
  }

  /** Delete a message from the current set of messages known to the application */
  public void deleteMessage(Message message) {
    messages.remove(message);
    persistentStorageAgent.delete(message);
  }

  /** Delete an old message sent by this User */
  public void deleteOldMessages(UUID userId, int numOfMessages) {
    List<Message> messagesByAuthor = getMessagesByAuthor(userId); 
    int numToRemove = Math.min(numOfMessages, messagesByAuthor.size());
    List<Message> messagesToDelete = messagesByAuthor.subList(0, numToRemove);
    messages.removeAll(messagesToDelete);
    messagesToDelete.clear();
  }

  /** Access the current set of Messages within the given Conversation. */
  public List<Message> getMessagesInConversation(UUID conversationId) {

    List<Message> messagesInConversation = new ArrayList<>();

    for (Message message : messages) {
      if (message.getConversationId().equals(conversationId)) {
        messagesInConversation.add(message);
      }
    }

    return messagesInConversation;
  }

  /** Access the current set of Messages sent by a specific user. */
  public List<Message> getMessagesByAuthor(UUID authorId) {

    if (!messagesByAuthorId.containsKey(authorId)) {
      return  new ArrayList<>();
    }
    List<Message> authorMessages = messagesByAuthorId.get(authorId);
    //Sorts using the overriden compareTo method on the Message class
    Collections.sort(authorMessages);

    return authorMessages;
  }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
    for (Message message : messages) {
      messagesByAuthorId.computeIfAbsent(
        message.getAuthorId(), k -> new ArrayList()).add(message);
    }
  }

  public List<Message> getMessagesByUserId(UUID author) {
    List<Message> ans = new ArrayList<Message>();
    for(Message m : messages){
      if(m.getAuthorId().equals(author)){
        ans.add(m);
      }
    }
    return ans;
  }

  /** returns number of messages */
  public int getNumMessages() {
    return messages.size();
  }
}
