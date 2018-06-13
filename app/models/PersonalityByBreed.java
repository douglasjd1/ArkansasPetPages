package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PersonalityByBreed
{
    @Id
    private int breedPersonalityId;

    String personalityName;

    public PersonalityByBreed(int breedPersonalityId, String personalityName)
    {
        this.breedPersonalityId = breedPersonalityId;
        this.personalityName = personalityName;
    }

    public int getBreedPersonalityId()
    {
        return breedPersonalityId;
    }

    public String getPersonalityName()
    {
        return personalityName;
    }
}
