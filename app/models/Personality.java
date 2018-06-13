package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Personality
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personalityId;
    private String personalityName;

    public int getPersonalityId()
    {
        return personalityId;
    }

    public String getPersonalityName()
    {
        return personalityName;
    }

    public void setPersonalityName(String personalityName)
    {
        this.personalityName = personalityName;
    }
}
