package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class BreedDetail
{
    @Id
    private int breedId;

    private String breedName;
    private String colorName;
    private int weightMin;
    private int weightMax;
    private int weightAvg;
    private int heightMin;
    private int heightMax;
    private int lifeSpanMin;
    private int lifeSpanMax;
    private String hairLengthName;
    private BigDecimal costFromBreeder;
    private String photo1;
    private String photo2;
    private String photo3;

    public BreedDetail(int breedId, String breedName, String colorName, int weightMin, int weightMax, int heightMin, int heightMax, int lifeSpanMin, int lifeSpanMax, String hairLengthName, BigDecimal costFromBreeder, String photo1, String photo2, String photo3)
    {
        this.breedId = breedId;
        this.breedName = breedName;
        this.colorName = colorName;
        this.weightMin = weightMin;
        this.weightMax = weightMax;
        this.heightMin = heightMin;
        this.heightMax = heightMax;
        this.lifeSpanMin = lifeSpanMin;
        this.lifeSpanMax = lifeSpanMax;
        this.hairLengthName = hairLengthName;
        this.costFromBreeder = costFromBreeder;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.photo3 = photo3;
    }

    public int getBreedId()
    {
        return breedId;
    }

    public String getBreedName()
    {
        return breedName;
    }

    public String getColorName()
    {
        return colorName;
    }

    public int getWeightMin()
    {
        return weightMin;
    }

    public int getWeightMax()
    {
        return weightMax;
    }

    public int getHeightMin()
    {
        return heightMin;
    }

    public int getHeightMax()
    {
        return heightMax;
    }

    public int getLifeSpanMin()
    {
        return lifeSpanMin;
    }

    public int getLifeSpanMax()
    {
        return lifeSpanMax;
    }

    public String getHairLengthName()
    {
        return hairLengthName;
    }

    public BigDecimal getCostFromBreeder()
    {
        return costFromBreeder;
    }

    public String getPhoto1()
    {
        return photo1;
    }

    public String getPhoto2()
    {
        return photo2;
    }

    public String getPhoto3()
    {
        return photo3;
    }
}
