import java.awt.Point;
import java.util.ArrayList;

public class centroid {

    Point centre = new Point();

    centroid(){
    }

    void findCentroid(ArrayList<BattleNode> connectedComponent, ArrayList<BattleNode> convexHull){
        double minDist = 0;
        boolean minDistSet = false;
        Point p = new Point();

        for (BattleNode node: connectedComponent){
                p = node.getPoint();
                double total = 0;
                if (insidePolygon(convexHull,convexHull.size(),p)) {

                        for (BattleNode hullNode : convexHull) {

                            total = total + euclideanDistance(p, hullNode.getPoint());
                        }

                        if (minDistSet == false) {
                            minDist = total;
                            this.centre = p;
                            minDistSet = true;
                        }
                        if (total < minDist) {
                            minDist = total;
                            this.centre = p;
                        }
                }

        }
    }

    double euclideanDistance(Point p , Point q){
        double x1 = q.getX()-p.getX();
        double x2 = q.getY()-p.getY();
        return Math.sqrt((x1*x1)+(x2*x2));
    }

    Point getCentroid(){
        return centre;
    }

    boolean insidePolygon(ArrayList<BattleNode> polygon, int N, Point p){
        int counter =0;
        double xInters;
        Point p1,p2;

        p1 = polygon.get(0).getPoint();
        for (int i = 1;i<=N;i++){
            p2 = polygon.get(i%N).getPoint();
            if (p.getY()>Math.min(p1.getY(),p2.getY())) {
                if (p.getY() < Math.max(p1.getY(), p2.getY())) {
                    if (p.getX() < Math.max(p1.getX(), p2.getX())) {
                        if (p1.getY() != p2.getY()) {
                            xInters = (p.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();
                            if (p1.getX() == p2.getX() || p.getX() <= xInters) {
                                counter++;
                                break;
                            }
                        }
                    }
                }
            }
            p1 = p2;
        }
        if (counter % 2 == 0){
            return false;
        }else{
            return true;
        }
    }

}
