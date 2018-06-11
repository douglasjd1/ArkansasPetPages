package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DogBreed
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dogBreedId;

    private int dogId;
    private int breedId;

    public int getDogBreedId()
    {
        return dogBreedId;
    }

    public int getDogId()
    {
        return dogId;
    }

    public int getBreedId()
    {
        return breedId;
    }
}
