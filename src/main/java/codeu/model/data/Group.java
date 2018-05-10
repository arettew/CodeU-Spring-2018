package codeu.model.data;

import java.time.Instant;
import java.util.UUID;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/** Class representing a registered Group. */
public class Group {
  private final UUID id;
  private final UUID owner;
  private final String name;
  private final Instant creation;
  private final Set<UUID> participants;

  public Group(UUID id, UUID owner, String name, Instant creation) {
    this.id = id;
    this.owner = owner;
    this.name = name;
    this.creation = creation;
    this.participants = new HashSet<UUID>();
    participants.add(owner);
  }

  /** Returns the ID of this Group. */
  public UUID getId() {
    return id;
  }

  /** Returns the ID of the User who created this Conversation. */
  public UUID getOwnerId() {
    return owner;
  }

  /** Returns the username of this Group. */
  public String getName() {
    return name;
  }

  /** Returns the creation time of this Group. */
  public Instant getCreationTime() {
    return creation;
  }

  public void addParticipant(UUID id) {
    participants.add(id);
  }

  public void removeParticipants(UUID id) {
    participants.remove(id);
  }

  public Set<UUID> getParticipants() {
    return participants;
  }
}
