import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.frame.DataFrameValue;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class dataPreProcess {

    public static DataFrame<Integer, String> filterDuds(DataFrame<Integer, String> df){//, String dateRange) {

        DataFrame<Integer, String> filter = df.rows().select(row -> {
            final double sightings = row.getInt("Count");
            return sightings != 0;
        });

        filter.colAt(8).forEach(row -> row.setValue("shipsDetected"));

        return filter;
    }

    public static DataFrame<Integer, String> findDeadGridPoints(DataFrame<Integer, String> inputData,
                                                                DataFrame<Integer, String> worldGridData) {

        List<?> worldLatValues = worldGridData.colAt(0).values().map(DataFrameValue::getValue)
                .collect(Collectors.toList());
        List<?> worldLonValues = worldGridData.colAt(1).values().map(DataFrameValue::getValue)
                .collect(Collectors.toList());

        List<?> latValues = worldGridData.colAt(0).values().map(DataFrameValue::getValue)
                .collect(Collectors.toList());
        List<?> lonValues = worldGridData.colAt(1).values().map(DataFrameValue::getValue)
                .collect(Collectors.toList());

        inputData.rows().forEach(row -> {
            double latVal = row.getDouble(0);
            double lonVal = row.getDouble(1);

            if (worldLatValues.contains(latVal)) {
                int indStart = worldLatValues.indexOf(latVal);
                int indEnd = worldLatValues.lastIndexOf(latVal);

                int indRemove = worldLonValues.subList(indStart, indEnd).indexOf(lonVal);
                indRemove = indRemove + indStart;
                worldLonValues.remove(indRemove);
                worldLatValues.remove(indRemove);
            }
        });

        List<Integer> indexRange = new ArrayList<Integer>();
        for (int i = 0; i <= worldLatValues.size() + 1; i++) {
            indexRange.add(i);
        }

        DataFrame<Integer, String> deadGridPoints = DataFrame.of(indexRange, String.class, columns -> {
            columns.add("Lat", worldLatValues);
            columns.add("Lon", worldLonValues);
        });
        return deadGridPoints;
    }


    public static DataFrame<Integer, String> removeLandCoordinates(
            DataFrame<Integer, String> df, List<List<List<ArrayList<ArrayList<Double>>>>> polygon) {

        GeometryFactory gf = new GeometryFactory();

        for (List<ArrayList<ArrayList<Double>>> coordinatesList : polygon.get(0)) {
            for (ArrayList<ArrayList<Double>> coordinates : coordinatesList) {
                if (coordinates.size() > 0) {
                    ArrayList<Double> lonList = coordinates.get(1);
                    ArrayList<Double> latList = coordinates.get(0);
                    ArrayList<Coordinate> geoCoordinateList = new CoordinateList();
                    for (int i = 0; i < lonList.size(); i++) {
                        geoCoordinateList.add(new Coordinate(lonList.get(i), latList.get(i)));
                    }
                    Coordinate[] coordinateArray = ((CoordinateList) geoCoordinateList).toCoordinateArray();
                    LinearRing jtsRing = gf.createLinearRing(coordinateArray);
                    Polygon poly = gf.createPolygon(jtsRing, null);

                    DataFrame<Integer, String> filter = df.rows().select(row -> {
                        final double longitude = row.getDouble("Lon");
                        final double latitude = row.getDouble("Lat");
                        Coordinate coord = new Coordinate(longitude, latitude);
                        Point pt = gf.createPoint(coord);

                        return (poly.contains(pt) == false);
                    });
                    df = filter;
                }
            }
        }

        return df;
    }
}


