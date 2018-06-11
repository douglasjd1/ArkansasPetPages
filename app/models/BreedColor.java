package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BreedColor
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int breedColorId;
    private int breedId;
    private int colorId;

    public void setBreedId(int breedId)
    {
        this.breedId = breedId;
    }

    public void setColorId(int colorId)
    {
        this.colorId = colorId;
    }

    public int getBreedId()
    {
        return breedId;
    }

    public int getColorId()
    {
        return colorId;
    }
}
