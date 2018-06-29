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
    private String dogReffNum;
    private int weight;
    private int height;
    private int hairLengthId;
    private Integer locationId;
    private int colorId;
    private byte[] defaultPhotoData;
    private Integer petPagesUserId;

    public void setDogName(String dogName)
    {
        this.dogName = dogName;
    }

    public void setDogAge(int dogAge)
    {
        this.dogAge = dogAge;
    }

    public void setDogReffNum(String dogReffNum)
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

    public void setLocationId(Integer locationId)
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

    public String getDogReffNum()
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

    public Integer getLocationId()
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

    public void setDefaultPhotoData(byte[] defaultPhotoData)
    {
        this.defaultPhotoData = defaultPhotoData;
    }

    public byte[] getDefaultPhotoData()
    {
        return defaultPhotoData;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public Integer getPetPagesUserId()
    {
        return petPagesUserId;
    }

    public void setPetPagesUserId(Integer petPagesUserId)
    {
        this.petPagesUserId = petPagesUserId;
    }
}
