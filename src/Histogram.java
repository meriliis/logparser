import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Histogram extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parameters parameters = getParameters();
        List<String> rawArguments = parameters.getRaw();
        String filename = rawArguments.get(0);

        stage.setTitle("Hourly Number of Requests");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
        bc.setTitle("Hourly Number of Requests");
        xAxis.setLabel("Date");
        yAxis.setLabel("Number of Requests");

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            ArrayList<Map.Entry<String, Integer>> dataList = createSortedDataList(br);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            bc.getData().add(series);

            for (Map.Entry<String, Integer> dataEntry : dataList) {
                bc.getData().get(0).getData().add(new XYChart.Data<String, Number>(dataEntry.getKey(), dataEntry.getValue()));
                bc.getData().get(0).getData().forEach(d -> d.getNode().setStyle("-fx-bar-fill: #595959"));
            }

            bc.setBarGap(1);
            bc.setLegendVisible(false);
            bc.setVerticalGridLinesVisible(false);
            Scene scene = new Scene(bc, 800, 600);
            stage.setScene(scene);
            stage.show();
        }
    }

    public ArrayList<Map.Entry<String, Integer>> createSortedDataList(BufferedReader br) throws IOException {
        String line = br.readLine();
        Map<String, Integer> data = new HashMap<>();
        ArrayList<String> dates = new ArrayList<>();
        while (line != null) {
            String time = line.split(":")[0];
            String date = time.substring(0, 10);
            if (data.containsKey(time)) {
                data.put(time, data.get(time) + 1);
            } else {
                data.put(time, 1);
            }

            if (!dates.contains(date)) {
                dates.add(date);
            }
            line = br.readLine();
        }

        // I case some hours didn't have any requests I add the 0 value anyways.
        for (String date : dates) {
            for (int i = 0; i < 24; i++) {
                String time = "";
                if (i < 10) {
                    time = date + " 0" + i;
                } else {
                    time = date + " " + i;
                }
                if (!data.containsKey(time)) {
                    data.put(time, 0);
                }
            }
        }


        ArrayList<Map.Entry<String, Integer>> dataList = new ArrayList<Map.Entry<String, Integer>>(data.entrySet());
        Collections.sort(dataList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        return dataList;
    }

}
