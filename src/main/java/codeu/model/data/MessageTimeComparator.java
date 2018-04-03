package codeu.model.data;

import java.util.Comparator;

//This comparator should be used to sort message objects by the time they were sent
public class MessageTimeComparator implements Comparator<Message>
{
    // Used for sorting in descending order of time
    @Override
    public int compare(Message a, Message b)
    {
        return (b.getCreationTime().isAfter(a.getCreationTime())) ? -1 : 1;
    }
}