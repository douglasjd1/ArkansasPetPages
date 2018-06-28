package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ShelterDetail
{
    @Id
    private int locationId;

    private String locationName;
    private String city;
    private String address;
    private String stateName;
    private String zipCode;
    private String phoneNumber;
    private String websiteURL;
    private String emailAddress;

    public ShelterDetail(int locationId, String locationName, String city, String address, String stateName, String zipCode, String phoneNumber,
                         String websiteURL, String emailAddress)
    {
        this.locationId = locationId;
        this.locationName = locationName;
        this.city = city;
        this.address = address;
        this.stateName = stateName;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
        this.emailAddress = emailAddress;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public String getCity()
    {
        return city;
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

    public int getLocationId()
    {
        return locationId;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }
}
