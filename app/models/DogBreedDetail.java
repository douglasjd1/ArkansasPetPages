package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DogBreedDetail
{
    @Id
    private int dogBreedId;

    private int breedId;
    private int dogId;
    private String breedName;

    public DogBreedDetail(int breedId, int dogId, String breedName)
    {
        this.breedId = breedId;
        this.dogId = dogId;
        this.breedName = breedName;
    }

    public int getBreedId()
    {
        return breedId;
    }

    public int getDogId()
    {
        return dogId;
    }

    public String getBreedName()
    {
        return breedName;
    }
}
