package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class HairLength
{
    @Id
    private int hairLengthId;
    private String hairLengthName;
    private int sortOrder;

    public int getHairLengthId()
    {
        return hairLengthId;
    }

    public String getHairLengthName()
    {
        return hairLengthName;
    }

    public int getSortOrder()
    {
        return sortOrder;
    }
}
