package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BreederDetail
{
    @Id
    private int locationId;

    private String locationName;
    private String address;
    private String city;
    private String stateName;
    private String zipCode;
    private String breedName;
    private int breedId;
    private String phoneNumber;
    private String emailAddress;
    private String websiteURL;

    public BreederDetail(int locationId, String locationName, String address, String city, String stateName, String zipCode, String breedName, int breedId, String phoneNumber, String emailAddress, String websiteURL)
    {
        this.locationId = locationId;
        this.locationName = locationName;
        this.address = address;
        this.city = city;
        this.stateName = stateName;
        this.zipCode = zipCode;
        this.breedName = breedName;
        this.breedId = breedId;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.websiteURL = websiteURL;
    }

    public BreederDetail(int locationId, String locationName, String address, String city, String stateName, String zipCode, String breedName, String phoneNumber, String emailAddress, String websiteURL)
    {
        this.locationId = locationId;
        this.locationName = locationName;
        this.address = address;
        this.city = city;
        this.stateName = stateName;
        this.zipCode = zipCode;
        this.breedName = breedName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.websiteURL = websiteURL;
    }

    public int getLocationId()
    {
        return locationId;
    }

    public void setLocationId(int locationId)
    {
        this.locationId = locationId;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getStateName()
    {
        return stateName;
    }

    public void setStateName(String stateName)
    {
        this.stateName = stateName;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getBreedName()
    {
        return breedName;
    }

    public void setBreedName(String breedName)
    {
        this.breedName = breedName;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getWebsiteURL()
    {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL)
    {
        this.websiteURL = websiteURL;
    }

    public int getBreedId()
    {
        return breedId;
    }

    public void setBreedId(int breedId)
    {
        this.breedId = breedId;
    }
}