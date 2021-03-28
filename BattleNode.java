import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BattleNode {
    //TODO make all gird point objects
    private Point2D.Double coordinates;
    private Point point;
    private boolean hit;
    private boolean visited;
    private int id;
    private int componentId;
    private ArrayList<BattleNode> neighbours = new ArrayList<>();
    private boolean member;

    BattleNode(){
        this.hit = false;
        this.visited = false;
        this.componentId = 0;
        this.member = false;
    }
   public void setGlobeLocation(double x,double y){
        coordinates = new Point2D.Double(x,y);
        point = new Point((int)(x+89.5),(int)(y+179.5));

    }
    public Point2D.Double getCoordinates(){return coordinates;}

    public Point getPoint(){return point;}

   public void hit(){ hit = true; }

   public void visit(){ visited = true;}

   public void addNeighbour(BattleNode node) {
        boolean exists = false;
        for(BattleNode n:neighbours){
            if(n.coordinates.getX() == node.coordinates.getX() && n.coordinates.getY() == node.coordinates.getY()){
               exists = true;
            }
        }
        if (exists == false){
            neighbours.add(node);
        }

    }
    public ArrayList<BattleNode> getNeighbours(){
        if (neighbours.size()>0){
            return neighbours;
        }
        else{
            return null;
        }
    }

   public void asMember(){member = true;}

   public void setNodeId(int ID){ id = ID; }

   public void setComponentId(int ID){ componentId = ID; }

   public boolean hasHit() {
        return hit;
    }

    public boolean visited(){
        return visited;
    }

   public int nodeId(){return id;}

   public int compId(){return componentId;}

   public boolean isMember(){return member;}

   boolean neighbourHasHit() {
       return neighbours.stream().filter(BattleNode::hasHit).map(BattleNode::hasHit).findAny().orElse(false);
   }
}



