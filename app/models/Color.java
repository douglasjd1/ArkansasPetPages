package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Color
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int colorId;
    private String colorName;

    public int getColorId()
    {
        return colorId;
    }

    public String getColorName()
    {
        return colorName;
    }

    public void setColorName(String colorName)
    {
        this.colorName = colorName;
    }
}
