package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BreedPersonality
{
    @Id
    private int breedPersonalityId;

    private int personalityId;
    private int breedId;

    public int getBreedPersonalityId()
    {
        return breedPersonalityId;
    }

    public int getPersonalityId()
    {
        return personalityId;
    }

    public int getBreedId()
    {
        return breedId;
    }
}
