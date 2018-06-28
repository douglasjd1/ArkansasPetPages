package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Location
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int locationId;
    private String locationName;
    private String city;
    private Integer breedId;
    private String address;
    private String stateId;
    private String zipCode;
    private String phoneNumber;
    private String websiteURL;
    private String emailAddress;
    private byte[] locationPassword;
    private byte[] salt;

    public String getLocationName()
    {
        return locationName;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public Integer getBreedId()
    {
        return breedId;
    }

    public void setBreedId(Integer breedId)
    {
        this.breedId = breedId;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getStateId()
    {
        return stateId;
    }

    public void setStateId(String stateId)
    {
        this.stateId = stateId;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public int getLocationId()
    {
        return locationId;
    }

    public String getWebsiteURL()
    {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL)
    {
        this.websiteURL = websiteURL;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public byte[] getLocationPassword()
    {
        return locationPassword;
    }

    public void setLocationPassword(byte[] locationPassword)
    {
        this.locationPassword = locationPassword;
    }

    public byte[] getSalt()
    {
        return salt;
    }

    public void setSalt(byte[] salt)
    {
        this.salt = salt;
    }


}
