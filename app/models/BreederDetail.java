package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BreederDetail
{
    @Id
    private int locationId;

    private String locationName;
    private String city;
    private String breedName;
    private String address;
    private String stateName;
    private String zipCode;
    private String phoneNumber;
    private String websiteURL;

    public BreederDetail(int locationId, String locationName, String city, String breedName, String address, String stateName, String zipCode, String phoneNumber, String websiteURL)
    {
        this.locationId = locationId;
        this.locationName = locationName;
        this.city = city;
        this.breedName = breedName;
        this.address = address;
        this.stateName = stateName;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public String getCity()
    {
        return city;
    }

    public String getBreedName()
    {
        return breedName;
    }

    public String getAddress()
    {
        return address;
    }

    public String getStateName()
    {
        return stateName;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getWebsiteURL()
    {
        return websiteURL;
    }
}
