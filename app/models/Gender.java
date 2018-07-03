package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Gender
{
    @Id
    private int genderId;

    private String genderName;

    public String getGenderName()
    {
        return genderName;
    }
}
