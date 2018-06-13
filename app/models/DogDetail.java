package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DogDetail
{
    @Id
    private int dogId;

    private String dogName;
    private int weight;
    private String hairLengthName;
    private String colorName;


}
