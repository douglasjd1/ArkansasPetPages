package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dog
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dogId;

    private String dogName;
    private int dogAge;
    private int dogReffNum;
    private int weight;
    private int hairLengthId;
    private int locationId;
    private int colorId;

    public void setDogName(String dogName)
    {
        this.dogName = dogName;
    }

    public void setDogAge(int dogAge)
    {
        this.dogAge = dogAge;
    }

    public void setDogReffNum(int dogReffNum)
    {
        this.dogReffNum = dogReffNum;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public void setHairLengthId(int hairLengthId)
    {
        this.hairLengthId = hairLengthId;
    }

    public void setLocationId(int locationId)
    {
        this.locationId = locationId;
    }

    public int getDogId()
    {
        return dogId;
    }

    public String getDogName()
    {
        return dogName;
    }

    public int getDogAge()
    {
        return dogAge;
    }

    public int getDogReffNum()
    {
        return dogReffNum;
    }

    public int getWeight()
    {
        return weight;
    }

    public int getHairLengthId()
    {
        return hairLengthId;
    }

    public int getLocationId()
    {
        return locationId;
    }

    public int getColorId()
    {
        return colorId;
    }

    public void setColorId(int colorId)
    {
        this.colorId = colorId;
    }
}
