package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DogPhoto
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dogPhotoId;

    private byte[] dogPhotoData;
    private int dogId;

    public byte[] getDogPhotoData()
    {
        return dogPhotoData;
    }

    public int getDogId()
    {
        return dogId;
    }

    public void setDogPhotoData(byte[] dogPhotoData)
    {
        this.dogPhotoData = dogPhotoData;
    }

    public void setDogId(int dogId)
    {
        this.dogId = dogId;
    }

    public int getDogPhotoId()
    {
        return dogPhotoId;
    }
}
