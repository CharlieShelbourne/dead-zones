import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.abs;

public class quickHull {
    ArrayList<BattleNode> hull = new ArrayList<>();
    quickHull(){

    }

    int findSide(Point p1, Point p2, Point p){
        double val =(p.getY() - p1.getY()) * (p2.getX() - p1.getX()) - (p2.getY() - p1.getY()) * (p.getX() - p1.getX());
        if (val > 0){return 1;}
        if (val < 0){return -1;}
        return 0;
    }
    // Proportional distance of p from line p1 to p2
    int lineDist(Point p1, Point p2, Point p){
        return (int) (abs ((p.getY() - p1.getY()) * (p2.getX() - p1.getX()) -
                (p2.getY() - p1.getY()) * (p.getX() - p1.getX())));
    }
    void quickHull(ArrayList<BattleNode> connectedComponent, int n, BattleNode p1, BattleNode p2, int side){
        int ind = -1;
        int maxDist = 0;

        for (int i =0; i < n;i++){
            int temp = lineDist(p1.getPoint(),p2.getPoint(),connectedComponent.get(i).getPoint());
            if(findSide(p1.getPoint(),p2.getPoint(),connectedComponent.get(i).getPoint()) == side && temp > maxDist){
                ind = i;
                maxDist = temp;
            }
        }
        if(ind == -1){
            if(!hull.contains(p1))
                hull.add(p1);
            if(!hull.contains(p2))
                hull.add(p2);
            return;
        }
        quickHull(connectedComponent,n,connectedComponent.get(ind),p1,-findSide(connectedComponent.get(ind).getPoint(),p1.getPoint(),p2.getPoint()));
        quickHull(connectedComponent,n,connectedComponent.get(ind),p2,-findSide(connectedComponent.get(ind).getPoint(),p2.getPoint(),p1.getPoint()));
    }
    void findHull(ArrayList<BattleNode> connectedComponent){
        int n = connectedComponent.size();
        ArrayList<java.awt.Point> p = new ArrayList<>();
        ArrayList<Point2D.Double> geoList = new ArrayList<>();

        for(BattleNode node: connectedComponent){
            p.add(node.getPoint());
            geoList.add(node.getCoordinates());
        }

        java.awt.Point[] a = p.toArray(new java.awt.Point[p.size()]);

        if (n < 3){
            System.out.println("Convex hull not possible");
            return;
        }
        int minX = 0;
        int maxX = 0;
        for(int i = 1; i<n; i++){
            if(connectedComponent.get(i).getPoint().getX() < connectedComponent.get(minX).getPoint().getX())
                minX = i;
            if(connectedComponent.get(i).getPoint().getX() > connectedComponent.get(maxX).getPoint().getX())
                maxX = i;
        }
        //Recursively find convex hull points
        quickHull(connectedComponent,n,connectedComponent.get(minX),connectedComponent.get(maxX),1);
        quickHull(connectedComponent,n,connectedComponent.get(minX),connectedComponent.get(maxX),-1);

    }
    ArrayList<BattleNode> getHull(){return hull;}

}
