package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DogPersonality
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dogPersonalityid;
    private int dogId;
    private int personalityId;

    public int getDogId()
    {
        return dogId;
    }

    public void setDogId(int dogId)
    {
        this.dogId = dogId;
    }

    public int getPersonalityId()
    {
        return personalityId;
    }

    public void setPersonalityId(int personalityId)
    {
        this.personalityId = personalityId;
    }
}
