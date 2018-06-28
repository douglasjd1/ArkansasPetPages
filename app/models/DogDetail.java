package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DogDetail
{
    @Id
    private int dogId;

    private String dogName;
    private int weight;
    private int height;
    private String hairLengthName;
    private String colorName;
    private int dogAge;

    public DogDetail(int dogId, String dogName, int weight, int height, String hairLengthName, String colorName, int dogAge)
    {
        this.dogId = dogId;
        this.dogName = dogName;
        this.weight = weight;
        this.height = height;
        this.hairLengthName = hairLengthName;
        this.colorName = colorName;
        this.dogAge = dogAge;
    }

    public String getDogName()
    {
        return dogName;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public String getHairLengthName()
    {
        return hairLengthName;
    }

    public String getColorName()
    {
        return colorName;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setColorName(String colorName)
    {
        this.colorName = colorName;
    }

    public int getDogId()
    {
        return dogId;
    }
        
    public int getDogAge()
    {
        return dogAge;
    }

}
