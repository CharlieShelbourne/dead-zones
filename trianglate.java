
import java.util.ArrayList;

import org.ejml.*;

import org.ejml.simple.*;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

public class trianglate {

    Geometry delaunayTriangulation(ArrayList<BattleNode> connectedComponent){
        DelaunayTriangulationBuilder delaunay = new DelaunayTriangulationBuilder();
        delaunay.setTolerance(0.0);
        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();

        for(BattleNode node: connectedComponent) {
            coords.add(new Coordinate(node.getPoint().getX(),node.getPoint().getY()));
        }
        delaunay.setSites(coords);

        GeometryFactory geoFact = new GeometryFactory();

        Geometry mesh = delaunay.getTriangles(geoFact);

        return mesh;
    }

}


