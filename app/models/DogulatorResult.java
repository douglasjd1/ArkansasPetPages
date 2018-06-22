package models;

public class DogulatorResult implements Comparable<DogulatorResult>
{
    private String breedName;
    private double percentage;
    private String photo1;
    private int breedId;

    public DogulatorResult(String breedName, double percentage, String photo1, int breedId)
    {
        this.breedName = breedName;
        this.percentage = percentage;
        this.photo1 = photo1;
        this.breedId = breedId;
    }

    public String getBreedName()
    {
        return breedName;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public String getPhoto1()
    {
        return photo1;
    }

    public int getBreedId()
    {
        return breedId;
    }

    @Override
    public int compareTo(DogulatorResult o)
    {
        return Double.compare(this.getPercentage(), o.getPercentage());
    }
}
