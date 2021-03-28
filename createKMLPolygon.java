import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class createKMLPolygon {

    private static String folderName;
    final Kml kml = new Kml();
    Folder folder;
    Document doc;
    String path;

    createKMLPolygon(String path, String folderName){
        this.path = path;
        this.folderName = folderName;
        createKmlFolder();
    }

    void buildPolygon(ArrayList<BattleNode> convexHull){
        LinearRing ring = new LinearRing();
        for(BattleNode node: convexHull){
            ring.addToCoordinates(node.getCoordinates().getY(),node.getCoordinates().getX());
        }
        Boundary boundary = new Boundary();
        boundary.setLinearRing(ring);

        Placemark placeMark = folder.createAndAddPlacemark();
        placeMark.withStyleUrl("#transRedPoly");

        placeMark.createAndSetPolygon().setOuterBoundaryIs(boundary);

        setPolygonColor(placeMark);
    }

    void setPolygonColor(Placemark placeMark){
        placeMark.withName("Polygon");
        placeMark.withDescription(placeMark.getName());
        List<StyleSelector> styleSelector = placeMark.getStyleSelector();
        Style style = new Style();
        style.setId("transRedPoly");
        style.createAndSetPolyStyle().withColor("7d0000ff").withColorMode(ColorMode.RANDOM);
        style.createAndSetLineStyle().withWidth(1.5);;

        styleSelector.add(style);

    }

    public void createKmlFolder() {
        this.doc = kml.createAndSetDocument().withName("Polygon_"+folderName).withOpen(true);

        // create a Folder
        this.folder = doc.createAndAddFolder();
        folder.withName(this.folderName).withOpen(true);


    }

    void savePolygons() throws FileNotFoundException {

        kml.marshal(new File(this.path +
                folderName + ".kml"));

    }

}
