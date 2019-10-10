package utils;

public class Resource {

    private String name;

    private Integer amount;

    public Resource(String name, Integer amount){
        this.name = name;
        this.amount = amount;
    }

    public Integer getAmount(){
        return this.amount;
    }

    public void subtractAmount(Integer sub){
        this.amount -= sub;
    }
}
