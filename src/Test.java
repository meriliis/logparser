// Name of the file    :: Test.java
// created by          :: Meri Liis Treimann
// Date                :: 19/02/2016
// Description         :: test assignment
// To compile          :: javac Test.java
// To execute          :: java Test <log file> <n>, where n specifies the number of top resources printed out (based on average request duration)

import javafx.application.Application;

import java.io.*;
import java.util.*;


public class Test {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        String filename = args[0];

        if (args[0].equals("-h") || args.length != 2) {
            help();
        } else {
            int n = Integer.parseInt(args[1]);

            ArrayList<Request> requests = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line = br.readLine();
                while (line != null) {
                    requests.add(createRequest(line));
                    line = br.readLine();
                }

                // TODO: fix wrong conditional
                if (n > requests.size()) {
                    System.out.println("Log file has less than n requests (" + requests.size() + ").");
                    help();
                } else {
                    ArrayList<Map.Entry<String, Double>> averageRequestDurations = averageRequestDurations(requests);
                    for (int i = 0; i < n; i++) {
                        Map.Entry<String, Double> resource = averageRequestDurations.get(i);
                        System.out.println(resource.getKey() + "\t" + resource.getValue() + "ms");
                    }
                }

                Application.launch(Histogram.class, filename);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println();
                help();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println();
                help();
            }

        }

        long runningTime = System.currentTimeMillis() - startTime;
        System.out.println("\n Program ran " + runningTime + "ms.");
    }

    static void help() {
        try (BufferedReader br = new BufferedReader(new FileReader("help.txt"))) {
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Request createRequest(String line) {
        Request request;
        String[] requestInformation = line.split("\\s+");
        String date = requestInformation[0];
        String timestamp = requestInformation[1];
        String threadID = requestInformation[2];
        String userContext = requestInformation[3];

        if (requestInformation.length == 7) {
            String URI_query = requestInformation[4];
            String URI = URI_query.split("\\?")[0];
            int duration = Integer.parseInt(requestInformation[6]);
            request = new Request(date, timestamp, threadID, userContext, URI_query, duration);
            request.URI = URI;
        } else {
            String resource = requestInformation[4];
            String dataPayload;                             // Special treatment for data payload: data payload may be empty or contain whitespace.
            if (requestInformation[5].equals("in")) {       // Case when data payload is 0 elements long the 5th element of the row is "in" which we do not want the data payload to be.
                dataPayload = null;
            } else {                                        // In any other case, data payload is everything from 5th element to "in", "in" excluded.
                dataPayload = "";
                for (int i = 5; i < requestInformation.length - 2; i++) {
                    dataPayload += requestInformation[i] + " ";
                }
                dataPayload.trim();
            }
            int duration = Integer.parseInt(requestInformation[requestInformation.length - 1]);
            request = new Request(date, timestamp, threadID, userContext, resource, dataPayload, duration);
        }

        return request;

    }

    public static ArrayList<Map.Entry<String, Double>> averageRequestDurations(ArrayList<Request> requests) {
        HashMap<String, ArrayList<Integer>> requestDurations = new HashMap<>();
        String resource = "";
        for (Request r : requests) {
            if (r.URI == null) {
                resource = r.resource;
            } else {
                resource = r.URI;
            }

            if (requestDurations.containsKey(resource)) {
                requestDurations.get(resource).add(r.duration);
            } else {
                ArrayList<Integer> durations = new ArrayList<>();
                durations.add(r.duration);
                requestDurations.put(resource, durations);
            }
        }

        HashMap<String, Double> averageRequestDurationsMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : requestDurations.entrySet()) {
            int sumDuration = 0;
            for (Integer duration : entry.getValue()) {
                sumDuration += duration;
            }
            double averageDuration = (double) sumDuration / entry.getValue().size();
            averageRequestDurationsMap.put(entry.getKey(), (double) Math.round(averageDuration * 100) / 100);

        }

        ArrayList<Map.Entry<String, Double>> averageRequestDurations = new ArrayList<Map.Entry<String, Double>>(averageRequestDurationsMap.entrySet());
        Collections.sort(averageRequestDurations, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        return averageRequestDurations;
    }
}


