import org.locationtech.jts.geom.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.*;

public class alphaShape {

    ArrayList<LineString> edges = new ArrayList<>();
    ArrayList<java.awt.Point> vertices = new ArrayList<>();
    ArrayList<LineString> outerEdges = new ArrayList<>();
    ArrayList<BattleNode> outerRing = new ArrayList<>();

    alphaShape(ArrayList<BattleNode> connectedComponent){

        Geometry mesh = new trianglate().delaunayTriangulation(connectedComponent);

        Geometry zero = mesh.getGeometryN(0);

        for(int i =0;i < mesh.getNumGeometries();i++){
            Coordinate[] coords = mesh.getGeometryN(i).getCoordinates();
            HashSet<Coordinate> hashSet = new HashSet<Coordinate>(Arrays.asList(coords));
            coords = hashSet.toArray(new Coordinate[0]);

            findEdges(coords);
        }
        ArrayList<org.locationtech.jts.geom.Point> pointStack = new ArrayList<>();
        for (LineString edge : edges) {
            int firstInd = edges.indexOf(edge);
            int lastInd = edges.lastIndexOf(edge);

            if (firstInd == lastInd) {
                outerEdges.add(edge);

            }
        }

        for (LineString edge: edges){
            boolean remove = false;
            int count = 0;
            for(LineString edge2: edges) {
                if (edge != edge2) {
                    org.locationtech.jts.geom.Point p1 = edge.getPointN(0);
                    org.locationtech.jts.geom.Point p2 = edge.getPointN(1);
                    org.locationtech.jts.geom.Point p3 = edge2.getPointN(0);
                    org.locationtech.jts.geom.Point p4 = edge2.getPointN(1);

                    if (((p1.getX() == p3.getX() && p1.getY() == p3.getY()) && (p2.getX() == p4.getX() && p2.getY() == p4.getY())) ||
                            (p1.getX() == p4.getX() && p1.getY() == p4.getY()) && (p2.getX() == p3.getX() && p2.getY() == p3.getY())) {

                        remove = true;
                        outerEdges.remove(edge2);
                        count = count+1;
                    }
                }
            }
            if (remove) {
                outerEdges.remove(edge);
                remove = false;
            }
        }


        ArrayList<Double> xs = new ArrayList<>();
        ArrayList<Double> ys = new ArrayList<>();
        for (LineString edge: outerEdges){
            for(int i =0; i<edge.getNumPoints();i++){
               java.awt.Point aux = new java.awt.Point((int)edge.getPointN(i).getX(),(int)edge.getPointN(i).getY());

               for(BattleNode node: connectedComponent){
                   if(node.getPoint().getX() == aux.getX() && node.getPoint().getY() == aux.getY() && !outerRing.contains(node)){
                      outerRing.add(node);
                   }
               }
           }
        }

    }



    void findEdges(Coordinate[] coords){
        GeometryFactory gf = new GeometryFactory();
        ArrayList<LineString> e = new ArrayList<>();
        Coordinate[] aux = {coords[0],coords[1]};
        Coordinate[] aux2 = {coords[0],coords[2]};
        Coordinate[] aux3 = {coords[1],coords[2]};
        edges.add(gf.createLineString(aux));
        edges.add(gf.createLineString(aux2));
        edges.add(gf.createLineString(aux3));


    }
    ArrayList<BattleNode> getAlphaShape(){
        return outerRing;
    }

}

