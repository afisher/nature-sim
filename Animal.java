import java.awt.Image;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Animal extends Organism {
    protected int age;
    protected double hunger;
    
    protected abstract double getMaxHunger();
    protected abstract int getMaxAge();
    protected abstract int getSightDistance();
    protected abstract int getMoveDistance();
    public abstract double getCalories();
    public abstract void act(Grid grid);
    public abstract Image getImage();
    
    public void step(Grid grid){
        age++;
        hunger+= getCalories()/4;
        if(isOld() || isStarving()) {
            if(isOld()){
                Debug.echo("Animal at "+getLocation()+" died due to old age");
            } else {
                Debug.echo("Animal at "+getLocation()+" died due to hunger");
            }
            grid.removeAnimal(getLocation());
        } else {
            act(grid);
        }
    }
    public boolean isOld(){
        return age >= getMaxAge();
    }
    public boolean isStarving(){
        return hunger >= getMaxHunger();
    }
    protected void eat(double amount){
        hunger -= amount;
        if(hunger < 0) {
            hunger = 0;
        }
    }
    protected void move(Grid grid, Location newLocation){
        grid.removeAnimal(getLocation());
        grid.addAnimal(this, newLocation);
        setLocation(newLocation);
    }
    protected void move(Grid grid, GridSquare newGridSquare){
        move(grid, newGridSquare.getLocation());
    }
    
    protected Organism bestPreyInDistance(Grid grid, ArrayList<String> prey, int distance){
        GridSquare mySquare = grid.get(getLocation());

        Organism bestAdjacentPrey = null;
        
        if (mySquare.getPlant() != null && mySquare.getPlant().isAlive() && prey.contains(mySquare.getPlant().getClass().getName())){
            bestAdjacentPrey = mySquare.getPlant();
        }
    
        List<DistanceSquarePair> reachableSquares = grid.getAdjacentSquares(getLocation(), distance);
        List<DistanceSquarePair> preySquares = grid.getOrganismSquares(reachableSquares, prey);
        List<DistanceSquarePair> emptySquares = grid.getEmptySquares(reachableSquares);
        
        Organism temp;
        for(DistanceSquarePair pair: preySquares){
            if(emptySquares.contains(pair)){
                temp = pair.gridSquare.getPlant();
                if (bestAdjacentPrey == null || bestAdjacentPrey.getCalories() < temp.getCalories()){
                    bestAdjacentPrey = temp;
                }
            } else {
                temp = pair.gridSquare.getAnimal();
                if (prey.contains(temp.getClass().getName())) {
                     if (bestAdjacentPrey == null || bestAdjacentPrey.getCalories() < temp.getCalories()){
                        bestAdjacentPrey = temp;
                    }
                } else {
                    //I want to eat the plant, but the square is occupied... Oh well.
                }
            }
        }
        return bestAdjacentPrey;
    }
}
