import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.frame.DataFrameValue;

import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Coordinate;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;


public class main {


    public static void main(String[] args) throws IOException, JAXBException, ClassNotFoundException {
        final long startTime = System.currentTimeMillis();

        double gridSpacings = 1;

        //Import land mass polygons from KML file
        extractKmlPolygon parser = new extractKmlPolygon();
        List<List<List<ArrayList<ArrayList<Double>>>>> polygonList = parser.extractKmlPolygon(path);

        //Generate a data frame with of grid points over globe
        DataFrame<Integer, String> gridOfWorld = worldGrid.genWorldGrid(gridSpacings);
        gridOfWorld.cols().add("label", String.class, v -> "unlabelled");

        //Read data into data frame from GEM analytic file
        DataFrame<Integer, String> df = buildDataFrame(path,dataFile);
        df.cols().add("label", String.class, v -> "unlabelled");

        //Clean Data
        dataPreProcess pre = new dataPreProcess();
        /*filters 0 sighting values (errors) and date ranges outside of range specified (date range taken from GEM
        analytics spread sheet*/
        df = pre.filterDuds(df);//, "2019-09-10Z - 2019-09-16Z");

        //Using shipping data remove all grid point from grid of world data frame where ships have been sighted
        DataFrame<Integer, String> deadGridPoints = pre.findDeadGridPoints(df, gridOfWorld);

        //Using land polygons taken from countries_world.kml file, remove dead zone grid points appearing on land
        deadGridPoints = pre.removeLandCoordinates(deadGridPoints, polygonList);

        //create KML file of place markers for all non land dead points in globe
        kmlFormating kml = new kmlFormating("deadGridPoints");
        kml.createKmlFolder();
        kml.generatePlaceMarkersDF(deadGridPoints);

        //Create graph node object representation of grid points over the world
        Graph2 G2 = new Graph2(gridSpacings,gridOfWorld);
        G2.setDeadNodes(deadGridPoints);
        //create links between neighbouring nodes classed as dead zone nodes with 1 degree distance
        // using depth first search (DFS)
        G2.setNeighbours(deadGridPoints);

        //Locate clusters using the graph representation of dead zone nodes
        clustering cl = new clustering();
        cl.findConnectedComponents(G2.getNodeMatrix());
        ArrayList<ArrayList<BattleNode>> connectedComponents = cl.getConnectedComponents();

        //alphaShape alpha = new alphaShape(connectedComponents.get(20));
        ArrayList<ArrayList<BattleNode>> alphaShapes = new ArrayList<>();
        for(ArrayList<BattleNode> connectedComponent: connectedComponents){
            alphaShape alpha = new alphaShape(connectedComponent);
            alphaShapes.add(alpha.getAlphaShape());
        }

        for(int i =0; i<connectedComponents.size();i++){
            if(connectedComponents.get(i).size() > 3){
                centroid cen = new centroid();
                cen.findCentroid(connectedComponents.get(i),alphaShapes.get(i));
                Collections.sort(alphaShapes.get(i), new PointComp(cen.getCentroid()));
            }
        }

        createKMLPolygon kmlPoly2 = new createKMLPolygon(path,"alphaShapes2");
        for(ArrayList<BattleNode> alphaS: alphaShapes) {
            kmlPoly2.buildPolygon(alphaS);
            kmlPoly2.savePolygons();
        }


        //Use quick hull algorithm to find the convex hull of nodes
        quickHull cHull = new quickHull();
        ArrayList<ArrayList<BattleNode>> connectedComponentHulls = new ArrayList<>();
        for(ArrayList<BattleNode> connectedComponent: connectedComponents){
            cHull.findHull(connectedComponent);
            connectedComponentHulls.add(cHull.getHull());
            cHull = new quickHull();
        }

        //Find centre node of each cluster/ convex hull and re-order convex hull nodes in clockwise order
        for(int i =0; i<connectedComponents.size();i++){
            if(connectedComponents.get(i).size() > 3){
                centroid cen = new centroid();
                cen.findCentroid(connectedComponents.get(i),connectedComponentHulls.get(i));
                Collections.sort(connectedComponentHulls.get(i), new PointComp(cen.getCentroid()));
            }
        }

        //Build and save polygons using the re-ordered convex hull nodes
        createKMLPolygon kmlPoly = new createKMLPolygon(path,"convexHulls2");
        for(ArrayList<BattleNode> convexHull: connectedComponentHulls) {
            kmlPoly.buildPolygon(convexHull);
            kmlPoly.savePolygons();
        }

    final long endTime = System.currentTimeMillis();
    System.out.println("Total execution time: " + (endTime - startTime)/1000+"s");
    }

    public static DataFrame<Integer, String> buildDataFrame(String path, String dataFile) {
        DataFrame<Integer, String> frame = DataFrame.read().csv(path + dataFile + ".csv");
        return frame;
    }

}

class PointComp implements Comparator<BattleNode> {

    private java.awt.Point centre;

    public PointComp(java.awt.Point C) {
        this.centre = C;
    }
    
    @Override
    public int compare(BattleNode o1, BattleNode o2) {
        double angle1 = Math.atan2(o1.getPoint().getY() - centre.getY(),
                o1.getPoint().getX() - centre.getX());
        double angle2 = Math.atan2(o2.getPoint().getY() - centre.getY(),
                o2.getPoint().getX() - centre.getX());
        if (angle1 > angle2) {
            return 1;
        }
        else if (angle1 < angle2) {
            return -1;
        }
        else{
            return 0;
        }

    }
}



