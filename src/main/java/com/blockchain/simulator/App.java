package com.blockchain.simulator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;

/**
 * The main app for parsing config and initiating call
 *
 */
public class App
{
    public static void main( String[] args )
            throws IOException, ParseException, IllegalArgumentException
    {
        System.out.println("Hello\n");
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(args[0]);
        JSONObject jsonObj = (JSONObject) parser.parse(fileReader);
        if (!jsonObj.containsKey("protocol")) {
            throw new IllegalArgumentException("Should specify protocol in config");
        }
        final String protocol = jsonObj.get("protocol").toString();
        if (protocol.equals("dolev_strong")) {
            JSONObject dolevStrongSJON = (JSONObject) jsonObj.get("dolev_strong_config");
            if (!dolevStrongSJON.containsKey("round")) {
                throw new IllegalArgumentException("Dolev Strong protocol's round argument should be specified");
            }
            if (!dolevStrongSJON.containsKey("num_corrupt_player")) {
                throw new IllegalArgumentException("Dolev Strong protocol's num_corrupt_player should be specified");
            }
            if (!dolevStrongSJON.containsKey("num_total_player")) {
                throw new IllegalArgumentException("Dolev Strong protocol's num_total_player should be specified");
            }
            if (!dolevStrongSJON.containsKey("sender")) {
                throw new IllegalArgumentException("Dolev Strong protocol's sender should be specified");
            }
            if (!dolevStrongSJON.containsKey("initial_bit")) {
                throw new IllegalArgumentException("Dolev Strong protocol's initial_bit should be specified");
            }

            DolevStrongConfig config = new DolevStrongConfig(
                    Integer.parseInt(dolevStrongSJON.get("round").toString()),
                    Integer.parseInt(dolevStrongSJON.get("num_corrupt_player").toString()),
                    Integer.parseInt(dolevStrongSJON.get("num_total_player").toString()),
                    Integer.parseInt(dolevStrongSJON.get("sender").toString()),
                    Integer.parseInt(dolevStrongSJON.get("initial_bit").toString())
            );

            DolevStrongRoundSimulator simulator = new DolevStrongRoundSimulator(config);
            simulator.run();
        } else if (protocol.equals("streamlet")) {

        } else {
            throw new IllegalArgumentException("Protocol in Config is not implemented");
        }
    }
}
