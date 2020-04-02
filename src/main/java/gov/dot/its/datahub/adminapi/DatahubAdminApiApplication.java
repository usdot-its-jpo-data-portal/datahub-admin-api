package gov.dot.its.datahub.adminapi;

import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DatahubAdminApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DatahubAdminApiApplication.class);
		app.setLogStartupInfo(false);
		Banner banner = new Banner() {
			static final String MESSAGE_TEMPLATE = "%s = %s";
			static final String ES_TEMPLATE = "%s = %s://%s:%s";
			@Override
			public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
				out.println(new String(new char[80]).replace("\0", "_"));
				out.println("######                                                 #                          ");
				out.println("#     #   ##   #####   ##   #    # #    # #####       # #   #####  #    # # #    #");
				out.println("#     #  #  #    #    #  #  #    # #    # #    #     #   #  #    # ##  ## # ##   #");
				out.println("#     # #    #   #   #    # ###### #    # #####     #     # #    # # ## # # # #  #");
				out.println("#     # ######   #   ###### #    # #    # #    #    ####### #    # #    # # #  # #");
				out.println("#     # #    #   #   #    # #    # #    # #    #    #     # #    # #    # # #   ##");
				out.println("######  #    #   #   #    # #    #  ####  #####     #     # #####  #    # # #    #");
				out.println();
				out.println("DataHub Admin API");
				out.println();
				out.println(String.format(MESSAGE_TEMPLATE, "Port", environment.getProperty("server.port")));
				out.println(String.format(MESSAGE_TEMPLATE, "Origins", environment.getProperty("datahub.admin.api.origins")));
				String tk = environment.getProperty("datahub.admin.api.security.token.key").isEmpty() ? "<NOTVALID>" : environment.getProperty("datahub.admin.api.security.token.key");
				out.println(String.format(MESSAGE_TEMPLATE, environment.getProperty("datahub.admin.api.security.token.name"), tk ));
				out.println(String.format(
						ES_TEMPLATE, "Elasticsearch",
						environment.getProperty("datahub.admin.api.es.scheme"),
						environment.getProperty("datahub.admin.api.es.host"),
						environment.getProperty("datahub.admin.api.es.port")
						));
				out.println(String.format(MESSAGE_TEMPLATE, "Debug", environment.getProperty("datahub.admin.api.debug")));

				out.println(new String(new char[80]).replace("\0", "_"));
			}

		};
		app.setBanner(banner);
		app.run();
	}

}
