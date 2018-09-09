package fr.mbutton.prometheus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("metrics")
public class KafkaCounterExporter {

	static String counterValue = "0";

	@GET
	public String getCounter() {
		return "counter " + counterValue;
	}
}