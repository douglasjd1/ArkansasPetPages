package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Breed
{
    @Id
    private int breedId;
    private String breedName;
    private int weightMin;
    private int weightMax;
    private int heightMax;
    private int heightMin;
    private int lifeSpanMin;
    private int lifeSpanMax;
    private int hairLengthId;
    private BigDecimal costFromBreeder;

    public int getBreedId()
    {
        return breedId;
    }

    public String getBreedName()
    {
        return breedName;
    }

    public int getWeightMin()
    {
        return weightMin;
    }

    public int getWeightMax()
    {
        return weightMax;
    }

    public int getHeightMax()
    {
        return heightMax;
    }

    public int getHeightMin()
    {
        return heightMin;
    }

    public int getLifeSpanMin()
    {
        return lifeSpanMin;
    }

    public int getLifeSpanMax()
    {
        return lifeSpanMax;
    }

    public int getHairLengthId()
    {
        return hairLengthId;
    }

    public BigDecimal getCostFromBreeder()
    {
        return costFromBreeder;
    }

    public void setBreedName(String breedName)
    {
        this.breedName = breedName;
    }

    public void setWeightMin(int weightMin)
    {
        this.weightMin = weightMin;
    }

    public void setWeightMax(int weightMax)
    {
        this.weightMax = weightMax;
    }

    public void setHeightMax(int heightMax)
    {
        this.heightMax = heightMax;
    }

    public void setHeightMin(int heightMin)
    {
        this.heightMin = heightMin;
    }

    public void setLifeSpanMin(int lifeSpanMin)
    {
        this.lifeSpanMin = lifeSpanMin;
    }

    public void setLifeSpanMax(int lifeSpanMax)
    {
        this.lifeSpanMax = lifeSpanMax;
    }

    public void setHairLengthId(int hairLengthId)
    {
        this.hairLengthId = hairLengthId;
    }

    public void setCostFromBreeder(BigDecimal costFromBreeder)
    {
        this.costFromBreeder = costFromBreeder;
    }
}
