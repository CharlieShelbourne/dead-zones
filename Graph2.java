import com.zavtech.morpheus.frame.DataFrame;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Graph2 {
    private ArrayList<ArrayList<BattleNode>> nodeMatrix;
    double gridSpacings;
    int xDimension;
    int yDimension;

    Graph2(double gridSpacings, DataFrame<Integer,String> df){
        this.gridSpacings = gridSpacings;

        this.xDimension = (int)(180/gridSpacings);
        this.yDimension = (int)(360/gridSpacings);
        nodeMatrix = new ArrayList<>();
        for (int i = 0; i < xDimension; i++) {
            ArrayList<BattleNode> col = new ArrayList<>();
            for (int j = 0; j < yDimension; j++) {
                col.add(new BattleNode());
            }
            nodeMatrix.add(col);
        }

        df.rows().forEach(row -> {
            final double latitude = row.getDouble("Lat");
            final double longitude = row.getDouble("Lon");
            int indexLat = (int) (latitude + 90-(gridSpacings/2));
            int indexLon = (int) (longitude + 180-(gridSpacings/2));

            nodeMatrix.get(indexLat).get(indexLon).setGlobeLocation(latitude, longitude);
        });
    }

    public void setDeadNodes(DataFrame<Integer,String> df){
        df.rows().forEach(row -> {
            final double latitude = row.getDouble("Lat");
            final double longitude = row.getDouble("Lon");
            int indexLat = (int) (latitude + 90-(gridSpacings/2));
            int indexLon = (int) (longitude + 180-(gridSpacings/2));
            nodeMatrix.get(indexLat).get(indexLon).hit();
        });
    }
    public ArrayList<ArrayList<BattleNode>> getNodeMatrix(){
        return nodeMatrix;
    }

    public void setNeighbours(DataFrame<Integer,String> df){
        AtomicInteger count = new AtomicInteger();
        df.rows().forEach(row -> {
            final double latitude = row.getDouble("Lat");
            final double longitude = row.getDouble("Lon");
            int indexLat = (int) (latitude +  90-(gridSpacings/2));
            int indexLon = (int) (longitude + 180-(gridSpacings/2));

            count.set(count.get() + 1);
            int lat1 = -1;
            int lat2 = 1;
            int lon1 = -1;
            int lon2 = 1;

            if (indexLat == 0 || indexLon == 0 || indexLat == xDimension -1 || indexLon == yDimension -1) {
                if (indexLat == 0) {
                    //lat1 = 180 - 1;
                    lat1 =0;
                } else if (indexLat == xDimension -1) {
                    //lat2 = -179;
                    lat2 = 0;
                }
                if (indexLon == 0){
                    //lon1 = 360 - 1;
                    lon1 =0;
                }else if (indexLon == yDimension -1){
                    //lon2 = -359;
                    lon2 = 0;
                }
            }

            int rowNbr[] = {lat1, lat1, lat1, 0, 0, lat2, lat2, lat2};
            int colNbr[] = {lon1, 0, lon2, lon1, lon2, lon1, 0, lon2};

            //int rowNbr[] = {lat1, 0, 0, lat2};
            //int colNbr[] = {0, lon1, lon2, 0};


            for(int k =0; k<rowNbr.length;k++) {
                BattleNode B = nodeMatrix.get(indexLat).get(indexLon);
                BattleNode B2 = nodeMatrix.get(indexLat + rowNbr[k]).get(indexLon + colNbr[k]);
                boolean flag = false;

                if(B.getNeighbours() != null) {
                    for (BattleNode neighbour : B.getNeighbours()) {
                        if (B2.getPoint().getX() == neighbour.getPoint().getX() &&
                                B2.getPoint().getY() == neighbour.getPoint().getY()) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        B.addNeighbour(B2);
                    }
                }else{
                    B.addNeighbour(B2);
                }
            }
        });
    }
}
