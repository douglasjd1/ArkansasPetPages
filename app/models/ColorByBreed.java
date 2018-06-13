package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ColorByBreed
{
    @Id
    private int breedColorId;

    private String colorName;

    public ColorByBreed(int breedColorId, String colorName)
    {
        this.breedColorId = breedColorId;
        this.colorName = colorName;
    }

    public int getBreedColorId()
    {
        return breedColorId;
    }

    public String getColorName()
    {
        return colorName;
    }
}
